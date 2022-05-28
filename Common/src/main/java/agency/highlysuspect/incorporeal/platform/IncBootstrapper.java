package agency.highlysuspect.incorporeal.platform;

/**
 * Each method in IncBootstrapper corresponds to one task that needs to be performed when starting the game.
 * They are called by Inc.onInitialize() and Inc.tryAfterBotaniaInitialization().
 * 
 * These could, obviously, be slapped into a giant "initialize()" method, instead of broken up like this.
 * The purpose of breaking up IncBootstrapper into many methods is to allow easy comparison of the Fabric and Forge implementations.
 */
public interface IncBootstrapper {
	ConfigBuilder createConfigBuilder(String filename);
	
	void registerBlocks();
	void registerBlockEntityTypes();
	void registerItems();
	void registerEntityTypes();
	void registerEntityAttributes();
	void registerSoundEvents();
	void registerServerToClientNetworkChannelSender();
	void registerCapabilities();
	void registerCommands();
	
	void endSelfInit();
	
	void registerCorporeaIndexCallback();
	void registerRedstoneRootPlaceEvent();
}
