package agency.highlysuspect.incorporeal.platform.forge;

import agency.highlysuspect.incorporeal.block.entity.RedStringConstrictorBlockEntity;
import agency.highlysuspect.incorporeal.block.entity.RedStringLiarBlockEntity;
import agency.highlysuspect.incorporeal.platform.IncXplat;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.network.PacketDistributor;

public class IncXplatForge implements IncXplat {
	@Override
	public CreativeModeTab getCreativeTab() {
		return IncForgeCreativeTab.INSTANCE;
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
