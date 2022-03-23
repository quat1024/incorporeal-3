package agency.highlysuspect.incorporeal.platform.forge.client;

import agency.highlysuspect.incorporeal.platform.IncClientBootstrapper;
import agency.highlysuspect.incorporeal.platform.IncClientXplat;

public class ClientXplatForge implements IncClientXplat {
	@Override
	public IncClientBootstrapper createBootstrapper() {
		return new ForgeClientBootstrapper();
	}
}
