package agency.highlysuspect.incorporeal.computer.types;

import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import net.minecraft.nbt.CompoundTag;

import java.util.Optional;

/**
 * A DataType representing "solidified requests", which are a corporea request matcher + count pair.
 */
public class SolidifiedRequestType implements DataType<SolidifiedRequest> {
	@Override
	public void save(SolidifiedRequest thing, CompoundTag tag) {
		tag.put("request", thing.save());
	}
	
	@Override
	public Optional<SolidifiedRequest> tryLoad(CompoundTag tag) {
		return SolidifiedRequest.tryLoad(tag.getCompound("request"));
	}
	
	@Override
	public int color(SolidifiedRequest thing) {
		return 0x66FF33;
	}
	
	@Override
	public int signal(SolidifiedRequest thing) {
		return thing.signalStrength();
	}
}
