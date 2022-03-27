package agency.highlysuspect.incorporeal.mixin.datagen;

import agency.highlysuspect.incorporeal.datagen.DatagenDuck;
import agency.highlysuspect.incorporeal.datagen.EnUsRewriter;
import net.minecraft.data.DataGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DataGenerator.class)
public class DataGeneratorMixin implements DatagenDuck {
	@Unique EnUsRewriter enUsRewriter = new EnUsRewriter();
	
	@Override
	public EnUsRewriter inc$getEnUsRewriter() {
		return enUsRewriter;
	}
	
	/**
	 * After all the data generators have run, give EnUsRewriter a chance to apply any changes made to en_us.json.
	 */
	@Inject(method = "run", at = @At("TAIL"))
	private void afterRun(CallbackInfo ci) {
		enUsRewriter.rewrite();
	}
}
