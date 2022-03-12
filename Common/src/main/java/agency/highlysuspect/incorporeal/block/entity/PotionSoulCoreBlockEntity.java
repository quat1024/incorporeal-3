package agency.highlysuspect.incorporeal.block.entity;

import agency.highlysuspect.incorporeal.entity.PotionSoulCoreCollector;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Optional;

public class PotionSoulCoreBlockEntity extends AbstractSoulCoreBlockEntity {
	public PotionSoulCoreBlockEntity(BlockPos pos, BlockState state) {
		super(IncBlockEntityTypes.POTION_SOUL_CORE, pos, state);
	}
	
	@Override
	protected int getMaxMana() {
		return 3000;
	}
	
	@Override
	protected void tick() {
		if(level == null || level.isClientSide()) return;
		
		//Manage the soulcore collector entities.
		List<PotionSoulCoreCollector> collectors = findCollectors();
		Optional<Player> player = findPlayerClientSafeLol();
		
		//If there's no player present, or there's somehow more than 1 collector, remove them.
		if(collectors.size() >= 2 || player.isEmpty()) {
			collectors.forEach(Entity::discard);
			collectors.clear();
		}
		
		//If there are no collectors and the player is online, create a new collector.
		if(collectors.isEmpty() && player.isPresent()) {
			level.addFreshEntity(new PotionSoulCoreCollector(level, worldPosition));
		}
	}
	
	public List<PotionSoulCoreCollector> findCollectors() {
		assert level != null;
		return level.getEntitiesOfClass(PotionSoulCoreCollector.class, new AABB(worldPosition));
	}
	
	@Override
	public int computeSignal() {
		return findPlayerClientSafeLol().map(player -> {
			if(player.getMaxHealth() == 0) return 0; //What?
			else return Mth.floor(Mth.clamp((player.getHealth() / player.getMaxHealth()) * 15, 0, 15));
		}).orElse(0);
	}
}
