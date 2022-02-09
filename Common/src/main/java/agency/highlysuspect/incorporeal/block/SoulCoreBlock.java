package agency.highlysuspect.incorporeal.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.common.block.BlockMod;
import vazkii.botania.common.entity.EntityDoppleganger;

import java.util.function.Supplier;

public class SoulCoreBlock extends BlockMod implements EntityBlock {
	public SoulCoreBlock(Supplier<BlockEntityType<? extends AbstractSoulCoreBlockEntity>> typeS, Properties props) {
		super(props);
		this.typeS = typeS;
	}
	
	private final Supplier<BlockEntityType<? extends AbstractSoulCoreBlockEntity>> typeS;
	
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if(!EntityDoppleganger.isTruePlayer(player)) return InteractionResult.PASS;
		
		return level.getBlockEntity(pos) instanceof AbstractSoulCoreBlockEntity soul ? soul.activate(player, hand) : InteractionResult.PASS;
	}
	
	@Override
	public boolean hasAnalogOutputSignal(BlockState $$0) {
		return true;
	}
	
	@Override
	public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
		return level.getBlockEntity(pos) instanceof AbstractSoulCoreBlockEntity soul ? soul.signal() : 0;
	}
	
	@Override
	public RenderShape getRenderShape(BlockState $$0) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return typeS.get().create(pos, state);
	}
	
	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		if(level.isClientSide()) return null;
		else return createTickerHelper(type, typeS.get(), AbstractSoulCoreBlockEntity::serverTick);
	}
}
