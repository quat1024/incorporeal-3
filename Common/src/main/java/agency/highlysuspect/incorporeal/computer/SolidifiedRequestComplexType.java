package agency.highlysuspect.incorporeal.computer;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class SolidifiedRequestComplexType implements DataType<SolidifiedRequest> {
	@Override
	public Class<SolidifiedRequest> typeClass() {
		return SolidifiedRequest.class;
	}
	
	@Override
	public ResourceLocation id() {
		return Inc.id("solidified_request");
	}
	
	@Override
	public void save(SolidifiedRequest thing, CompoundTag tag) {
		tag.put("request", thing.save());
	}
	
	@Override
	public SolidifiedRequest load(CompoundTag tag) {
		return SolidifiedRequest.loadOrEmpty(tag.getCompound("request"));
	}
	
	@Override
	public SolidifiedRequest add(SolidifiedRequest... inputs) {
		return inputs[0]; //todo not actually sure how to handle this case. should probably disallow it
	}
}
