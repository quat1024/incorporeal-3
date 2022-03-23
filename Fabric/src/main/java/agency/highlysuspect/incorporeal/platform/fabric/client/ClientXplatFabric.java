package agency.highlysuspect.incorporeal.platform.fabric.client;

import agency.highlysuspect.incorporeal.platform.IncClientBootstrapper;
import agency.highlysuspect.incorporeal.platform.IncClientXplat;

public class ClientXplatFabric implements IncClientXplat {
	@Override
	public IncClientBootstrapper createBootstrapper() {
		return new FabricClientBootstrapper();
	}
}
