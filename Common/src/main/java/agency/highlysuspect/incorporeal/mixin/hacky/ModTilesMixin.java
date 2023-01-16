package agency.highlysuspect.incorporeal.mixin.hacky;

import agency.highlysuspect.incorporeal.IncBlocks;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.block.block_entity.BotaniaBlockEntities;

import java.util.stream.Stream;

@Mixin(BotaniaBlockEntities.class)
public class ModTilesMixin {
	/**
	 * Add my compressed tiny potato blocks as acceptable targets for the Tiny Potato block entity.
	 */
	@ModifyVariable(
		method = "type",
		at = @At("HEAD"),
		argsOnly = true,
		remap = false
	)
	private static Block[] moreTaters(Block... blocks) {
		if(blocks.length != 0 && blocks[0] == BotaniaBlocks.tinyPotato) {
			return Stream.concat(Stream.of(blocks), IncBlocks.COMPRESSED_TATERS.values().stream()).toArray(Block[]::new);
		} else return blocks;
	}
}
