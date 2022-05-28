package agency.highlysuspect.incorporeal.mixin;

import agency.highlysuspect.incorporeal.util.ServerPlayerDuck;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin implements ServerPlayerDuck {
	@Unique int boundPearlEpoch = 0;
	
	@Override
	public int inc$getEpoch() {
		return boundPearlEpoch;
	}
	
	@Override
	public void inc$bumpEpoch() {
		boundPearlEpoch++;
	}
	
	@Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
	private void whenReadingSaveData(CompoundTag tag, CallbackInfo ci) {
		boundPearlEpoch = tag.getInt("incorporeal-boundPearlEpoch");
	}
	
	@Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
	private void whenWritingSaveData(CompoundTag tag, CallbackInfo ci) {
		tag.putInt("incorporeal-boundPearlEpoch", boundPearlEpoch);
	}
	
	//what
	@Inject(method = "restoreFrom", at = @At("RETURN"))
	private void idkDude(ServerPlayer clone, boolean whomst, CallbackInfo ci) {
		boundPearlEpoch = ((ServerPlayerDuck) clone).inc$getEpoch();
	}
}
