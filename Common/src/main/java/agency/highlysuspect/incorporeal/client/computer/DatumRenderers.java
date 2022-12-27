package agency.highlysuspect.incorporeal.client.computer;

import agency.highlysuspect.incorporeal.computer.types.DataType;
import agency.highlysuspect.incorporeal.computer.types.DataTypes;
import agency.highlysuspect.incorporeal.computer.types.Datum;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;

public class DatumRenderers {
	public static final Map<DataType<?>, DatumRenderer<?>> renderers = new IdentityHashMap<>();
	
	public static <T> void register(DataType<T> type, DatumRenderer<T> renderer) {
		renderers.put(type, renderer);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> @Nullable DatumRenderer<T> getRenderer(Datum<T> datum) {
		return (DatumRenderer<T>) renderers.get(datum.type());
	}
	
	public static <T> void draw(Minecraft mc, PoseStack pose, float partialTicks, Datum<T> datum) {
		DatumRenderer<T> shit = getRenderer(datum);
		if(shit != null) shit.drawCentered(mc, pose, partialTicks, datum);
	}
	
	public static void registerBuiltinRenderers() {
		//sigh
		register(DataTypes.EMPTY, (mc, pose, partialTicks, datum) ->
			drawCentered(mc, pose, Component.translatable("incorporeal.empty_datum"), datum.color()));
		
		register(DataTypes.INTEGER, (mc, pose, partialTicks, datum) ->
			drawCentered(mc, pose, Component.translatable("incorporeal.integer_datum", datum.thing()), datum.color()));
		
		//i say this every time, but... these are temporary
		register(DataTypes.MATCHER, (mc, pose, partialTicks, datum) ->
			drawCentered(mc, pose, Component.translatable("incorporeal.matcher_datum", datum.thing().getRequestName()), datum.color()));
		
		register(DataTypes.SOLIDIFIED_REQUEST, (mc, pose, partialTicks, datum) ->
			drawCentered(mc, pose, Component.translatable("incorporeal.solidified_request_datum", datum.thing().toComponent()), datum.color()));
	}
	
	public static void drawCentered(Minecraft mc, PoseStack pose, Component yes, int color) {
		drawCentered(mc, pose, yes, 0, 0, color);
	}
	
	public static void drawCentered(Minecraft mc, PoseStack pose, Component yes, int x, int y, int color) {
		int halfWidth = mc.font.width(yes) / 2;
		
		GuiComponent.fill(pose, x - halfWidth - 4, y - 7, x + halfWidth + 4, y + 7, 0x44000000);
		GuiComponent.fill(pose, x - halfWidth - 2, y - 5, x + halfWidth + 2, y + 5, 0x44000000);
		
		mc.font.drawShadow(pose, yes, -halfWidth + x, y - 4, color);
	}
}
