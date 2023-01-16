package agency.highlysuspect.incorporeal.block.entity;

import agency.highlysuspect.incorporeal.IncBlockEntityTypes;
import agency.highlysuspect.incorporeal.util.MoreNbtHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.botania.api.block.WandBindable;
import vazkii.botania.api.corporea.CorporeaHelper;
import vazkii.botania.api.corporea.CorporeaSpark;
import vazkii.botania.common.block.block_entity.BotaniaBlockEntity;

import java.util.HashSet;
import java.util.Set;

/**
 * The Corporea Pylon.
 * You can bind it to (multiple) other Corporea Pylons using the Wand of the Forest, over a very large range.
 * Any Corporea Sparks placed atop a pair of bound pylons act like they are connected.
 */
public class CorporeaPylonBlockEntity extends BotaniaBlockEntity implements WandBindable, Multibindable {
	public CorporeaPylonBlockEntity(BlockPos pos, BlockState state) {
		super(IncBlockEntityTypes.CORPOREA_PYLON, pos, state);
	}
	
	//User-editable bind targets, created using the Wand of the Forest.
	private Set<BlockPos> wandBindings = new HashSet<>();
	
	public static void tick(Level level, BlockPos pos, BlockState state, CorporeaPylonBlockEntity self) {
		//yeah im sick of ticker abstraction sorry
		self.tick();
	}
	
	private void tick() {
		if(level == null) return;
		
		CorporeaSpark mySpark = getSpark();
		if(mySpark == null) return; //Nothing to do
		
		for(BlockPos binding : wandBindings) {
			if(!level.isLoaded(binding)) continue;
			
			//If there is a corporea pylon at the binding location...
			BlockEntity bindTarget = level.getBlockEntity(binding);
			if(!(bindTarget instanceof CorporeaPylonBlockEntity otherPylon)) continue; 
			
			//...and it has a spark...
			CorporeaSpark theirSpark = otherPylon.getSpark();
			if(theirSpark == null) continue;
			
			//...register a binding between them
			boolean isNew = false;
			isNew |= ((ICorporeaSparkExt) mySpark).registerRemoteBinding(theirSpark);
			isNew |= ((ICorporeaSparkExt) theirSpark).registerRemoteBinding(mySpark);
			
			if(isNew) {
				//Refresh the network bindings
				((ICorporeaSparkExt) mySpark).inc$findNetwork();
				((ICorporeaSparkExt) theirSpark).inc$findNetwork();
			}
		}
	}
	
	public CorporeaSpark getSpark() {
		//The BlockEntity is at the bottom of the Corporea Pylon, which is three blocks tall.
		//The corporea spark is attached to the block two blocks above myself.
		CorporeaSpark spark = CorporeaHelper.instance().getSparkForBlock(level, getBlockPos().above(2));
		if(spark instanceof ICorporeaSparkExt) return spark; //Unchecked casts to this type are performed later
		else return null;
	}
	
	@Override
	public Set<BlockPos> getBindings() {
		return wandBindings;
	}
	
	@Override
	public int getBindRange() {
		return 128;
	}
	
	@Override
	public void writePacketNBT(CompoundTag cmp) {
		super.writePacketNBT(cmp);
		cmp.put("Binds", MoreNbtHelpers.writeBlockPosSet(wandBindings));
	}
	
	@Override
	public void readPacketNBT(CompoundTag cmp) {
		super.readPacketNBT(cmp);
		wandBindings = MoreNbtHelpers.readBlockPosSet(cmp.getList("Binds", MoreNbtHelpers.BLOCKPOS_SET_TYPE));
	}
	
	public interface ICorporeaSparkExt {
		/**
		 * Returns true if the extra binding is new, or false if it was already known
		 */
		boolean registerRemoteBinding(CorporeaSpark other);
		
		//Invoker
		void inc$findNetwork();
	}
}
