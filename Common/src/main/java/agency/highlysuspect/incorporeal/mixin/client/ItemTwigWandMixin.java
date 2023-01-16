package agency.highlysuspect.incorporeal.mixin.client;

import agency.highlysuspect.incorporeal.block.entity.Multibindable;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import vazkii.botania.api.item.WireframeCoordinateListProvider;
import vazkii.botania.common.item.WandOfTheForestItem;

import java.util.ArrayList;
import java.util.List;

/**
 * I am SOOOOOO SORRY for this one
 */
@Mixin(WandOfTheForestItem.class)
public class ItemTwigWandMixin implements WireframeCoordinateListProvider {
	@Override
	public List<BlockPos> getWireframesToDraw(Player player, ItemStack stack) {
		Level level = player.level;
		
		List<BlockPos> result = new ArrayList<>();
		
		Multibindable bindAttempt = (Multibindable) WandOfTheForestItem.getBindingAttempt(stack)
			.map(level::getBlockEntity)
			.filter(be -> be instanceof Multibindable)
			.orElse(null);
		if(bindAttempt != null) result.addAll(bindAttempt.getBindings());
		
		HitResult pos = Minecraft.getInstance().hitResult;
		if(pos instanceof BlockHitResult bhr &&
			level.getBlockEntity(bhr.getBlockPos()) instanceof Multibindable multibindable &&
			multibindable != bindAttempt) result.addAll(multibindable.getBindings());
		
		return result;
	}
}
