package agency.highlysuspect.incorporeal.client;

import agency.highlysuspect.incorporeal.platform.IncClientBootstrapper;
import agency.highlysuspect.incorporeal.platform.IncClientXplat;

@SuppressWarnings("ClassCanBeRecord") //Private constructor pls
public class IncClient {
	public static IncClient INSTANCE = new IncClient(IncClientXplat.INSTANCE.createBootstrapper());
	
	private IncClient(IncClientBootstrapper bootstrapper) {
		this.bootstrapper = bootstrapper;
	}
	
	private final IncClientBootstrapper bootstrapper;
	
	public void onInitializeClient() {
		//platform-specific init
		bootstrapper.registerItemPropertyOverrides();
		bootstrapper.registerBlockRenderLayers();
		bootstrapper.registerColorProviders();
		
		bootstrapper.registerBlockEntityRenderers();
		bootstrapper.registerEntityRenderers();
		bootstrapper.registerItemRenderers();
		
		bootstrapper.registerClientCapabilities();
		bootstrapper.registerServerToClientNetworkChannelReceiver();
		
		//common init
		IncClientNetwork.initialize();
	}
}
