package agency.highlysuspect.incorporeal.computer.types;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import agency.highlysuspect.incorporeal.util.SimplerRegistry;
import net.minecraft.util.Unit;
import vazkii.botania.api.corporea.ICorporeaRequestMatcher;

public class DataTypes {
	public static final SimplerRegistry<DataType<?>> REGISTRY = new SimplerRegistry<>();
	
	public static final DataType<Unit> EMPTY = new EmptyType();
	public static final DataType<Integer> INTEGER = new IntegerType();
	public static final DataType<ICorporeaRequestMatcher> MATCHER = new CorporeaRequestMatcherType();
	public static final DataType<SolidifiedRequest> SOLIDIFIED_REQUEST = new SolidifiedRequestType();
	
	public static void registerBuiltinTypes() {
		REGISTRY.register(EMPTY, Inc.id("empty"));
		REGISTRY.register(INTEGER, Inc.id("integer"));
		REGISTRY.register(MATCHER, Inc.id("matcher"));
		REGISTRY.register(SOLIDIFIED_REQUEST, Inc.id("solidified_request"));
	}
}
