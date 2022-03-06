package agency.highlysuspect.incorporeal.block;

import agency.highlysuspect.incorporeal.CompressedTaterUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.common.block.decor.BlockTinyPotato;
import vazkii.botania.common.block.tile.TileTinyPotato;

import java.util.List;

public class CompressedTinyPotatoBlock extends BlockTinyPotato {
	public CompressedTinyPotatoBlock(int compressionLevel, Properties builder) {
		super(builder);
		this.compressionLevel = compressionLevel;
		
		float radius = CompressedTaterUtil.taterRadius(compressionLevel);
		float height = radius * 3;
		shape = Shapes.box(.5 - radius, 0, .5 - radius, .5 + radius, height, .5 + radius);
	}
	
	public final int compressionLevel;
	public final VoxelShape shape;
	
	public static final int TATERS_PER_COMPRESSION_LEVEL = 9; //todo maybe make this configurable lmao
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag piss) {
		tooltip.add(CompressedTaterUtil.formatCount(compressionLevel, TATERS_PER_COMPRESSION_LEVEL));
		
		super.appendHoverText(stack, level, tooltip, piss);
	}
	
	@NotNull
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
		return shape;
	}
	
	//Copy-paste from Botania with 1 change
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		BlockEntity tile = world.getBlockEntity(pos);
		if (tile instanceof TileTinyPotato tater) {
			tater.interact(player, hand, player.getItemInHand(hand), hit.getDirection());
			if (!world.isClientSide) {
				//AABB box = SHAPE.bounds(); //refers to the hardcoded default tiny potato shape. I need to change it
				AABB box = shape.bounds();
				((ServerLevel) world).sendParticles(ParticleTypes.HEART, pos.getX() + box.minX + Math.random() * (box.maxX - box.minX), pos.getY() + box.maxY, pos.getZ() + box.minZ + Math.random() * (box.maxZ - box.minZ), 1, 0, 0, 0, 0);
			}
		}
		return InteractionResult.SUCCESS;
	}
}
