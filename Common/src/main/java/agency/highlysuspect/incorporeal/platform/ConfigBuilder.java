package agency.highlysuspect.incorporeal.platform;

import java.util.function.Supplier;

public interface ConfigBuilder {
	void pushCategory(String name);
	void popCategory();
	
	Supplier<Boolean> addBooleanProperty(String name, boolean defaultValue, String... commentLines);
	
	void finish();
}
