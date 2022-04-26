package agency.highlysuspect.incorporeal.platform;

import agency.highlysuspect.incorporeal.block.entity.RedStringConstrictorBlockEntity;
import agency.highlysuspect.incorporeal.block.entity.RedStringLiarBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Incorporeal-specific cross-loader abstractions.
 * 
 * In the built mod, this `platform` package contains more platform-specific implementation details, such as
 * an implementation of IncXplat for the particular platform, and the main mod entrypoint.
 */
public interface IncXplat {
	IncXplat INSTANCE = ServiceHelper.loadSingletonService(IncXplat.class);
	
	//Thing that contains implementations for registering blocks, items, etc.
	//(Kept in a separate class mainly to keep IncXplat to a reasonable size.) 
	IncBootstrapper createBootstrapper();
	
	//TODO: Move to IncBootstrapper I think
	CreativeModeTab createCreativeTab();
	
	//The named DamageSource constructor is protected on Fabric
	DamageSource createDamageSource(String name);
	
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
