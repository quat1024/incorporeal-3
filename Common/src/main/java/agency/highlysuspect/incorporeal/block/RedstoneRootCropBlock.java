package agency.highlysuspect.incorporeal.block;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import vazkii.botania.common.item.ModItems;

/**
 * Incorporeal edits Botania's "redstone root" to be a plantable crop. This is the crop block.
 * 
 * @see agency.highlysuspect.incorporeal.mixin.ModItemsMixin for where this edit is done.
 */
public class RedstoneRootCropBlock extends CropBlock {
	public RedstoneRootCropBlock(Properties props) {
		super(props);
		
		registerDefaultState(defaultBlockState().setValue(AGE, 0));
	}
	
	//The default age prop goes to 7 so tbh i should bite the bullet and just draw another sprite lol
	public static final int MAX_AGE = 6;
	public static final IntegerProperty AGE = IntegerProperty.create("age", 0, MAX_AGE);
	public static final VoxelShape[] SHAPES = Util.make(new VoxelShape[MAX_AGE + 1], arr -> {
		for(int i = 0; i <= MAX_AGE; i++) {
			double yea = (MAX_AGE - i) / 32d;
			arr[i] = Shapes.create(yea, 0, yea, 1 - yea, 3/16d, 1 - yea);
		}
	});
	
	@Override
	public IntegerProperty getAgeProperty() {
		return AGE;
	}
	
	@Override
	public int getMaxAge() {
		return MAX_AGE;
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		return SHAPES[state.getValue(AGE)];
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		//super.createBlockStateDefinition(builder); //Adds the default age property with 7 parameters
		builder.add(AGE);
	}
	
	/// Things relating to the mixin crimes below
	
	@Override
	public Item asItem() {
		return ModItems.redstoneRoot;
	}
	
	//Weird cropblock thing
	@Override
	protected ItemLike getBaseSeedId() {
		return this;
	}
	
	//(super illegal)
	@Override
	public String getDescriptionId() {
		//Noone will ever know!
		return "item.botania.redstone_root";
	}
}
