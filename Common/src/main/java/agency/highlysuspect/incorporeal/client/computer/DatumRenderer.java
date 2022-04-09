package agency.highlysuspect.incorporeal.client.computer;

import agency.highlysuspect.incorporeal.computer.types.Datum;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;

public interface DatumRenderer<T> {
	void drawCentered(Minecraft mc, PoseStack pose, float partialTicks, Datum<T> datum);
}
