package agency.highlysuspect.incorporeal.item;

import agency.highlysuspect.incorporeal.block.entity.AbstractSoulCoreBlockEntity;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
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
		
		if(getOwnerUuid(held) == null) {
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
		
		return InteractionResultHolder.pass(held);
	}
	
	@Override
	public boolean isFoil(ItemStack stack) {
		return super.isFoil(stack) || getOwnerUuid(stack) != null;
	}
	
	public class DispenseBehavior extends OptionalDispenseItemBehavior {
		@Override
		protected ItemStack execute(BlockSource source, ItemStack stack) {
			UUID ownerUuid = getOwnerUuid(stack);
			if(ownerUuid == null) {
				setSuccess(false);
				return stack;
			}
			
			ServerPlayer player = source.getLevel().getServer().getPlayerList().getPlayer(ownerUuid);
			if(player == null) {
				setSuccess(false);
				return stack;
			}
			
			//and now move into something similar to AbstractProjectileDispenseBehavior!
			Position pos = DispenserBlock.getDispensePosition(source);
			Direction dir = source.getBlockState().getValue(DispenserBlock.FACING);
			
			ThrownEnderpearl pearl = new ThrownEnderpearl(source.getLevel(), player);
			//move it to the dispenser's dispense position instead of the position of the player
			pearl.setPos(pos.x(), pos.y(), pos.z());
			pearl.shoot(dir.getStepX(), dir.getStepY() + 0.1f, dir.getStepZ(), 1.1f, 6f);
			pearl.setItem(stack);
			source.getLevel().addFreshEntity(pearl);
			
			stack.shrink(1);
			setSuccess(true);
			return stack;
		}
	}
}
