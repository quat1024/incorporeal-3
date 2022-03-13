package agency.highlysuspect.incorporeal.computer.types;

import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import net.minecraft.nbt.CompoundTag;

import java.util.Optional;

public class SolidifiedRequestType implements DataType<SolidifiedRequest> {
	@Override
	public void save(SolidifiedRequest thing, CompoundTag tag) {
		tag.put("request", thing.save());
	}
	
	@Override
	public Optional<SolidifiedRequest> tryLoad(CompoundTag tag) {
		return SolidifiedRequest.tryLoad(tag);
	}
	
	@Override
	public SolidifiedRequest sum(SolidifiedRequest... inputs) {
		return inputs[0]; //todo not actually sure how to handle this case. should probably disallow it
	}
}
