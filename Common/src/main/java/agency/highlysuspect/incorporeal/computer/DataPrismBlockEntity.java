package agency.highlysuspect.incorporeal.computer;

import agency.highlysuspect.incorporeal.block.entity.IncBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.block.IWandBindable;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.common.block.tile.TileMod;
import vazkii.botania.common.helper.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DataPrismBlockEntity extends TileMod implements IWandBindable {
	public DataPrismBlockEntity(BlockPos pos, BlockState state) {
		super(IncBlockEntityTypes.DATA_PRISM, pos, state);
	}
	
	private final List<BlockPos> bindTargets = new ArrayList<>();
	
	public void act(BlockPos me, Direction direction) {
		assert level != null;
		
		//TODO
	}
	
	@Override
	public boolean canSelect(Player player, ItemStack wand, BlockPos pos, Direction side) {
		return true;
	}
	
	@Override
	public boolean bindTo(Player player, ItemStack wand, BlockPos target, Direction side) {
		//linear scan, yes
		if(bindTargets.contains(target)) {
			bindTargets.remove(target);
			setChanged();
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
			return true;
		}
		
		BlockPos me = getBlockPos();
		if(Objects.equals(me, target)) return false;
		if(MathHelper.distSqr(me, target) > 12 * 12) return false;
		
		//Only allow axis aligned binds for now.
		//TODO remove this restriction
		int matchingAxes = 0;
		if(me.getX() == target.getX()) matchingAxes++;
		if(me.getY() == target.getY()) matchingAxes++;
		if(me.getZ() == target.getZ()) matchingAxes++;
		if(matchingAxes != 2) return false;
		
		bindTargets.add(target);
		setChanged();
		VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
		return true;
	}
	
	int lol;
	
	@Nullable
	@Override
	public BlockPos getBinding() {
		//TODO: This is very silly
		if(bindTargets.size() == 0) return null;
		return bindTargets.get(lol++ % bindTargets.size());
	}
	
	@Override
	public void writePacketNBT(CompoundTag cmp) {
		ListTag list = new ListTag();
		for(BlockPos bindTarget : bindTargets) {
			list.add(NbtUtils.writeBlockPos(bindTarget));
		}
		cmp.put("Binds", list);
	}
	
	@Override
	public void readPacketNBT(CompoundTag cmp) {
		bindTargets.clear();
		ListTag list = cmp.getList("Binds", 10);
		for(Tag tagg : list) {
			if(!(tagg instanceof CompoundTag taggg)) continue;
			bindTargets.add(NbtUtils.readBlockPos(taggg));
		}
	}
}
