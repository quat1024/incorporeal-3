package agency.highlysuspect.incorporeal.computer.types;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.computer.CorporeaRequestMatcherType;
import agency.highlysuspect.incorporeal.computer.EmptyType;
import agency.highlysuspect.incorporeal.computer.IntegerType;
import agency.highlysuspect.incorporeal.computer.SolidifiedRequestType;
import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import agency.highlysuspect.incorporeal.item.TicketConjurerItem;
import agency.highlysuspect.incorporeal.item.TicketItem;
import agency.highlysuspect.incorporeal.util.SimplerRegistry;
import com.google.common.collect.Iterators;
import net.minecraft.util.Unit;
import vazkii.botania.api.corporea.CorporeaRequestMatcher;

public class DataTypes {
	public static final SimplerRegistry<DataType<?>> REGISTRY = new SimplerRegistry<>();
	
	public static final DataType<Unit> EMPTY = new EmptyType();
	public static final DataType<Integer> INTEGER = new IntegerType();
	public static final DataType<CorporeaRequestMatcher> MATCHER = new CorporeaRequestMatcherType();
	public static final DataType<SolidifiedRequest> SOLIDIFIED_REQUEST = new SolidifiedRequestType();
	
	public static void registerBuiltinTypes() {
		REGISTRY.register(EMPTY, Inc.id("empty"));
		REGISTRY.register(INTEGER, Inc.id("integer"));
		REGISTRY.register(MATCHER, Inc.id("matcher"));
		REGISTRY.register(SOLIDIFIED_REQUEST, Inc.id("solidified_request"));
	}
	
	public static Iterable<TicketItem<?>> allTicketItems() {
		return () -> Iterators.transform(REGISTRY.iterator(), DataType::ticketItem);
	}
	
	public static Iterable<TicketConjurerItem<?>> allConjurerItems() {
		return () -> Iterators.transform(REGISTRY.iterator(), DataType::conjurerItem);
	}
}
