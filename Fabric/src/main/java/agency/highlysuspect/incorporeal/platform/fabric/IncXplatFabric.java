package agency.highlysuspect.incorporeal.platform.fabric;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.block.entity.RedStringConstrictorBlockEntity;
import agency.highlysuspect.incorporeal.block.entity.RedStringLiarBlockEntity;
import agency.highlysuspect.incorporeal.IncItems;
import agency.highlysuspect.incorporeal.platform.IncBootstrapper;
import agency.highlysuspect.incorporeal.platform.IncXplat;
import agency.highlysuspect.incorporeal.platform.fabric.block.entity.FabricRedStringConstrictorBlockEntity;
import agency.highlysuspect.incorporeal.platform.fabric.block.entity.FabricRedStringLiarBlockEntity;
import agency.highlysuspect.incorporeal.platform.fabric.mixin.FabricAccessorDamageSource;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.impl.item.group.ItemGroupExtensions;
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
		return new Tab();
	}
	
	private static class Tab extends CreativeModeTab {
		public Tab() {
			super(computeIndex(), Inc.MODID);
			hideTitle();
			setBackgroundSuffix("incorporeal.png");
		}
		
		private static int computeIndex() {
			((ItemGroupExtensions) CreativeModeTab.TAB_BUILDING_BLOCKS).fabric_expandArray();
			return CreativeModeTab.TABS.length - 1;
		}
		
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(IncItems.CREATIVE_MODE_TAB_ICON);
		}
	}
	
	@Override
	public DamageSource createDamageSource(String name) {
		return FabricAccessorDamageSource.inc$new(name);
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
