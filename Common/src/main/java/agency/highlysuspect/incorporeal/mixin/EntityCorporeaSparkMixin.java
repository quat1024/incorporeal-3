package agency.highlysuspect.incorporeal.mixin;

import agency.highlysuspect.incorporeal.block.entity.CorporeaPylonBlockEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.botania.api.corporea.ICorporeaSpark;
import vazkii.botania.common.entity.EntityCorporeaSpark;

import java.util.Iterator;
import java.util.List;
import java.util.WeakHashMap;

/**
 * Used to implement the Corporea Pylon
 */
@Mixin(EntityCorporeaSpark.class)
public abstract class EntityCorporeaSparkMixin implements CorporeaPylonBlockEntity.ICorporeaSparkExt {
	//the value is "world time since a corporea pylon last pinged me about this remote binding"
	@Unique WeakHashMap<ICorporeaSpark, Long> remoteBindings = new WeakHashMap<>();
	
	@Shadow protected abstract void findNetwork();
	
	@Override
	public boolean registerRemoteBinding(ICorporeaSpark other) {
		boolean alreadyKnew = remoteBindings.containsKey(other);
		remoteBindings.put(other, other.entity().level.getGameTime());
		return !alreadyKnew;
	}
	
	@Override
	public void inc$findNetwork() {
		findNetwork();
	}
	
	
	@SuppressWarnings("ConstantConditions") //Double-casts)
	@Inject(method = "tick", at = @At("HEAD"))
	private void onTick(CallbackInfo ci) {
		if(!remoteBindings.isEmpty()) {
			Level level = ((EntityCorporeaSpark) (Object) this).level;
			
			//Clean up dead/unloaded corporea spark bindings (without waiting for WeakHashMap garbage collector to do it)
			Iterator<ICorporeaSpark> sparkerator = remoteBindings.keySet().iterator();
			while(sparkerator.hasNext()) {
				ICorporeaSpark spark = sparkerator.next();
				
				long lastPing = remoteBindings.get(spark);
				long timeSinceLastPing = level.getGameTime() - lastPing;
				
				if(!spark.entity().isAlive() || !level.isLoaded(spark.getAttachPos()) || timeSinceLastPing > 5) {
					sparkerator.remove();
				}
			}
		}
	}
	
	
	@Inject(method = "getNearbySparks", remap = false, at = @At("RETURN"))
	private void addMoreSparks(CallbackInfoReturnable<List<ICorporeaSpark>> cir) {
		if(!remoteBindings.isEmpty()) {
			//Remove anything already in extraBindings (e.g. if you connect sparks with pylons that would have connected anyway)
			cir.getReturnValue().forEach(remoteBindings.keySet()::remove);
			
			//Add all the extrabindings
			cir.getReturnValue().addAll(remoteBindings.keySet());
		}
	}
}
