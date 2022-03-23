package agency.highlysuspect.incorporeal.platform;

import agency.highlysuspect.incorporeal.util.ServiceHelper;

public interface IncClientXplat {
	IncClientXplat INSTANCE = ServiceHelper.loadSingletonService(IncClientXplat.class);
	
	//Thing that contains implementations for registering renderers, etc.
	IncClientBootstrapper createBootstrapper();
}
