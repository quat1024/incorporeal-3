package agency.highlysuspect.incorporeal.datagen;

import agency.highlysuspect.incorporeal.block.IncBlocks;
import agency.highlysuspect.incorporeal.item.IncItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.Blocks;
import vazkii.botania.common.lib.ModTags;

import java.util.function.Consumer;

public class IncCommonTagGen {
	public static void doIt(DataGenerator datagen, Consumer<JsonFile> files) {
		//rods
		DataDsl.itemTag(ModTags.Items.RODS)
			.addItems(IncItems.FRACTURED_SPACE_ROD)
			.save(files);
		
		//corporea spark override
		DataDsl.blockTag(ModTags.Blocks.CORPOREA_SPARK_OVERRIDE)
			.addBlocks(
				//things that aren't real inventories per se
				IncBlocks.RED_STRING_LIAR, IncBlocks.ENDER_SOUL_CORE,
				//for the "sparks on player heads" access control mechanic
				Blocks.PLAYER_HEAD, Blocks.PLAYER_WALL_HEAD,
				//literally no reason
				IncBlocks.CLEARLY
			).save(files);
		
		//functional special flowers
		DataDsl.blockAndItemTag(ModTags.Blocks.FUNCTIONAL_SPECIAL_FLOWERS).add(
			IncBlocks.FUNNY, IncBlocks.FUNNY_SMALL, IncBlocks.FLOATING_FUNNY, IncBlocks.FLOATING_FUNNY_SMALL,
			IncBlocks.SANVOCALIA, IncBlocks.SANVOCALIA_SMALL, IncBlocks.FLOATING_SANVOCALIA, IncBlocks.FLOATING_SANVOCALIA_SMALL
		).save(files);
		
		//mini flowers
		DataDsl.blockAndItemTag(ModTags.Blocks.MINI_FLOWERS).add(
			IncBlocks.FUNNY_SMALL, IncBlocks.FLOATING_FUNNY_SMALL,
			IncBlocks.SANVOCALIA_SMALL, IncBlocks.FLOATING_SANVOCALIA_SMALL
		).save(files);
		
		//special floating flowers
		DataDsl.blockAndItemTag(ModTags.Blocks.SPECIAL_FLOATING_FLOWERS).add(
			IncBlocks.FLOATING_FUNNY, IncBlocks.FLOATING_FUNNY_SMALL,
			IncBlocks.FLOATING_SANVOCALIA, IncBlocks.FLOATING_SANVOCALIA_SMALL
		).save(files);
	}
}
