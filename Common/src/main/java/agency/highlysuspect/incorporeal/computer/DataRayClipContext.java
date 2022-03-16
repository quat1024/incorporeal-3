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
import java.util.function.Function;

/**
 * This one Minecraft method works in a very particular way:
 * net.minecraft.world.level.BlockGetter#traverseBlocks(Vec3, Vec3, Object, BiFunction, Function),
 * and this class is designed around its idiosyncrasies.
 * 
 * It takes two type parameters, which I'll name <CTX, RESULT>.
 * The full signature is something like:
 *    (Vec3 start, Vec3 end, CTX ctx, BiFunction<CTX, BlockPos, @Nullable RESULT> step, Function<CTX, RESULT> miss).
 * For some context where this method is used: in vanilla, RESULT is always the type "BlockHitResult".
 * 
 * `ctx`: Any type you want. Here, it's DataRayClipContext.
 * `step`: If you return non-null, traverseBlocks returns that value. If you return null, raycasting continues to the next block.
 * `miss`: If the raycast reached the end without `step` returning non-null, this method is invoked to get the final return value.
 * 
 * In this ctx-class implementation, CTX and RESULT are the same type. It's also a little unusal for the CTX class to be mutable,
 * but I want to trace along the whole ray and pick up information along the way, instead of finding a single intersection point.
 */
public class DataRayClipContext {
	public DataRayClipContext(Level level, BlockPos start, BlockPos end) {
		this.level = level;
		this.start = start;
		this.end = end;
	}
	
	public static DataRayClipContext performClip(Level level, BlockPos start, BlockPos end) {
		return BlockGetter.traverseBlocks(
			//start, end
			Vec3.atCenterOf(start), Vec3.atCenterOf(end),
			//ctx
			new DataRayClipContext(level, start, end),
			//step, miss
			DataRayClipContext::step, Function.identity()
		);
	}
	
	public final Level level;
	public final BlockPos start;
	public final BlockPos end;
	
	public final List<Pairing> pairings = new ArrayList<>(4); //assume there's not many
	public @Nullable ProviderEntry unpairedProvider;
	
	public DataRayClipContext step(BlockPos cursor) {
		//Look up the aspects of whatever's at this location.
		BlockEntity be = level.getBlockEntity(cursor);
		BlockState state = be == null ? null : be.getBlockState();
		boolean isDirectBinding = start.equals(cursor);
		@Nullable DatumAcceptor acceptor = NotCapabilities.findDatumAcceptor(level, cursor, state, be, isDirectBinding);
		@Nullable DatumProvider provider = NotCapabilities.findDatumProvider(level, cursor, state, be, isDirectBinding);
		@Nullable DataLensProvider lensProvider = NotCapabilities.findDataLensProvider(level, cursor, state, be, isDirectBinding);
		
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
		
		//Always continue to the next
		return null;
	}
	
	//I apologize in advance for the garbage collector pressure...
	public static record LensEntry(DataLens lens, Vec3 pos) {}
	public static record ProviderEntry(DatumProvider provider, Vec3 pos, ArrayList<LensEntry> lenses) {}
	public static record AcceptorEntry(DatumAcceptor acceptor, Vec3 pos) {}
	public static record Pairing(ProviderEntry provider, AcceptorEntry acceptor) {}
}
