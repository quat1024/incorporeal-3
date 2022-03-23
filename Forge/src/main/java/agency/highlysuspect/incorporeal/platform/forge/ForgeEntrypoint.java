package agency.highlysuspect.incorporeal.platform.forge;

import agency.highlysuspect.incorporeal.Inc;
import net.minecraftforge.fml.common.Mod;

@Mod("incorporeal")
public class ForgeEntrypoint {
	public ForgeEntrypoint() {
		Inc.INSTANCE.onInitialize();
	}
}
