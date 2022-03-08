package agency.highlysuspect.incorporeal.entity;

import agency.highlysuspect.incorporeal.block.entity.IncBlockEntityTypes;
import agency.highlysuspect.incorporeal.block.entity.PotionSoulCoreBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Optional;

public class PotionSoulCoreCollector extends LivingEntity {
	public PotionSoulCoreCollector(EntityType<? extends LivingEntity> type, Level level) {
		super(type, level);
		
		noPhysics = true;
	}
	
	public PotionSoulCoreCollector(Level level, BlockPos pos) {
		this(IncEntityTypes.POTION_SOUL_CORE_COLLECTOR, level);
		setPos(pos.getX() + .5d, pos.getY() + 0.005d, pos.getZ() + 0.5d);
	}
	
	public static AttributeSupplier.Builder attrs() {
		return Mob.createMobAttributes()
			.add(Attributes.MAX_HEALTH, 400)
			.add(Attributes.MOVEMENT_SPEED, 0)
			.add(Attributes.ARMOR, 0)
			.add(Attributes.ARMOR_TOUGHNESS, 0);
	}
	
	@Override
	public void heal(float howMuch) {
		super.heal(howMuch);
		if(level.isClientSide()) return;
		
		Data data = find();
		if(data == null) {
			discard();
			return;
		}
		
		data.player.heal(howMuch);
		data.be.drainMana(200);
	}
	
	@Override
	public boolean hurt(DamageSource source, float howMuch) {
		boolean happened = super.hurt(source, howMuch); //TODO: remove this super call? (will want to fire events on forge)
		if(happened || level.isClientSide) return happened;
		
		Data data = find();
		if(data == null) {
			discard();
			return false;
		}
		
		happened = data.player.hurt(source, howMuch);
		if(happened) {
			data.be.drainMana(200);
			return true;
		}
		
		return false;
	}
	
	@Override
	public void tick() {
		if(level.isClientSide()) return;
		
		//Lock into position, etc
		setDeltaMovement(0, 0, 0);
		setRot(0, 0);
		setHealth(getMaxHealth() / 2);
		setAirSupply(getMaxAirSupply());
		setPos(Math.floor(position().x) + 0.5d, Math.floor(position().y + 0.005d), Math.floor(position().z) + 0.5d);
		
		//Find the potionsoulcore and the player. if either does not exist, remove the entity
		Data data = find();
		if(data == null) {
			discard();
			return;
		}
		
		//Transfer long lasting potion effects to the player.
		for(MobEffectInstance effect : getActiveEffects()) {
			data.player.addEffect(effect);
			data.be.drainMana(200);
		}
		
		//Clear my own potion effects
		removeAllEffects(); //nicely
		getActiveEffects().clear(); //harshly (it's cancellable on forge)
	}
	
	private record Data(PotionSoulCoreBlockEntity be, ServerPlayer player) {}
	private @Nullable Data find() {
		PotionSoulCoreBlockEntity be = IncBlockEntityTypes.POTION_SOUL_CORE.getBlockEntity(level, blockPosition());
		if(be == null) return null;
		
		Optional<ServerPlayer> player = be.findPlayer();
		if(player.isEmpty()) return null;
		
		return new Data(be, player.get());
	}
	
	@Override
	public void knockback(double $$0, double $$1, double $$2) {
		//nope!
	}
	
	@Override
	public boolean canBreatheUnderwater() {
		return true;
	}
	
	@Override
	protected boolean shouldDropExperience() {
		return false;
	}
	
	@Override
	protected boolean shouldDropLoot() {
		return false;
	}
	
	@Override
	protected int getExperienceReward(Player $$0) {
		return 0;
	}
	
	@Override
	public boolean isInvertedHealAndHarm() {
		return true; //:eyes:
	}
	
	@Override
	public MobType getMobType() {
		return MobType.UNDEAD; //:eyes:
	}
	
	/// livingentity boilerplate ///
	
	@Override
	public Iterable<ItemStack> getArmorSlots() {
		return Collections.emptySet();
	}
	
	@Override
	public ItemStack getItemBySlot(EquipmentSlot equipmentSlot) {
		return ItemStack.EMPTY;
	}
	
	@Override
	public void setItemSlot(EquipmentSlot equipmentSlot, ItemStack itemStack) {
		//(poof)
	}
	
	@Override
	public HumanoidArm getMainArm() {
		return HumanoidArm.LEFT;
	}
	
	@Override
	public boolean isPushable() {
		return false;
	}
	
	@Override
	public boolean startRiding(Entity $$0, boolean $$1) {
		return false;
	}
	
	@Override
	protected boolean canRide(Entity $$0) {
		return false;
	}
	
	/// entity boilerplate ///
	
	@Override
	public Packet<?> getAddEntityPacket() {
		return new ClientboundAddEntityPacket(this);
	}
}
