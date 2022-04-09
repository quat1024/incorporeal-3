package agency.highlysuspect.incorporeal.datagen;

import agency.highlysuspect.incorporeal.IncBlocks;
import agency.highlysuspect.incorporeal.IncItems;
import agency.highlysuspect.incorporeal.IncTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import vazkii.botania.common.lib.ModTags;

import java.util.function.Consumer;

public class IncCommonTagGen {
	public static void doIt(DataGenerator datagen, Consumer<JsonFile> files) {
		//data viewers
		DataDsl.itemTag(IncTags.Items.DATA_VIEWERS)
			.addItems(IncItems.DATA_MONOCLE)
			.save(files);
		
		///
		
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
				//let you place sparks on the hacky-as-hell corporea pylon
				IncBlocks.CORPOREA_PYLON,
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
		
		//burst viewers (Manaseer Monocle-like items)
		DataDsl.itemTag(ModTags.Items.BURST_VIEWERS)
			.addItems(IncItems.DATA_MONOCLE)
			.save(files);
		
		//mineability
		DataDsl.blockTag(BlockTags.MINEABLE_WITH_PICKAXE).addBlocks(
			IncBlocks.CORPOREA_SOLIDIFIER,
			IncBlocks.RED_STRING_LIAR,
			IncBlocks.RED_STRING_CONSTRICTOR,
			IncBlocks.FRAME_TINKERER,
			IncBlocks.CORPOREA_PYLON,
			
			IncBlocks.ENDER_SOUL_CORE,
			IncBlocks.POTION_SOUL_CORE,
			
			//natural devices -> left out (instabreak)
			//functional flowers -> left out
			
			IncBlocks.CLEARLY,
			IncBlocks.DATA_FUNNEL,
			IncBlocks.DATASTONE_BLOCK,
			IncBlocks.POINTED_DATASTONE
		).addBlocks(IncBlocks.UNSTABLE_CUBES.values())
			//tatoes -> left out because botania does too
			//(and also because i think their long breaking times are funny)
			.save(files);
	}
}
