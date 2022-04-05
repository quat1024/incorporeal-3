package agency.highlysuspect.incorporeal.client;

import agency.highlysuspect.incorporeal.item.TicketConjurerItem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class TicketConjurerHudHandler {
	/**
	 * Set to true when Botania draws its "Near Corporea Index!" overlay.
	 * If it's true, I move mine upwards so they don't overlap and look terrible.
	 * 
	 * @see agency.highlysuspect.incorporeal.mixin.client.HUDHandlerMixin
	 */
	public static boolean botaniaDrewNearIndexDisplay = false;
	
	public static void doIt(PoseStack ms, float partialTicks) {
		Minecraft mc = Minecraft.getInstance();
		if(!(mc.screen instanceof ChatScreen)) return;
		
		Player player = mc.player;
		if(player == null) return;
		
		ItemStack conjurer;
		if(player.getMainHandItem().getItem() instanceof TicketConjurerItem) conjurer = player.getMainHandItem();
		else if(player.getOffhandItem().getItem() instanceof TicketConjurerItem) conjurer = player.getOffhandItem();
		else return;
		
		String txt0 = I18n.get("incorporeal.ticket_conjurer.hold0");
		String txt1 = ChatFormatting.GRAY + I18n.get("incorporeal.ticket_conjurer.hold1");
		String txt2 = ChatFormatting.GRAY + I18n.get("incorporeal.ticket_conjurer.hold2");
		
		int l = Math.max(mc.font.width(txt0), Math.max(mc.font.width(txt1), mc.font.width(txt2))) + 20;
		int x = mc.getWindow().getGuiScaledWidth() - l - 20;
		int y = mc.getWindow().getGuiScaledHeight() - 60 - (botaniaDrewNearIndexDisplay ? 50 : 0);
		
		GuiComponent.fill(ms, x - 6, y - 6, x + l + 6, y + 37, 0x44000000);
		GuiComponent.fill(ms, x - 4, y - 4, x + l + 4, y + 35, 0x44000000);
		mc.getItemRenderer().renderAndDecorateItem(conjurer, x, y + 10);
		
		mc.font.drawShadow(ms, txt0, x + 20, y, 0xFFFFFF);
		mc.font.drawShadow(ms, txt1, x + 20, y + 14, 0xFFFFFF);
		mc.font.drawShadow(ms, txt2, x + 20, y + 24, 0xFFFFFF);
	}
}
