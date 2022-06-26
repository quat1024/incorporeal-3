package agency.highlysuspect.incorporeal.item;

import agency.highlysuspect.incorporeal.entity.FracturedSpaceCollector;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.item.ICoordBoundItem;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.helper.ItemNBTHelper;
import vazkii.botania.common.helper.PlayerHelper;

import java.util.List;

public class FracturedSpaceRodItem extends Item {
	public FracturedSpaceRodItem(Properties $$0) {
		super($$0);
	}
	
	private static final String POS = "CratePos";
	private static final String DIMENSION = "CrateDimension";
	
	
	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		Player player = ctx.getPlayer();
		if(!PlayerHelper.isTruePlayer(player)) return InteractionResult.FAIL;
		
		Level level = ctx.getLevel();
		ItemStack held = ctx.getItemInHand();
		BlockPos pos = ctx.getClickedPos();
		BlockState hitState = level.getBlockState(pos);
		
		if(isCrate(hitState)) {
			//clicked a crate, remember this position
			ItemNBTHelper.setCompound(held, POS, NbtUtils.writeBlockPos(pos));
			ItemNBTHelper.setString(held, DIMENSION, stringifyDimension(level));
			status(player, t("incorporeal.fractured_space.saved").withStyle(ChatFormatting.DARK_PURPLE));
		} else {
			//didn't click a crate
			
			//make sure they click the top of the block
			if(ctx.getClickedFace() != Direction.UP) return InteractionResult.PASS;
			
			//make sure a crate position exists
			CompoundTag cratePosCmp = ItemNBTHelper.getCompound(held, POS, true);
			String crateDimensionStr = ItemNBTHelper.getString(held, DIMENSION, null);
			if(cratePosCmp == null || crateDimensionStr == null) {
				status(player, t("incorporeal.fractured_space.no_pos").withStyle(ChatFormatting.RED));
				return InteractionResult.FAIL;
			}
			
			//make sure the dimension is the same
			if(!stringifyDimension(level).equals(crateDimensionStr)) {
				status(player, t("incorporeal.fractured_space.wrong_dimension").withStyle(ChatFormatting.RED));
				return InteractionResult.FAIL;
			}
			
			//(this spawns an entity + might require a chunkload)
			if(level.isClientSide()) return InteractionResult.SUCCESS;
			
			//make sure an open crate is there
			BlockPos cratePos = NbtUtils.readBlockPos(cratePosCmp);
			BlockState stateThere =	level.getBlockState(cratePos); //chunkload
			if(!isCrate(stateThere)) {
				status(player, t("incorporeal.fractured_space.no_crate_there").withStyle(ChatFormatting.RED));
				return InteractionResult.FAIL;
			}
			
			//all checks ok, spawn the collector
			FracturedSpaceCollector collector = new FracturedSpaceCollector(level, cratePos, player.getUUID());
			collector.setPos(ctx.getClickLocation().x, pos.getY() + 1, ctx.getClickLocation().z);
			level.addFreshEntity(collector);
		}
		
		return InteractionResult.SUCCESS;
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag why) {
		if(level == null) return;
		
		CompoundTag cratePosCmp = ItemNBTHelper.getCompound(stack, POS, true);
		if(cratePosCmp == null) {
			tooltip.add(t("incorporeal.fractured_space.tooltip.not_bound").withStyle(ChatFormatting.RED));
		} else {
			tooltip.add(t("incorporeal.fractured_space.tooltip.bound").withStyle(ChatFormatting.GREEN));
			
			String dimensionStr = ItemNBTHelper.getString(stack, DIMENSION, null);
			if(dimensionStr == null || !stringifyDimension(level).equals(dimensionStr)) {
				tooltip.add(t("incorporeal.fractured_space.tooltip.wrong_dimension").withStyle(ChatFormatting.RED));
			}
			
			if(why.isAdvanced()) {
				BlockPos pos = NbtUtils.readBlockPos(cratePosCmp);
				tooltip.add(t("incorporeal.fractured_space.tooltip.debug.pos", pos.getX(), pos.getY(), pos.getZ()).withStyle(ChatFormatting.GRAY));
				tooltip.add(t("incorporeal.fractured_space.tooltip.debug.dim", dimensionStr).withStyle(ChatFormatting.GRAY));
			}
		}
	}
	
	public static boolean isCrate(BlockState state) {
		//todo: in 1.12/16 this was a tag, i don't really remember why though lol
		// i guess you could do things like, have compat with quark chute?
		// also it checked the block entity type, for no reason lol
		return state.is(ModBlocks.openCrate);
	}
	
	private static String stringifyDimension(Level level) {
		return level.dimension().location().toString();
	}
	
	private static void status(Player player, Component component) {
		player.displayClientMessage(component, true);
	}
	
	private static TranslatableComponent t(String key, Object... formatStringArgs) {
		return new TranslatableComponent(key, formatStringArgs);
	}
	
	public static record CoordBoundItem(ItemStack stack) implements ICoordBoundItem {
		@Nullable
		@Override
		public BlockPos getBinding(Level level) {
			CompoundTag cratePosCmp = ItemNBTHelper.getCompound(stack, POS, true);
			if(cratePosCmp == null) return null;
			
			String dimensionStr = ItemNBTHelper.getString(stack, DIMENSION, null);
			if(dimensionStr == null || !stringifyDimension(level).equals(dimensionStr)) return null;
			
			return NbtUtils.readBlockPos(cratePosCmp);
		}
	}
}
