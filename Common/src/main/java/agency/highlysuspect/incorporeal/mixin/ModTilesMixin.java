package agency.highlysuspect.incorporeal.mixin;

import agency.highlysuspect.incorporeal.block.IncBlocks;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.block.tile.ModTiles;

import java.util.stream.Stream;

@Mixin(ModTiles.class)
public class ModTilesMixin {
	/**
	 * Add my compressed tiny potato blocks as acceptable targets for the Tiny Potato block entity.
	 */
	@ModifyVariable(
		method = "type",
		at = @At("HEAD"),
		argsOnly = true
	)
	private static Block[] moreTaters(Block... blocks) {
		if(blocks.length != 0 && blocks[0] == ModBlocks.tinyPotato) {
			return Stream.concat(Stream.of(blocks), IncBlocks.COMPRESSED_TATERS.values().stream()).toArray(Block[]::new);
		} else return blocks;
	}
}
