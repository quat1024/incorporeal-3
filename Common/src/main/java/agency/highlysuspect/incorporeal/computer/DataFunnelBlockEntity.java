package agency.highlysuspect.incorporeal.computer;

import agency.highlysuspect.incorporeal.IncBlockEntityTypes;
import agency.highlysuspect.incorporeal.computer.capabilities.DatumAcceptor;
import agency.highlysuspect.incorporeal.computer.capabilities.DatumProvider;
import agency.highlysuspect.incorporeal.computer.types.DataReducers;
import agency.highlysuspect.incorporeal.computer.types.Datum;
import agency.highlysuspect.incorporeal.net.DataFunnelEffect;
import agency.highlysuspect.incorporeal.net.IncNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.block.IWandBindable;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.common.block.tile.TileMod;
import vazkii.botania.common.helper.MathHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class DataFunnelBlockEntity extends TileMod implements IWandBindable, DatumAcceptor, DatumProvider {
	public DataFunnelBlockEntity(BlockPos pos, BlockState state) {
		super(IncBlockEntityTypes.DATA_FUNNEL, pos, state);
	}
	
	private final Set<BlockPos> bindTargets = new HashSet<>();
	private Datum<?> datum = Datum.EMPTY;
	private int signal = 0;
	
	@Override
	public void acceptDatum(@NotNull Datum<?> datum) {
		boolean changed = !this.datum.equals(datum);
		
		this.datum = datum;
		this.signal = datum.signal();
		
		if(changed) {
			setChanged();
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
		}
	}
	
	@Override
	public @NotNull Datum<?> readDatum() {
		return datum;
	}
	
	public Set<BlockPos> getBindings() {
		return bindTargets;
	}
	
	public int signal() {
		return signal;
	}
	
	public void doIt() {
		assert level != null;
		
		if(level.isClientSide) return;
		ServerLevel slevel = (ServerLevel) level; 
		
		List<Datum<?>> leftoverData = new ArrayList<>(bindTargets.size()); //upper bound
		DataFunnelEffect effect = new DataFunnelEffect();
		
		for(BlockPos bindingPos : bindTargets) {
			//Perform the ridiculously complicated raycast operation
			DataRayClipContext clip = DataRayClipContext.performClip(slevel, bindingPos, worldPosition);
			//Ok now I gotta sift through all that data!!!
			
			//Iterate through the linkages (maybe this should be done in reverse order idk, will change the gameplay)
			for(DataRayClipContext.Pairing pairing : clip.pairings) {
				DataRayClipContext.ProviderEntry providerEntry = pairing.provider();
				
				Vec3 sparkleStart = providerEntry.pos();
				Datum<?> datum = providerEntry.provider().readDatum();
				
				//Apply lens effects along the way, dragging the datum through the lens.
				for(DataRayClipContext.LensEntry lensEntry : providerEntry.lenses()) {
					effect.addLine(sparkleStart, lensEntry.pos(), datum.color());
					sparkleStart = lensEntry.pos();
					datum = lensEntry.lens().filter(datum);
				}
				
				//Insert the resulting datum into the acceptor...
				DataRayClipContext.AcceptorEntry acceptorEntry = pairing.acceptor();
				effect.addLine(sparkleStart, acceptorEntry.pos(), datum.color());
				DatumAcceptor acceptor = acceptorEntry.acceptor();
				if(acceptor == this && !datum.isEmpty()) {
					//Unless it's me because I need to combine and reduce them instead :eyes:
					leftoverData.add(datum);
				} else {
					acceptor.acceptDatum(datum);
				}
			}
		}
		
		//Reduce the leftover datums and store the result in myself
		acceptDatum(DataReducers.reduce(leftoverData));
		
		//Do an effect tm
		if(!effect.isEmpty()) IncNetwork.sendToAllWatching(effect, slevel, worldPosition);
	}
	
	@Override
	public boolean canSelect(Player player, ItemStack wand, BlockPos pos, Direction side) {
		return true;
	}
	
	@Override
	public boolean bindTo(Player player, ItemStack wand, BlockPos target, Direction side) {
		//binding to the same target again will unbind
		if(bindTargets.contains(target)) { //linear scan smh
			bindTargets.remove(target); //linear scan smh
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
		return new ArrayList<>(bindTargets).get(lol++ % bindTargets.size());
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
		signal = datum.signal();
		
		bindTargets.clear();
		ListTag list = cmp.getList("Binds", 10);
		for(Tag tagg : list) {
			if(!(tagg instanceof CompoundTag taggg)) continue;
			bindTargets.add(NbtUtils.readBlockPos(taggg));
		}
	}
}
