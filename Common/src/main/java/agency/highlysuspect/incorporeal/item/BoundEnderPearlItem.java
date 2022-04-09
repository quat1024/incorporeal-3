package agency.highlysuspect.incorporeal.item;

import agency.highlysuspect.incorporeal.block.entity.AbstractSoulCoreBlockEntity;
import agency.highlysuspect.incorporeal.util.MoreNbtHelpers;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
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
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.common.helper.ItemNBTHelper;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class BoundEnderPearlItem extends Item {
	public BoundEnderPearlItem(Properties props) {
		super(props);
	}
	
	public static final String OWNER_KEY = "BoundOwner";
	public static final String OWNER_NAME_KEY = "BoundOwnerName"; //Used in the tooltip
	
	public @Nullable UUID getOwnerUuid(ItemStack stack) {
		return MoreNbtHelpers.getUuid(stack, OWNER_KEY, null);
	}
	
	public @Nullable String getOwnerName(ItemStack stack) {
		return ItemNBTHelper.getString(stack, OWNER_NAME_KEY, null);
	}
	
	public void bindTo(ItemStack stack, Player player) {
		MoreNbtHelpers.setUuid(stack, OWNER_KEY, player.getUUID());
		ItemNBTHelper.setString(stack, OWNER_NAME_KEY, player.getGameProfile().getName());
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
			
			bindTo(ya, player);
			
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
			yeetEnderpearl(slevel, owner, held, pearl -> pearl.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 1.5f, 1));
			
			if(!player.getAbilities().instabuild) held.shrink(1);
		}
		
		return InteractionResultHolder.success(held);
	}
	
	@Override
	public boolean isFoil(ItemStack stack) {
		return super.isFoil(stack) || getOwnerUuid(stack) != null;
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag whyTho) {
		super.appendHoverText(stack, level, tooltip, whyTho);
		if(level == null) return;
		
		String ownerName = getOwnerName(stack);
		if(ownerName == null) {
			tooltip.add(new TranslatableComponent("incorporeal.bound_ender_pearl.notBound").withStyle(ChatFormatting.RED));
		} else {
			Component ownerComponent = new TextComponent(ownerName).withStyle(ChatFormatting.GOLD);
			tooltip.add(new TranslatableComponent("incorporeal.bound_ender_pearl.boundTo", ownerComponent));
		}
		
		if(whyTho.isAdvanced()) {
			UUID ownerUuid = getOwnerUuid(stack);
			if(ownerUuid != null) tooltip.add(new TextComponent(ownerUuid.toString()).withStyle(ChatFormatting.DARK_GRAY));
		}
	}
	
	public class DispenseBehavior extends OptionalDispenseItemBehavior {
		@Override
		protected ItemStack execute(BlockSource source, ItemStack stack) {
			ServerLevel slevel = source.getLevel();
			
			UUID ownerUuid = getOwnerUuid(stack);
			if(ownerUuid == null) {
				setSuccess(false);
				return stack;
			}
			
			ServerPlayer owner = slevel.getServer().getPlayerList().getPlayer(ownerUuid);
			if(owner == null || owner.level != slevel) {
				setSuccess(false);
				return stack;
			}
			
			//and now move into something similar to AbstractProjectileDispenseBehavior!
			Position pos = DispenserBlock.getDispensePosition(source);
			Direction dir = source.getBlockState().getValue(DispenserBlock.FACING);
			
			yeetEnderpearl(slevel, owner, stack, pearl -> {
				pearl.setPos(pos.x(), pos.y(), pos.z());
				pearl.shoot(dir.getStepX(), dir.getStepY() + 0.1f, dir.getStepZ(), 1.1f, 6f);
			});
			
			stack.shrink(1);
			setSuccess(true);
			return stack;
		}
	}
	
	private static void yeetEnderpearl(ServerLevel slevel, Player owner, ItemStack stack, Consumer<ThrownEnderpearl> pearlConfigurator) {
		ThrownEnderpearl pearl = new ThrownEnderpearl(slevel, owner);
		pearlConfigurator.accept(pearl);
		pearl.setItem(stack);
		slevel.addFreshEntity(pearl);
	}
}
