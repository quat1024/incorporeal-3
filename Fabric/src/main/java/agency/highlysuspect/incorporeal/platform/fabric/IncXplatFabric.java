package agency.highlysuspect.incorporeal.platform.fabric;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.block.entity.RedStringConstrictorBlockEntity;
import agency.highlysuspect.incorporeal.block.entity.RedStringLiarBlockEntity;
import agency.highlysuspect.incorporeal.IncItems;
import agency.highlysuspect.incorporeal.platform.IncBootstrapper;
import agency.highlysuspect.incorporeal.platform.IncXplat;
import agency.highlysuspect.incorporeal.platform.fabric.block.entity.FabricRedStringConstrictorBlockEntity;
import agency.highlysuspect.incorporeal.platform.fabric.block.entity.FabricRedStringLiarBlockEntity;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class IncXplatFabric implements IncXplat {
	@Override
	public IncBootstrapper createBootstrapper() {
		return new IncBootstrapFabric();
	}
	
	@Override
	public CreativeModeTab createCreativeTab() {
		return FabricItemGroupBuilder.create(Inc.id("tab"))
			.icon(() -> new ItemStack(IncItems.CREATIVE_MODE_TAB_ICON))
			.build() //end of FabricItemGroupBuilder, beginning of vanilla CreativeModeTab
			.hideTitle()
			.setBackgroundSuffix("incorporeal.png");
	}
	
	@Override
	public DamageSource createDamageSource(String name) {
		//Protected constructor
		return new DamageSource(name) {};
	}
	
	@Override
	public void sendTo(FriendlyByteBuf buf, ServerPlayer player) {
		ServerPlayNetworking.send(player, IncBootstrapFabric.NETWORK_ID, buf);
	}
	
	@Override
	public BlockEntitySupplier<RedStringConstrictorBlockEntity> redStringConstrictorMaker() {
		return FabricRedStringConstrictorBlockEntity::new;
	}
	
	@Override
	public BlockEntitySupplier<RedStringLiarBlockEntity> redStringLiarMaker() {
		return FabricRedStringLiarBlockEntity::new;
	}
}
