package agency.highlysuspect.incorporeal.mixin.client;

import agency.highlysuspect.incorporeal.computer.DataFunnelBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import vazkii.botania.api.item.IWireframeCoordinateListProvider;
import vazkii.botania.common.item.ItemTwigWand;

import java.util.Collections;
import java.util.List;

/**
 * I am SOOOOOO SORRY for this one
 */
@Mixin(ItemTwigWand.class)
public class ItemTwigWandMixin implements IWireframeCoordinateListProvider {
	@Override
	public List<BlockPos> getWireframesToDraw(Player player, ItemStack stack) {
		Level level = player.level;
		
		HitResult pos = Minecraft.getInstance().hitResult;
		if (pos != null && pos.getType() == HitResult.Type.BLOCK) {
			if (level.getBlockEntity(((BlockHitResult) pos).getBlockPos()) instanceof DataFunnelBlockEntity dataFunnel) {
				//botania api requires a List, but all i have is a set!!!!!!
				return List.copyOf(dataFunnel.getBindings());
			}
		}
		
		return Collections.emptyList();
	}
}
