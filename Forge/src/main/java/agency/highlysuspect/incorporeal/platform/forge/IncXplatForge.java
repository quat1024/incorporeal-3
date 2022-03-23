package agency.highlysuspect.incorporeal.platform.forge;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.block.entity.RedStringConstrictorBlockEntity;
import agency.highlysuspect.incorporeal.block.entity.RedStringLiarBlockEntity;
import agency.highlysuspect.incorporeal.IncItems;
import agency.highlysuspect.incorporeal.platform.IncBootstrapper;
import agency.highlysuspect.incorporeal.platform.IncXplat;
import agency.highlysuspect.incorporeal.platform.forge.block.entity.ForgeRedStringConstrictorBlockEntity;
import agency.highlysuspect.incorporeal.platform.forge.block.entity.ForgeRedStringLiarBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;

public class IncXplatForge implements IncXplat {
	@Override
	public IncBootstrapper createBootstrapper() {
		return new IncBootstrapForge();
	}
	
	@Override
	public CreativeModeTab createCreativeTab() {
		return new Tab();
	}
	
	private static class Tab extends CreativeModeTab {
		public Tab() {
			super(Inc.MODID);
			hideTitle();
			//forge pls. I do what i want
			//noinspection deprecation
			setBackgroundSuffix("incorporeal.png");
		}
		
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(IncItems.CREATIVE_MODE_TAB_ICON);
		}
		
		@Override
		public boolean hasSearchBar() {
			//Forge extension
			return true;
		}
	}
	
	@Override
	public DamageSource newDamageSource(String name) {
		return new DamageSource(name);
	}
	
	@Override
	public void sendTo(FriendlyByteBuf buf, ServerPlayer player) {
		IncForgeNetworking.EXTREMELY_SIMPLE_CHANNEL_CANT_YOU_SEE_HOW_SIMPLE_IT_IS.send(PacketDistributor.PLAYER.with(() -> player), buf);
	}
	
	@Override
	public BlockEntitySupplier<RedStringConstrictorBlockEntity> redStringConstrictorMaker() {
		return ForgeRedStringConstrictorBlockEntity::new;
	}
	
	@Override
	public BlockEntitySupplier<RedStringLiarBlockEntity> redStringLiarMaker() {
		return ForgeRedStringLiarBlockEntity::new;
	}
}
