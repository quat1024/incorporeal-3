package agency.highlysuspect.incorporeal.computer.types;

import agency.highlysuspect.incorporeal.corporea.AndingCorporeaRequestMatcher;
import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import net.minecraft.nbt.CompoundTag;

import java.util.List;
import java.util.Optional;

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
	public SolidifiedRequest sum(SolidifiedRequest a, SolidifiedRequest b) {
		return SolidifiedRequest.create(
			AndingCorporeaRequestMatcher.combine(List.of(a.matcher(), b.matcher())),
			a.count() + b.count()
		);
	}
}
