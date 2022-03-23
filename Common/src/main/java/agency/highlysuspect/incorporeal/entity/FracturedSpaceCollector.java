package agency.highlysuspect.incorporeal.entity;

import agency.highlysuspect.incorporeal.IncEntityTypes;
import agency.highlysuspect.incorporeal.item.FracturedSpaceRodItem;
import agency.highlysuspect.incorporeal.IncItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.client.fx.SparkleParticleData;
import vazkii.botania.client.fx.WispParticleData;
import vazkii.botania.xplat.IXplatAbstractions;

import java.util.List;
import java.util.UUID;

/**
 * The vortex that sucks items in, when using the Rod of the Fractured Space.
 */
public class FracturedSpaceCollector extends Entity {
	public FracturedSpaceCollector(EntityType<?> type, Level level) {
		super(type, level);
		
		noPhysics = true;
	}
	
	public FracturedSpaceCollector(Level level, BlockPos cratePos, UUID ownerUuid) {
		this(IncEntityTypes.FRACTURED_SPACE_COLLECTOR, level);
		this.cratePos = cratePos;
		this.ownerUuid = ownerUuid;
	}
	
	private BlockPos cratePos;
	//todo: revisit this, if i want to add Avatar integration (lol)
	private UUID ownerUuid;
	
	private static final EntityDataAccessor<Integer> DATA_AGE = SynchedEntityData.defineId(FracturedSpaceCollector.class, EntityDataSerializers.INT);
	private static final double RADIUS = 2;
	private static final int MAX_AGE = 30;
	private static final int AGE_SPECIAL_START = (MAX_AGE * 3) / 4;
	private static final int MANA_COST_PER_ITEM = 500;
	private static final ItemStack TOOL_STACK = new ItemStack(IncItems.FRACTURED_SPACE_ROD);
	
	private static final int CHUNK_LOAD_TIMEOUT = 300; //15 seconds
	private static final int CHUNK_LOAD_RADIUS = 3;
	private static final TicketType<Unit> CHUNK_LOAD_REASON = TicketType.create("incorporeal:fracturedspace", (x, y) -> 0, CHUNK_LOAD_TIMEOUT);
	
	@Override
	public void tick() {
		super.tick();
		
		//setAge(getAge() + 1), int age = getAge(); //redundant get
		int age = getAge() + 1;
		setAge(age);
		
		if(level.isClientSide()) {
			doSparkles(age);
		} else if(age > AGE_SPECIAL_START) {
			//find items intersecting me. (this time i remembered to check isAlive, isnt that great!!!!!!)
			List<ItemEntity> nearbyItemEnts = level.getEntitiesOfClass(ItemEntity.class, getBoundingBox(), ent ->
				ent != null && ent.isAlive() && Math.hypot(getX() - ent.getX(), getZ() - ent.getZ()) <= RADIUS);
			
			//give em a good zucc
			for(ItemEntity ent : nearbyItemEnts) {
				double dx = getX() - ent.getX();
				double dz = getZ() - ent.getZ();
				ent.setDeltaMovement(ent.getDeltaMovement().add(dx * .3, 0, dz * .3));
				ent.hasImpulse = true; //"velocityChanged"
			}
			
			//end of the animation?
			if(age >= MAX_AGE) {
				//transport the items
				
				//find out who to deduct the mana from
				if(ownerUuid == null) {
					discard();
					return;
				}
				
				Player player = level.getPlayerByUUID(ownerUuid);
				if(player == null) {
					discard();
					return;
				}
				
				//chunkload the destination! yea!
				if(level instanceof ServerLevel slevel) {
					slevel.getChunkSource().addRegionTicket(CHUNK_LOAD_REASON, new ChunkPos(cratePos), CHUNK_LOAD_RADIUS, Unit.INSTANCE);
				}
				
				//triple check that yes, there's still a crate here, and it can eject items
				BlockState stateThere = level.getBlockState(cratePos);
				if(!FracturedSpaceRodItem.isCrate(stateThere) || !canEject(cratePos)) {
					discard();
					return;
				}
				
				//delete all items and eject them from the crate
				for(ItemEntity ent : nearbyItemEnts) {
					ItemStack stack = ent.getItem();
					int count = stack.getCount();
					int cost = count * MANA_COST_PER_ITEM;
					
					//do they have enough mana to move this item stack?
					if(ManaItemHandler.instance().requestManaExact(TOOL_STACK, player, cost, false)) {
						//extract it for real
						ManaItemHandler.instance().requestManaExact(TOOL_STACK, player, cost, true);
						//and move the item
						eject(cratePos, stack);
						ent.discard();
					}
				}
			}
		}
		
		if(age >= MAX_AGE) {
			discard();
		}
	}
	
	/// TileOpenCrate pastes ///
	
	//changed to take worldPosition
	public boolean canEject(BlockPos worldPosition) {
		float width = EntityType.ITEM.getWidth();
		float height = EntityType.ITEM.getHeight();
		
		double ejectX = worldPosition.getX() + 0.5;
		double ejectY = worldPosition.getY() - height;
		double ejectZ = worldPosition.getZ() + 0.5;
		AABB itemBB = new AABB(ejectX - width / 2, ejectY, ejectZ - width / 2, ejectX + width / 2, ejectY + height, ejectZ + width / 2);
		return level.noCollision(itemBB);
	}
	
	//changed to spawn the item directly, without touching the open crate's real inventory
	public void eject(BlockPos worldPosition, ItemStack stack) {
		boolean redstone = level.hasNeighborSignal(worldPosition);
		
		double ejectY = worldPosition.getY() - EntityType.ITEM.getHeight();
		ItemEntity item = new ItemEntity(level, worldPosition.getX() + 0.5, ejectY, worldPosition.getZ() + 0.5, stack);
		item.setDeltaMovement(Vec3.ZERO);
		if (redstone) {
			IXplatAbstractions.INSTANCE.itemFlagsComponent(item).timeCounter = -200;
		}
		
		//getItemHandler().setItem(0, ItemStack.EMPTY);
		level.addFreshEntity(item);
	}
	
	/// Client effects ///
	
	private void doSparkles(int age) {
		//i just fucking copy paste this from 1.16 i dont know how it works anymore lol
		
		int PARTICLE_COUNT = 12;
		
		double ageFraction = age / (double) MAX_AGE;
		//double radiusMult = 4 * (ageFraction - ageFraction * ageFraction); //simple and cute easing function
		double radiusMult = 1.6 * (ageFraction - Math.pow(ageFraction, 7)); //less simple but cuter easing function
		double particleAngle = age / 25d;
		double height = radiusMult / 2;
		
		for(int i = 0; i < PARTICLE_COUNT; i++, particleAngle += (2 * Math.PI) / PARTICLE_COUNT) {
			double x = Math.cos(particleAngle) * RADIUS * radiusMult;
			double z = Math.sin(particleAngle) * RADIUS * radiusMult;
			
			float size = (float) (1 + ageFraction * 5 * Math.random());
			
			SparkleParticleData uwu = SparkleParticleData.sparkle(size, 0.1f, 0.85f, 0.65f, 5);
			level.addParticle(uwu, getX() + x, getY() + height, getZ() + z, 0, 0, 0);
		}
		
		double x = Math.cos(Math.random() * Math.PI * 2) * RADIUS * radiusMult;
		double z = Math.cos(Math.random() * Math.PI * 2) * RADIUS * radiusMult;
		
		WispParticleData awa = WispParticleData.wisp(.5f, .1f, 85f, .65f, 0.5f);
		level.addParticle(awa, getX() + x, getY() + height, getZ() + z, 0f, 0f, 0f);
		
		if(age >= MAX_AGE - 2) {
			level.addParticle(ParticleTypes.LARGE_SMOKE, getX(), getY(), getZ(), 0, 0, 0);
			SparkleParticleData uwu = SparkleParticleData.sparkle(2f, 0.9f, 0.45f, 0.05f, 2);
			for(int i = 0; i < 5; i++) {
				level.addParticle(uwu, getX(), getY(), getZ(), 0, 0, 0);
			}
		}
	}
	
	/// Boilerplate ///
	
	private int getAge() {
		return getEntityData().get(DATA_AGE);
	}
	
	private void setAge(int age) {
		getEntityData().set(DATA_AGE, age);
	}
	
	@Override
	protected void defineSynchedData() {
		getEntityData().define(DATA_AGE, 0);
	}
	
	@Override
	protected void addAdditionalSaveData(CompoundTag tag) {
		tag.putInt("Age", getAge());
		tag.putUUID("Owner", ownerUuid);
		tag.put("CratePos", NbtUtils.writeBlockPos(cratePos));
	}
	
	@Override
	protected void readAdditionalSaveData(CompoundTag tag) {
		setAge(tag.getInt("Age"));
		ownerUuid = tag.getUUID("Owner");
		cratePos = NbtUtils.readBlockPos(tag.getCompound("CratePos"));
	}
	
	@Override
	public Packet<?> getAddEntityPacket() {
		return new ClientboundAddEntityPacket(this);
	}
}
