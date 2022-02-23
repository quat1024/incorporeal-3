package agency.highlysuspect.incorporeal.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

/**
 * The base class of the "natural devices".
 */
public abstract class CrappyDiodeBlock extends DiodeBlock {
	public CrappyDiodeBlock(Properties props) {
		super(props);
		
		registerDefaultState(defaultBlockState()
			.setValue(DiodeBlock.POWERED, false)
			.setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH));
	}
	
	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		//In Forge this used to plug into IPlantable, but noone gives a shit about iplantable.
		//Really awful interface to implement via mixin/xplat anyways.
		//Maybe toss some more tags in here? (DIRT contains grass blocks, btw.)
		BlockPos soilPos = pos.below();
		
		Block block = level.getBlockState(soilPos).getBlock();
		return block == Blocks.FARMLAND || BlockTags.DIRT.contains(block);
	}
	
	//Copied from several blocks that are attached to things, like BushBlock and TorchBlock.
	//No idea why moyango doesn't properly abstract this instead of pasting it everywhere. Whatever.
	public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
		return !$$0.canSurvive($$3, $$4) ? Blocks.AIR.defaultBlockState() : super.updateShape($$0, $$1, $$2, $$3, $$4, $$5);
	}
	
	//Here is where I put things common to repeaters and comparators that Mojang copypastes between the two, instead of
	//properly organizing them into DiodeBlock.
	
	@Override
	protected int getDelay(BlockState blockState) {
		return 20;
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
		//If you don't add POWERED, several diodeblock things will crash.
		//If you don't add FACING, several HorizontalDirectionBlock things will crash.
		//Mojang this is what inheritance is for. THis is literally what inheritance is allllll about
		//Why
		super.createBlockStateDefinition($$0.add(DiodeBlock.POWERED, HorizontalDirectionalBlock.FACING));
	}
}
