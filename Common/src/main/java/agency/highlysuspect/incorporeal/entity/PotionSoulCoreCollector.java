package agency.highlysuspect.incorporeal.entity;

import agency.highlysuspect.incorporeal.IncEntityTypes;
import agency.highlysuspect.incorporeal.block.SoulCoreBlock;
import agency.highlysuspect.incorporeal.IncBlockEntityTypes;
import agency.highlysuspect.incorporeal.block.entity.PotionSoulCoreBlockEntity;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Optional;

public class PotionSoulCoreCollector extends LivingEntity {
	public PotionSoulCoreCollector(EntityType<? extends LivingEntity> type, Level level) {
		super(type, level);
		
		noPhysics = true;
	}
	
	//The entity sits just inside the soul core block.
	public static final double INSET = 0.01;
	public static final float WIDTH = (float) (SoulCoreBlock.WIDTH - INSET);
	public static final float HEIGHT = (float) (SoulCoreBlock.HEIGHT - INSET);
	public static final double DISTANCE_OFF_THE_GROUND = SoulCoreBlock.DISTANCE_OFF_THE_GROUND + (INSET / 2);
	
	public PotionSoulCoreCollector(Level level, BlockPos pos) {
		this(IncEntityTypes.POTION_SOUL_CORE_COLLECTOR, level);
		setPos(pos.getX() + .5d, pos.getY() + DISTANCE_OFF_THE_GROUND, pos.getZ() + .5d);
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
		
		Data data = findOrDiscard();
		if(data == null) return;
		
		data.player.heal(howMuch);
		data.be.drainMana(200);
	}
	
	@Override
	public boolean hurt(DamageSource source, float howMuch) {
		boolean happened = super.hurt(source, howMuch);
		if(happened || level.isClientSide) return happened;
		
		Data data = findOrDiscard();
		if(data == null) return false;
		
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
		
		//Lock into position, etc.
		//Unlike the AreaEffectCloud, the PotionSoulCoreCollector is a LivingEntity. It has health, air meter, etc.
		//I need to sort out some of those properties
		setDeltaMovement(0, 0, 0);
		setRot(0, 0);
		setHealth(getMaxHealth() / 2); //provide enough headroom to heal and harm
		setAirSupply(getMaxAirSupply());
		setPos(Math.floor(position().x) + .5d, Math.floor(position().y) + DISTANCE_OFF_THE_GROUND, Math.floor(position().z) + .5d);
		
		//Find the potionsoulcore and the player. if either does not exist, remove the entity
		Data data = findOrDiscard();
		if(data == null) return;
		
		//Transfer long lasting potion effects to the player.
		for(MobEffectInstance effect : getActiveEffects()) {
			data.player.addEffect(effect);
			data.be.drainMana(200);
		}
		
		//Clear my own potion effects
		removeAllEffects(); //nicely
		getActiveEffects().clear(); //harshly (removeAllEffects is cancellable on forge)
	}
	
	private record Data(PotionSoulCoreBlockEntity be, Player player) {}
	private @Nullable Data findOrDiscard() {
		PotionSoulCoreBlockEntity be = IncBlockEntityTypes.POTION_SOUL_CORE.getBlockEntity(level, blockPosition());
		if(be == null) {
			discard();
			return null;
		}
		
		Optional<ServerPlayer> player = be.findPlayer();
		if(player.isEmpty()) {
			discard();
			return null;
		}
		
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
	
	@Override
	public PushReaction getPistonPushReaction() {
		return PushReaction.IGNORE;
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
