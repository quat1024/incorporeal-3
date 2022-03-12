package agency.highlysuspect.incorporeal.platform;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.block.entity.RedStringConstrictorBlockEntity;
import agency.highlysuspect.incorporeal.block.entity.RedStringLiarBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.ServiceLoader;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Incorporeal-specific cross-loader abstractions.
 * 
 * In the built mod, this `platform` package contains more platform-specific implementation details, such as
 * an implementation of IncXplat for the particular platform, and the main mod entrypoint.
 */
public interface IncXplat {
	IncXplat INSTANCE = get();
	
	private static IncXplat get() {
		//Literally pasted from Botania, lol
		List<ServiceLoader.Provider<IncXplat>> providers = ServiceLoader.load(IncXplat.class).stream().toList();
		if (providers.size() != 1) {
			throw new IllegalStateException("There should be exactly one IncXplat implementation on the classpath. Found: " + providers.stream().map(p -> p.type().getName()).collect(Collectors.joining(",", "[", "]")));
		} else {
			ServiceLoader.Provider<IncXplat> provider = providers.get(0);
			Inc.LOGGER.debug("Instantiating IncXplat impl: " + provider.type().getName());
			return provider.get();
		}
	}
	
	//One of those things that happens to be different across loaders
	CreativeModeTab getCreativeTab();
	
	//The named DamageSource constructor is not public on Fabric
	DamageSource newDamageSource(String name);
	
	//Send a block of data from server to client, using the incorporeal packet abstraction
	void sendTo(FriendlyByteBuf buf, ServerPlayer player);
	
	//Block entities implemented differently on both sides
	BlockEntitySupplier<RedStringLiarBlockEntity> redStringLiarMaker();
	BlockEntitySupplier<RedStringConstrictorBlockEntity> redStringConstrictorMaker();
	
	//Copy and paste from minecraft because its private
	@FunctionalInterface
	interface BlockEntitySupplier<T extends BlockEntity> {
		T create(BlockPos var1, BlockState var2);
	}
}
