package agency.highlysuspect.incorporeal.block.entity;

import agency.highlysuspect.incorporeal.IncBlockEntityTypes;
import agency.highlysuspect.incorporeal.computer.DataRayClipContext;
import agency.highlysuspect.incorporeal.computer.capabilities.DatumAcceptor;
import agency.highlysuspect.incorporeal.computer.types.DataReducers;
import agency.highlysuspect.incorporeal.computer.types.Datum;
import agency.highlysuspect.incorporeal.net.DataFunnelEffect;
import agency.highlysuspect.incorporeal.net.IncNetwork;
import agency.highlysuspect.incorporeal.util.MoreNbtHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DataFunnelBlockEntity extends DataStorageBlockEntity implements Multibindable {
	public DataFunnelBlockEntity(BlockPos pos, BlockState state) {
		super(IncBlockEntityTypes.DATA_FUNNEL, pos, state);
	}
	
	private Set<BlockPos> bindTargets = new HashSet<>();
	
	@Override
	public Set<BlockPos> getBindings() {
		return bindTargets;
	}
	
	@Override
	public int getBindRange() {
		return 12;
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
	public void writePacketNBT(CompoundTag cmp) {
		super.writePacketNBT(cmp);
		cmp.put("Binds", MoreNbtHelpers.writeBlockPosSet(bindTargets));
	}
	
	@Override
	public void readPacketNBT(CompoundTag cmp) {
		super.readPacketNBT(cmp);
		bindTargets = MoreNbtHelpers.readBlockPosSet(cmp.getList("Binds", MoreNbtHelpers.BLOCKPOS_SET_TYPE));
	}
}
