package agency.highlysuspect.incorporeal.mixin;

import agency.highlysuspect.incorporeal.block.IncBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import vazkii.botania.common.item.ModItems;

/**
 * Replaces Botania's otherwise plain Redstone Root item, with a BlockItem that places the redstone root crop.
 */
@Mixin(ModItems.class)
public abstract class ModItemsMixin {
	@Shadow(remap = false) @Final @Mutable public static Item redstoneRoot;
	
	@Shadow(remap = false)
	@NotNull
	@SuppressWarnings("ConstantConditions") //Lol
	public static Item.Properties defaultBuilder() {
		return null;
	}
	
	static {
		redstoneRoot = new BlockItem(IncBlocks.REDSTONE_ROOT_CROP, defaultBuilder());
		Item.BY_BLOCK.put(IncBlocks.REDSTONE_ROOT_CROP, redstoneRoot);
	}
}
