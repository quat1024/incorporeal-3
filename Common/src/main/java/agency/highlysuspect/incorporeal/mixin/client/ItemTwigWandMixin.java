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
import vazkii.botania.api.item.IWireframeCoordinateListProvider;
import vazkii.botania.common.item.ItemTwigWand;

import java.util.ArrayList;
import java.util.List;

/**
 * I am SOOOOOO SORRY for this one
 */
@Mixin(ItemTwigWand.class)
public class ItemTwigWandMixin implements IWireframeCoordinateListProvider {
	@Override
	public List<BlockPos> getWireframesToDraw(Player player, ItemStack stack) {
		Level level = player.level;
		
		List<BlockPos> result = new ArrayList<>();
		
		Multibindable bindAttempt = (Multibindable) ItemTwigWand.getBindingAttempt(stack)
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
