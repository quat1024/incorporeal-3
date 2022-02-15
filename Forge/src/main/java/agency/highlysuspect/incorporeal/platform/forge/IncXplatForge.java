package agency.highlysuspect.incorporeal.platform.forge;

import agency.highlysuspect.incorporeal.platform.IncXplat;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.Item;
import net.minecraftforge.network.PacketDistributor;

public class IncXplatForge implements IncXplat {
	@Override
	public Item.Properties defaultItemProperties() {
		return new Item.Properties().tab(IncForgeCreativeTab.INSTANCE);
	}
	
	@Override
	public DamageSource newDamageSource(String name) {
		return new DamageSource(name);
	}
	
	@Override
	public void sendTo(FriendlyByteBuf buf, ServerPlayer player) {
		IncForgeNetworking.EXTREMELY_SIMPLE_CHANNEL_CANT_YOU_SEE_HOW_SIMPLE_IT_IS.send(PacketDistributor.PLAYER.with(() -> player), buf);
	}
}
