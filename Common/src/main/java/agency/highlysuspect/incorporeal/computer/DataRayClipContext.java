package agency.highlysuspect.incorporeal.computer;

import agency.highlysuspect.incorporeal.computer.capabilities.DataLensProvider;
import agency.highlysuspect.incorporeal.computer.capabilities.DatumAcceptor;
import agency.highlysuspect.incorporeal.computer.capabilities.DatumProvider;
import agency.highlysuspect.incorporeal.computer.capabilities.NotCapabilities;
import agency.highlysuspect.incorporeal.computer.types.DataLens;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * This method works in a very particular way:
 * @see net.minecraft.world.level.BlockGetter#traverseBlocks(Vec3, Vec3, Object, BiFunction, Function)
 * and this class is designed around its idiosyncrasies.
 * 
 * The third parameter is any arbitrary context parameter, here DataRayClipContext.
 * The fourth parameter is (context, blockpos) -> @Nullable result; here the result is also DataRayClipContext, for convenience.
 *   - If a null result is returned, the raycast continues into the next block space.
 *   - If non-null result is returned, the raycast is exited with that value.
 *   This method is implemented by "step".
 * The fifth paramter is (context) -> result, which is called
 *   - when the raycast misses all blocks (the BiFunction always returned null)
 *   - when the raycast has a very short (basically 0) length
 *   This method is implemented by "finish".
 */
public class DataRayClipContext {
	public DataRayClipContext(Level level, BlockPos ending) {
		this.level = level;
		this.ending = ending;
	}
	
	public static DataRayClipContext performClip(Level level, BlockPos start, BlockPos end) {
		return BlockGetter.traverseBlocks(
			Vec3.atCenterOf(start), Vec3.atCenterOf(end),
			new DataRayClipContext(level, end),
			DataRayClipContext::step, DataRayClipContext::finish
		);
	}
	
	public final Level level;
	public final BlockPos ending;
	
	public final List<Pairing> pairings = new ArrayList<>(4); //assume there's not many
	public @Nullable ProviderEntry unpairedProvider;
	
	public DataRayClipContext step(BlockPos cursor) {
		//Look up the aspects of whatever's at this location.
		BlockEntity be = level.getBlockEntity(cursor);
		BlockState state = be == null ? null : be.getBlockState();
		@Nullable DatumAcceptor acceptor = NotCapabilities.findDatumAcceptor(level, cursor, state, be);
		@Nullable DatumProvider provider = NotCapabilities.findDatumProvider(level, cursor, state, be);
		@Nullable DataLensProvider lensProvider = NotCapabilities.findDataLensProvider(level, cursor, state, be);
		
		if(unpairedProvider != null) {
			//If there is a lens at this location, tack it on to the list of lens effects applied to this provider
			if(lensProvider != null) {
				LensEntry lensEntry = new LensEntry(lensProvider.getLens(), lensProvider.tweakPosition(level, cursor));
				unpairedProvider.lenses.add(lensEntry);
			}
			
			//If there is an acceptor at this location, and I have a provider to pair with it, establish the link.
			if(acceptor != null) {
				AcceptorEntry acceptorEntry = new AcceptorEntry(acceptor, acceptor.tweakPosition(level, cursor));
				pairings.add(new Pairing(unpairedProvider, acceptorEntry));
				unpairedProvider = null; //start over
			}
		}
		
		//If there is a provider, move the cursor to it.
		if(provider != null) {
			unpairedProvider = new ProviderEntry(provider, provider.tweakPosition(level, cursor), new ArrayList<>());
		}
		
		return null;
	}
	
	public DataRayClipContext finish() {
		return this;
	}
	
	//I apologize in advance for the garbage collector pressure...
	public static record LensEntry(DataLens lens, Vec3 pos) {}
	public static record ProviderEntry(DatumProvider provider, Vec3 pos, ArrayList<LensEntry> lenses) {}
	public static record AcceptorEntry(DatumAcceptor acceptor, Vec3 pos) {}
	public static record Pairing(ProviderEntry provider, AcceptorEntry acceptor) {}
}
