package agency.highlysuspect.incorporeal.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import java.util.Random;

public class CrappyRepeaterBlock extends CrappyDiodeBlock {
	public CrappyRepeaterBlock(Properties props) {
		super(props);
		
		registerDefaultState(defaultBlockState()
			.setValue(L0CKED, false));
	}
	
	//using a different name, because one (1) time ever i was bitten by remapping
	//that fucked up if you included a field with the same name as an obf field.
	public static final BooleanProperty L0CKED = BlockStateProperties.LOCKED;
	//Yes that is a zero.
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
		super.createBlockStateDefinition($$0.add(L0CKED));
	}
	
	//Paste from RepeaterBlock
	public BlockState getStateForPlacement(BlockPlaceContext $$0) {
		BlockState $$1 = super.getStateForPlacement($$0);
		assert $$1 != null; //to clearn up a warning
		return $$1.setValue(L0CKED, this.isLocked($$0.getLevel(), $$0.getClickedPos(), $$1));
	}
	
	//Paste from RepeaterBlock
	public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
		return !$$3.isClientSide() && $$1.getAxis() != $$0.getValue(FACING).getAxis() ? $$0.setValue(L0CKED, this.isLocked($$3, $$4, $$0)) : super.updateShape($$0, $$1, $$2, $$3, $$4, $$5);
	}
	
	//Paste from RepeaterBlock
	public boolean isLocked(LevelReader $$0, BlockPos $$1, BlockState $$2) {
		return this.getAlternateSignal($$0, $$1, $$2) > 0;
	}
	
	//Paste from RepeaterBlock
	protected boolean isAlternateInput(BlockState $$0) {
		return isDiode($$0);
	}
	
	//Paste from RepeaterBlock modified to include a hardcoded particle distance
	public void animateTick(BlockState $$0, Level $$1, BlockPos $$2, Random $$3) {
		if ($$0.getValue(POWERED)) {
			Direction $$4 = $$0.getValue(FACING);
			double $$5 = (double)$$2.getX() + 0.5D + ($$3.nextDouble() - 0.5D) * 0.2D;
			double $$6 = (double)$$2.getY() + 0.4D + ($$3.nextDouble() - 0.5D) * 0.2D;
			double $$7 = (double)$$2.getZ() + 0.5D + ($$3.nextDouble() - 0.5D) * 0.2D;
			float $$8 = -5.0F;
			if ($$3.nextBoolean()) {
				//$$8 = (float)((Integer)$$0.getValue(DELAY) * 2 - 1);
				$$8 = 7;
			}
			
			$$8 /= 16.0F;
			double $$9 = $$8 * (float)$$4.getStepX();
			double $$10 = $$8 * (float)$$4.getStepZ();
			$$1.addParticle(DustParticleOptions.REDSTONE, $$5 + $$9, $$6, $$7 + $$10, 0.0D, 0.0D, 0.0D);
		}
	}
}
