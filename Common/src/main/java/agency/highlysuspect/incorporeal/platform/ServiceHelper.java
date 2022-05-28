package agency.highlysuspect.incorporeal.platform;

import agency.highlysuspect.incorporeal.Inc;

import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public class ServiceHelper {
	public static <T> T loadSingletonService(Class<T> serviceClass) {
		//Based on botania copy pasta
		List<ServiceLoader.Provider<T>> providers = ServiceLoader.load(serviceClass).stream().toList();
		if(providers.size() != 1) {
			String providersListMessage = providers.isEmpty() ? "None of them."
				: providers.stream().map(p -> p.type().getName()).collect(Collectors.joining(",", "[", "]"));
			
			throw new IllegalStateException("There should be exactly one %s implementation on the classpath. Found: %s".formatted(serviceClass.getSimpleName(), providersListMessage));
		} else {
			ServiceLoader.Provider<T> provider = providers.get(0);
			return provider.get();
		}
	}
}
