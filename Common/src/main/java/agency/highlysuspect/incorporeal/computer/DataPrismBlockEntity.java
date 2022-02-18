package agency.highlysuspect.incorporeal.computer;

import agency.highlysuspect.incorporeal.block.IncBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.block.IWandBindable;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.common.block.tile.TileMod;
import vazkii.botania.common.helper.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DataPrismBlockEntity extends TileMod implements IWandBindable {
	public DataPrismBlockEntity(BlockPos $$1, BlockState $$2) {
		super(IncBlockEntityTypes.DATA_PRISM, $$1, $$2);
	}
	
	private final List<BlockPos> bindTargets = new ArrayList<>();
	
	public void act(BlockPos me, Direction direction) {
		assert level != null;
		
		//TODO (HUUUUUUUGE TODO ITEM): allow raycasts at any angle instead of axis aligned only!!!
		// This is simpler to implement tho and I am seriously running out of time
		
		List<Object> collectedDatums = new ArrayList<>();
		for(BlockPos target : bindTargets) {
			//figure out which direction points from the target to myself
			BlockPos difference = me.subtract(target);
			int steps = difference.distManhattan(me) - 1;
			Direction towardsMe = Direction.getNearest(difference.getX(), difference.getY(), difference.getZ()); //might be backwards..!
			
			Object datum = null;
			BlockPos.MutableBlockPos cursor = target.mutable();
			for(int i = 0; i < steps; i++) {
				BlockEntity beHere = level.getBlockEntity(cursor);
				
				if(DataTypes.canExtractData(beHere)) datum = DataTypes.extractData(beHere);
				datum = DataTypes.filterData(level, cursor, datum);
				
				cursor.move(towardsMe);
			}
			
			if(datum != null) collectedDatums.add(datum);
		}
		
		//add all the pieces of data together (TODO)
		
		//send the resulting piece of data out the front of the block (TODO)
	}
	
	@Override
	public boolean canSelect(Player player, ItemStack wand, BlockPos pos, Direction side) {
		return true;
	}
	
	@Override
	public boolean bindTo(Player player, ItemStack wand, BlockPos target, Direction side) {
		//linear scan, yes
		if(bindTargets.contains(target)) {
			bindTargets.remove(target);
			setChanged();
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
			return true;
		}
		
		BlockPos me = getBlockPos();
		if(Objects.equals(me, target)) return false;
		if(MathHelper.distSqr(me, target) > 12 * 12) return false;
		
		//Only allow axis aligned binds for now.
		//TODO remove this restriction
		int matchingAxes = 0;
		if(me.getX() == target.getX()) matchingAxes++;
		if(me.getY() == target.getY()) matchingAxes++;
		if(me.getZ() == target.getZ()) matchingAxes++;
		if(matchingAxes != 2) return false;
		
		bindTargets.add(target);
		setChanged();
		VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
		return true;
	}
	
	int lol;
	
	@Nullable
	@Override
	public BlockPos getBinding() {
		//TODO: This is very silly
		if(bindTargets.size() == 0) return null;
		return bindTargets.get(lol++ % bindTargets.size());
	}
	
	@Override
	public void writePacketNBT(CompoundTag cmp) {
		ListTag list = new ListTag();
		for(BlockPos bindTarget : bindTargets) {
			list.add(NbtUtils.writeBlockPos(bindTarget));
		}
		cmp.put("Binds", list);
	}
	
	@Override
	public void readPacketNBT(CompoundTag cmp) {
		bindTargets.clear();
		ListTag list = cmp.getList("Binds", 10);
		for(Tag tagg : list) {
			if(!(tagg instanceof CompoundTag taggg)) continue;
			bindTargets.add(NbtUtils.readBlockPos(taggg));
		}
	}
}
