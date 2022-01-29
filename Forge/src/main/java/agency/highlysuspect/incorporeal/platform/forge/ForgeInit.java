package agency.highlysuspect.incorporeal.platform.forge;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.IncBlocks;
import agency.highlysuspect.incorporeal.IncItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Mod("incorporeal")
public class ForgeInit {
	public ForgeInit() {
		Inc.init();
		
		Inc.LOGGER.info("Hello from forge");
		
		bind(ForgeRegistries.BLOCKS, IncBlocks::registerBlocks);
		bind(ForgeRegistries.ITEMS, IncItems::registerItems);
	}
	
	//Botania copypasterino
	private static <T extends IForgeRegistryEntry<T>> void bind(IForgeRegistry<T> registry, Consumer<BiConsumer<T, ResourceLocation>> source) {
		FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(registry.getRegistrySuperType(),
			(RegistryEvent.Register<T> event) -> {
				IForgeRegistry<T> forgeRegistry = event.getRegistry();
				source.accept((t, rl) -> {
					t.setRegistryName(rl);
					forgeRegistry.register(t);
				});
			});
	}
}
