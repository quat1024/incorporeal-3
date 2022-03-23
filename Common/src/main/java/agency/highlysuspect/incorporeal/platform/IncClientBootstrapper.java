package agency.highlysuspect.incorporeal.platform;

public interface IncClientBootstrapper {
	void registerItemPropertyOverrides();
	void registerBlockRenderLayers();
	void registerColorProviders();
	
	void registerBlockEntityRenderers();
	void registerEntityRenderers();
	void registerItemRenderers();
	
	void registerClientCapabilities();
	void registerServerToClientNetworkChannelReceiver();
}
