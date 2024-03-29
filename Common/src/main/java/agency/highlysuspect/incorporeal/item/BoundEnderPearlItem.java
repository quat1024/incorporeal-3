package agency.highlysuspect.incorporeal.item;

import agency.highlysuspect.incorporeal.block.entity.AbstractSoulCoreBlockEntity;
import agency.highlysuspect.incorporeal.util.MoreNbtHelpers;
import agency.highlysuspect.incorporeal.util.ServerPlayerDuck;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.mana.ManaBarTooltip;
import vazkii.botania.api.mana.ManaItem;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.common.helper.ItemNBTHelper;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class BoundEnderPearlItem extends Item {
	public BoundEnderPearlItem(Properties props) {
		super(props);
	}
	
	public static final String OWNER_KEY = "BoundOwner";
	public static final String OWNER_NAME_KEY = "BoundOwnerName"; //Used in the tooltip
	public static final String EPOCH_KEY = "BoundEpoch";
	
	public static final int TOTAL_CHARGES = 10;
	public static final int MANA_PER_CHARGE = 1000;
	public static final int MANA_CONTAINER_SIZE = TOTAL_CHARGES * MANA_PER_CHARGE;
	public static final int MANA_RECHARGE_RATE = MANA_PER_CHARGE / 20 / 5; //5 seconds per charge is fair i think
	
	public @Nullable UUID getOwnerUuid(ItemStack stack) {
		return MoreNbtHelpers.getUuid(stack, OWNER_KEY, null);
	}
	
	public @Nullable String getOwnerName(ItemStack stack) {
		return ItemNBTHelper.getString(stack, OWNER_NAME_KEY, null);
	}
	
	public int getEpoch(ItemStack stack) {
		return ItemNBTHelper.getInt(stack, EPOCH_KEY, 0);
	}
	
	@Override
	public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
		return Optional.of(ManaBarTooltip.fromManaItem(stack));
	}
	
	public void bindTo(ItemStack stack, ServerPlayer player) {
		MoreNbtHelpers.setUuid(stack, OWNER_KEY, player.getUUID());
		ItemNBTHelper.setString(stack, OWNER_NAME_KEY, player.getGameProfile().getName());
		ItemNBTHelper.setInt(stack, EPOCH_KEY, ((ServerPlayerDuck) player).inc$getEpoch());
		
		vazkii.botania.api.mana.ManaItem manaItem = XplatAbstractions.INSTANCE.findManaItem(stack);
		if(manaItem != null) manaItem.addMana(manaItem.getMaxMana()); //free mana woo
	}
	
	public void payMana(@Nullable Player player, @Nullable InteractionHand hand, ItemStack stack) {
		vazkii.botania.api.mana.ManaItem manaItem = XplatAbstractions.INSTANCE.findManaItem(stack);
		if(manaItem == null) return;
		
		manaItem.addMana(-MANA_PER_CHARGE);
		
		//If there is no more mana, break
		if(manaItem.getMana() <= 0) {
			stack.setCount(0);
			if(player != null && hand != null) {
				player.setItemInHand(hand, ItemStack.EMPTY);
				player.broadcastBreakEvent(hand);
			}
		}
	}
	
	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
		vazkii.botania.api.mana.ManaItem manaItem = XplatAbstractions.INSTANCE.findManaItem(stack);
		if(manaItem == null) return;
		
		int howMuchToRequest = Math.min(manaItem.getMaxMana() - manaItem.getMana(), MANA_RECHARGE_RATE);
		if (!world.isClientSide && entity instanceof Player player &&
			manaItem.getMana() <= manaItem.getMaxMana() &&
			ManaItemHandler.instance().requestManaExactForTool(stack, player, howMuchToRequest, true) //yooo armor mana discounts?? :real:
		) {
			manaItem.addMana(howMuchToRequest);
		}
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		
		UUID ownerUuid = getOwnerUuid(stack);
		if(ownerUuid == null) {
			//Not bound; bind to the player holding the item
			if(player instanceof ServerPlayer splayer) {
				bindTo(stack, splayer);
				player.hurt(AbstractSoulCoreBlockEntity.SOUL, 2f);	
			}
		} else {
			//Already bound; act like a working enderpearl that teleports the bound player
			//sound play on the clientside, but the rest happens serverside only
			level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDER_PEARL_THROW, SoundSource.NEUTRAL, .5f, .4f / (level.getRandom().nextFloat() * .4f + .8f));
			
			if(level instanceof ServerLevel slevel) {
				ServerPlayer owner = slevel.getServer().getPlayerList().getPlayer(ownerUuid);
				//Is the owner online and in the dimension?
				if(owner == null || owner.level != level) {
					return InteractionResultHolder.fail(stack);
				}
				
				//Has the owner not broken the link to this enderpearl?
				if(getEpoch(stack) != ((ServerPlayerDuck) owner).inc$getEpoch()) {
					player.displayClientMessage(Component.translatable("incorporeal.bound_ender_pearl.dissolve").withStyle(ChatFormatting.BLUE), true);
					player.broadcastBreakEvent(hand);
					player.setItemInHand(hand, ItemStack.EMPTY);
					return InteractionResultHolder.success(ItemStack.EMPTY);
				}
				
				//basically a paste from EnderpearlItem
				//note that owner does't necessarily equal "player", the thrower
				yeetEnderpearl(slevel, owner, stack, pearl -> pearl.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 1.5f, 1));
				payMana(player, hand, stack);
				//if(!player.getAbilities().instabuild) player.getCooldowns().addCooldown(this, 20); //slow mana recharge rate takes care of this imo
			}
			
		}
		return InteractionResultHolder.success(stack);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag whyTho) {
		super.appendHoverText(stack, level, tooltip, whyTho);
		if(level == null) return;
		
		String ownerName = getOwnerName(stack);
		if(ownerName == null) {
			tooltip.add(Component.translatable("incorporeal.bound_ender_pearl.notBound").withStyle(ChatFormatting.RED));
		} else {
			Component ownerComponent = Component.literal(ownerName).withStyle(ChatFormatting.GOLD);
			tooltip.add(Component.translatable("incorporeal.bound_ender_pearl.boundTo", ownerComponent));
		}
		
		if(whyTho.isAdvanced()) {
			UUID ownerUuid = getOwnerUuid(stack);
			if(ownerUuid != null) tooltip.add(Component.literal(ownerUuid.toString()).withStyle(ChatFormatting.DARK_GRAY));
			tooltip.add(Component.translatable("incorporeal.bound_ender_pearl.epoch", getEpoch(stack)).withStyle(ChatFormatting.DARK_GRAY));
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
			//Is the owner online and in the dimension?
			if(owner == null || owner.level != slevel) {
				setSuccess(false);
				return stack;
			}
			
			//Has the owner not broken the link to this enderpearl?
			if(getEpoch(stack) != ((ServerPlayerDuck) owner).inc$getEpoch()) {
				setSuccess(true);
				return stack;
			}
			
			//and now move into something similar to AbstractProjectileDispenseBehavior!
			Position pos = DispenserBlock.getDispensePosition(source);
			Direction dir = source.getBlockState().getValue(DispenserBlock.FACING);
			
			yeetEnderpearl(slevel, owner, stack, pearl -> {
				pearl.setPos(pos.x(), pos.y(), pos.z());
				pearl.shoot(dir.getStepX(), dir.getStepY() + 0.1f, dir.getStepZ(), 1.1f, 6f);
			});
			
			payMana(null, null, stack);
			
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
	
	public record ManaItem(BoundEnderPearlItem item, ItemStack stack) implements vazkii.botania.api.mana.ManaItem {
		public static final String MANA_KEY = "Mana";
		
		@Override
		public int getMana() {
			return ItemNBTHelper.getInt(stack, MANA_KEY, 0);
		}
		
		@Override
		public int getMaxMana() {
			return MANA_CONTAINER_SIZE;
		}
		
		@Override
		public void addMana(int mana) {
			ItemNBTHelper.setInt(stack, MANA_KEY, Mth.clamp(getMana() + mana, 0, getMaxMana()));
		}
		
		@Override
		public boolean canReceiveManaFromPool(BlockEntity be) {
			return item.getOwnerUuid(stack) != null;
		}
		
		@Override
		public boolean canReceiveManaFromItem(ItemStack otherStack) {
			return item.getOwnerUuid(stack) != null;
		}
		
		@Override
		public boolean canExportManaToPool(BlockEntity pool) {
			return false;
		}
		
		@Override
		public boolean canExportManaToItem(ItemStack otherStack) {
			return false;
		}
		
		@Override
		public boolean isNoExport() {
			return true;
		}
	}
}
