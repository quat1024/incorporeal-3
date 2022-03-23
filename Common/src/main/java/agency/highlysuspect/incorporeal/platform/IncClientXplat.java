package agency.highlysuspect.incorporeal.platform;

public interface IncClientXplat {
	IncClientXplat INSTANCE = ServiceHelper.loadSingletonService(IncClientXplat.class);
	
	//Thing that contains implementations for registering renderers, etc.
	IncClientBootstrapper createBootstrapper();
}
