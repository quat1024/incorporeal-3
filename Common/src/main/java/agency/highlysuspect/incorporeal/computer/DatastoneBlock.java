package agency.highlysuspect.incorporeal.computer;

import agency.highlysuspect.incorporeal.IncBlocks;
import agency.highlysuspect.incorporeal.computer.types.Datum;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;

import java.util.ArrayList;
import java.util.List;

public class DatastoneBlock extends Block {
	public DatastoneBlock(Properties props) {
		super(props);
	}
	
	public void extendColumn(ServerLevel level, BlockPos pos, Datum<?> newDatum) {
		//Find the bottom of the pointed datastone column (or the block below myself, if there aren't any)
		List<PointedDatastoneBlockEntity> stones = new ArrayList<>();
		BlockPos.MutableBlockPos cursor = pos.mutable().move(Direction.DOWN);
		while(level.getBlockEntity(cursor) instanceof PointedDatastoneBlockEntity stone) {
			stones.add(stone);
			cursor.move(Direction.DOWN);
		}
		
		//cursor is now one past the end of the pointed datastones column, where a new one should go. Place it.
		//Ohhh my goddd there is so much song and dance here
		BlockPlaceContext ctx = new DirectionalPlaceContext(level, cursor.immutable(), Direction.DOWN, ItemStack.EMPTY, Direction.UP);
		BlockState stateThere = level.getBlockState(cursor);
		if(level.getBlockState(cursor).canBeReplaced(ctx) && level.isUnobstructed(stateThere, cursor, CollisionContext.empty()) && IncBlocks.POINTED_DATASTONE.canSurvive(stateThere, level, cursor)) {
			level.setBlockAndUpdate(cursor, IncBlocks.POINTED_DATASTONE.defaultBlockState());
			
			BlockEntity newBlockEntity = level.getBlockEntity(cursor);
			if(newBlockEntity instanceof PointedDatastoneBlockEntity newStone) stones.add(newStone);
		}
		
		//Copy each entry into the datastone below it
		if(!stones.isEmpty()) {
			for(int i = stones.size() - 1; i >= 1; i--) {
				PointedDatastoneBlockEntity above = stones.get(i - 1);
				PointedDatastoneBlockEntity below = stones.get(i);
				below.acceptDatum(above.readDatum());
			}
			//And insert the new data at the top
			stones.get(0).acceptDatum(newDatum);
		}
	}
	
	public Datum<?> retractColumn(ServerLevel level, BlockPos pos) {
		//Find the bottom of the pointed datastone column (or the block below myself, if there aren't any)
		List<PointedDatastoneBlockEntity> stones = new ArrayList<>();
		BlockPos.MutableBlockPos cursor = pos.mutable().move(Direction.DOWN);
		while(level.getBlockEntity(cursor) instanceof PointedDatastoneBlockEntity stone) {
			stones.add(stone);
			cursor.move(Direction.DOWN);
		}
		
		//If there are no pointed datastones, do not perform any removals
		if(stones.isEmpty()) return Datum.EMPTY;
		
		//Cursor is one past the end of the pointed datastones column.
		//Move the cursor to the datastone to be removed
		cursor.move(Direction.UP);
		
		//Move the data in the stones upwards
		Datum<?> toReturn = stones.get(0).readDatum();
		for(int i = 0; i < stones.size() - 1; i++) {
			PointedDatastoneBlockEntity above = stones.get(i);
			PointedDatastoneBlockEntity below = stones.get(i + 1);
			above.acceptDatum(below.readDatum());
		}
		
		//Remove the end of the datastone column
		IncBlocks.POINTED_DATASTONE.fallOrBreak(level, cursor);
		
		return toReturn;
	}
}
