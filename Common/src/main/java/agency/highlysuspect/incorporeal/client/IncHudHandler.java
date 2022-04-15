package agency.highlysuspect.incorporeal.client;

import agency.highlysuspect.incorporeal.block.RedStringConstrictorBlock;
import agency.highlysuspect.incorporeal.block.entity.RedStringConstrictorBlockEntity;
import agency.highlysuspect.incorporeal.item.TicketConjurerItem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class IncHudHandler {
	/**
	 * Set to true when Botania draws its "Near Corporea Index!" overlay.
	 * If it's true, I move mine upwards so they don't overlap and look terrible.
	 * 
	 * @see agency.highlysuspect.incorporeal.mixin.client.HUDHandlerMixin
	 */
	public static boolean botaniaDrewNearIndexDisplay = false;
	
	public static void doIt(PoseStack pose, float partialTicks) {
		Minecraft mc = Minecraft.getInstance();
		Level level = mc.level;
		Player player = mc.player;
		if(level == null || player == null) return;
		
		HitResult hr = mc.hitResult;
		if(hr instanceof BlockHitResult bhr) {
			BlockEntity hitBe = level.getBlockEntity(bhr.getBlockPos());
			
			if(hitBe instanceof RedStringConstrictorBlockEntity cons) {
				doRedStringConstrictorHud(mc, pose, partialTicks, cons);
			}
		}
		
		doTicketConjurerHud(mc, pose, partialTicks, player);
	}
	
	private static void doRedStringConstrictorHud(Minecraft mc, PoseStack pose, float partialTicks, RedStringConstrictorBlockEntity cons) {
		//i know this isnt the correct way to localize plurals but, rrrrreh
		Component txt = new TranslatableComponent(
			"incorporeal.red_string_constrictor.tooltip." +
				(cons.removesSlotsFromFront() ? "start" : "end") +
				(cons.removedSlotCount() == 1 ? "" : ".plural"),
			cons.removedSlotCount()
		);
		int strlen = mc.font.width(txt);
		
		//Sloppily copied from corporea crystal cubes which have a similar hud
		int w = mc.getWindow().getGuiScaledWidth();
		int h = mc.getWindow().getGuiScaledHeight();
		int boxH = h / 2 + 10;
		GuiComponent.fill(pose, w / 2 + 8, h / 2 - 12, w / 2 + strlen + 32, boxH, 0x44000000);
		GuiComponent.fill(pose, w / 2 + 6, h / 2 - 14, w / 2 + strlen + 34, boxH + 2, 0x44000000);
		//noinspection IntegerDivisionInFloatingPointContext
		mc.font.drawShadow(pose, txt, w / 2 + 30, h / 2 - 5, 0x6666FF);
	}
	
	private static void doTicketConjurerHud(Minecraft mc, PoseStack pose, float partialTicks, Player player) {
		if(!(mc.screen instanceof ChatScreen)) return;
		
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
		
		GuiComponent.fill(pose, x - 6, y - 6, x + l + 6, y + 37, 0x44000000);
		GuiComponent.fill(pose, x - 4, y - 4, x + l + 4, y + 35, 0x44000000);
		mc.getItemRenderer().renderAndDecorateItem(conjurer, x, y + 10);
		
		mc.font.drawShadow(pose, txt0, x + 20, y, 0xFFFFFF);
		mc.font.drawShadow(pose, txt1, x + 20, y + 14, 0xFFFFFF);
		mc.font.drawShadow(pose, txt2, x + 20, y + 24, 0xFFFFFF);
	}
}
