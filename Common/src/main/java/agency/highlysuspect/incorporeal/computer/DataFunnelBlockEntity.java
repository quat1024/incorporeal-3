package agency.highlysuspect.incorporeal.computer;

import agency.highlysuspect.incorporeal.block.entity.IncBlockEntityTypes;
import agency.highlysuspect.incorporeal.computer.capabilities.DatumAcceptor;
import agency.highlysuspect.incorporeal.computer.capabilities.DatumProvider;
import agency.highlysuspect.incorporeal.computer.types.Datum;
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

public class DataFunnelBlockEntity extends TileMod implements IWandBindable, DatumAcceptor, DatumProvider {
	public DataFunnelBlockEntity(BlockPos pos, BlockState state) {
		super(IncBlockEntityTypes.DATA_FUNNEL, pos, state);
	}
	
	private final List<BlockPos> bindTargets = new ArrayList<>();
	private Datum<?> datum = Datum.EMPTY;
	
	@Override
	public void acceptDatum(Datum<?> datum) {
		boolean changed = !this.datum.equals(datum);
		this.datum = datum;
		if(changed) {
			setChanged();
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
		}
	}
	
	@Override
	public Datum<?> readDatum() {
		return datum;
	}
	
	public void act() {
		assert level != null;
		
		//TODO: the entire shit
		// - raycast from datumproviders at bind positions towards myself
		//   - if finding a mana prism w/ NotManaLens, apply the data effect
		//   - if finding a datumacceptor, input datum, and drop that raycast
		// - do the funny magma operation to combine all the datums
		// - store resulting datum in myself
	}
	
	@Override
	public boolean canSelect(Player player, ItemStack wand, BlockPos pos, Direction side) {
		return true;
	}
	
	@Override
	public boolean bindTo(Player player, ItemStack wand, BlockPos target, Direction side) {
		//linear scan, yes
		
		//binding to the same target again will unbind
		if(bindTargets.contains(target)) {
			bindTargets.remove(target);
			setChanged();
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
			return true;
		}
		
		if(bindTargets.size() >= 10) return false; //gotta stop somewhere
		if(Objects.equals(getBlockPos(), target)) return false; //don't allow self-binds?
		if(MathHelper.distSqr(getBlockPos(), target) > 12 * 12) return false; //limit range
		
		bindTargets.add(target);
		setChanged();
		VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
		return true;
	}
	
	int lol;
	
	@Nullable
	@Override
	public BlockPos getBinding() {
		//TODO: This is very silly, and is obviously temporary
		// Look into something like IWireframeCoordinateListProvider but for blockentities and not items i guess?
		if(bindTargets.size() == 0) return null;
		return bindTargets.get(lol++ % bindTargets.size());
	}
	
	@Override
	public void writePacketNBT(CompoundTag cmp) {
		cmp.put("Datum", datum.save());
		
		ListTag list = new ListTag();
		for(BlockPos bindTarget : bindTargets) {
			list.add(NbtUtils.writeBlockPos(bindTarget));
		}
		cmp.put("Binds", list);
	}
	
	@Override
	public void readPacketNBT(CompoundTag cmp) {
		datum = Datum.load(cmp.getCompound("Datum"));
		
		bindTargets.clear();
		ListTag list = cmp.getList("Binds", 10);
		for(Tag tagg : list) {
			if(!(tagg instanceof CompoundTag taggg)) continue;
			bindTargets.add(NbtUtils.readBlockPos(taggg));
		}
	}
}
