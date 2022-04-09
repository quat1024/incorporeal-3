package agency.highlysuspect.incorporeal.item;

import agency.highlysuspect.incorporeal.block.entity.AbstractSoulCoreBlockEntity;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class BoundEnderPearlItem extends Item {
	public BoundEnderPearlItem(Properties props) {
		super(props);
	}
	
	public static final String OWNER_KEY = "BoundOwner";
	
	public @Nullable UUID getOwnerUuid(ItemStack stack) {
		if(!stack.hasTag()) return null;
		CompoundTag tag = stack.getTag(); assert tag != null;
		
		if(!tag.hasUUID(OWNER_KEY)) return null;
		else return tag.getUUID(OWNER_KEY);
	}
	
	public void setOwnerUuid(ItemStack stack, UUID uuid) {
		stack.getOrCreateTag().putUUID(OWNER_KEY, uuid);
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack held = player.getItemInHand(hand);
		
		UUID ownerUuid = getOwnerUuid(held);
		if(ownerUuid == null) {
			//Unbound. Bind it to the player
			ItemStack ya;
			if(player.getAbilities().instabuild) {
				ya = held.copy();
				ya.setCount(1);
			} else ya = held.split(1);
			
			setOwnerUuid(ya, player.getUUID());
			
			if(!player.level.isClientSide()) player.hurt(AbstractSoulCoreBlockEntity.SOUL, 2f);
			if(!player.getInventory().add(ya)) player.drop(ya, false);
			
			return InteractionResultHolder.success(held);
		}
		
		//Already bound; act like a working enderpearl that teleports the bound player
		
		level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDER_PEARL_THROW, SoundSource.NEUTRAL, .5f, .4f / (level.getRandom().nextFloat() * .4f + .8f));
		
		if(level instanceof ServerLevel slevel) {
			ServerPlayer owner = slevel.getServer().getPlayerList().getPlayer(ownerUuid);
			if(owner == null || owner.level != level) {
				return InteractionResultHolder.fail(held);
			}
			
			//basically paste from EnderpearlItem
			//note that owner does't necessarily equal "player", the thrower
			ThrownEnderpearl pearl = new ThrownEnderpearl(slevel, owner);
			pearl.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 1.5f, 1);
			pearl.setItem(held);
			slevel.addFreshEntity(pearl);
			
			if(!player.getAbilities().instabuild) held.shrink(1);
		}
		
		return InteractionResultHolder.success(held);
	}
	
	@Override
	public boolean isFoil(ItemStack stack) {
		return super.isFoil(stack) || getOwnerUuid(stack) != null;
	}
	
	public class DispenseBehavior extends OptionalDispenseItemBehavior {
		@Override
		protected ItemStack execute(BlockSource source, ItemStack stack) {
			ServerLevel level = source.getLevel();
			
			UUID ownerUuid = getOwnerUuid(stack);
			if(ownerUuid == null) {
				setSuccess(false);
				return stack;
			}
			
			ServerPlayer owner = level.getServer().getPlayerList().getPlayer(ownerUuid);
			if(owner == null || owner.level != level) {
				setSuccess(false);
				return stack;
			}
			
			//and now move into something similar to AbstractProjectileDispenseBehavior!
			Position pos = DispenserBlock.getDispensePosition(source);
			Direction dir = source.getBlockState().getValue(DispenserBlock.FACING);
			
			ThrownEnderpearl pearl = new ThrownEnderpearl(level, owner);
			//move it to the dispenser's dispense position instead of the position of the player
			pearl.setPos(pos.x(), pos.y(), pos.z());
			pearl.shoot(dir.getStepX(), dir.getStepY() + 0.1f, dir.getStepZ(), 1.1f, 6f);
			pearl.setItem(stack);
			level.addFreshEntity(pearl);
			
			stack.shrink(1);
			setSuccess(true);
			return stack;
		}
	}
}
