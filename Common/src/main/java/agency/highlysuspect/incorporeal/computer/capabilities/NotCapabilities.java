package agency.highlysuspect.incorporeal.computer.capabilities;

import agency.highlysuspect.incorporeal.computer.NotManaLens;
import agency.highlysuspect.incorporeal.computer.types.DataTypes;
import agency.highlysuspect.incorporeal.computer.types.Datum;
import agency.highlysuspect.incorporeal.corporea.RetainerDuck;
import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import agency.highlysuspect.incorporeal.mixin.CorporeaItemStackMatcherAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ComparatorBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.corporea.ICorporeaRequestMatcher;
import vazkii.botania.common.block.tile.corporea.TileCorporeaCrystalCube;
import vazkii.botania.common.block.tile.corporea.TileCorporeaIndex;
import vazkii.botania.common.block.tile.mana.TileManaVoid;
import vazkii.botania.common.block.tile.mana.TilePrism;
import vazkii.botania.common.item.lens.ItemLens;

import java.util.Collection;

/**
 * Hey look, it's stuff that I used forge capailities/AttachCapabilitiesEvent for in 1.12 and 1.16.
 * That was kinda frickin pointless, because it's not like any other mod is ever going to add more of these.
 * 
 * If you REALLY want to add mod compat and wish I used a caps system, just hack up this class with mixin lmfao.
 * It's not like I have a leg to stand on about that being a bad idea. Have you seen the crap I do to Botania?
 */
public class NotCapabilities {
	//Pass state/be if you already have them, otherwise they'll be looked up from level/pos by the usual methods.
	//Yes, this has the same shape as fabric block-api-lookup stuff. I mean, it's good stuff, lol.
	public static @Nullable DatumAcceptor findDatumAcceptor(Level level, BlockPos pos, @Nullable BlockState state, @Nullable BlockEntity be) {
		//Lookup the block entity if it wasn't provided
		if(be == null) be = level.getBlockEntity(pos);
		if(be != null) {
			//if it self-implements just return it
			if(be instanceof DatumAcceptor acceptor) return acceptor;
			
			//Mana voids: trash can
			if(be instanceof TileManaVoid) return datum -> {};
			
			//Corporea retainers: set their retained stuff
			if(be instanceof RetainerDuck quack) return datum -> {
				if(datum.type() == DataTypes.EMPTY) {
					//Clear the retainer. I think this is a satisfactory way?
					//mainly I'm worried about "request for 0 of nothing" vs. "actually *cleared* in an observable way"
					quack.inc$setMatcher(null);
					quack.inc$setCount(0);
				}
				
				//Set the count/matcher independently or both at the same time
				else if(datum.type() == DataTypes.INTEGER) quack.inc$setCount(datum.castAndGet());
				else if(datum.type() == DataTypes.MATCHER) quack.inc$setMatcher(datum.castAndGet());
				else if(datum.type() == DataTypes.SOLIDIFIED_REQUEST) quack.inc$liquifactRequest(datum.castAndGet());
			};
			
			//Corporea indices: perform corporea requests
			if(be instanceof TileCorporeaIndex index) return datum -> {
				if(datum.type() == DataTypes.SOLIDIFIED_REQUEST) {
					SolidifiedRequest request = datum.castAndGet();
					index.doCorporeaRequest(request.matcher(), request.count(), index.getSpark());
				}
			};
			
			//Corporea crystal cubes: set (or clear) the item that they display
			if(be instanceof TileCorporeaCrystalCube cube) return datum -> {
				if(datum.type() == DataTypes.EMPTY) {
					cube.setRequestTarget(ItemStack.EMPTY); //Clear it
					return;
				}
				
				ICorporeaRequestMatcher matcher;
				if(datum.type() == DataTypes.SOLIDIFIED_REQUEST) matcher = datum.<SolidifiedRequest>castAndGet().matcher();
				else if(datum.type() == DataTypes.MATCHER) matcher = datum.castAndGet();
				else return;
				
				if(matcher instanceof CorporeaItemStackMatcherAccessor ismAccessor) {
					//Only requests for item stacks can be displayed in the crystal cube.
					cube.setRequestTarget(ismAccessor.inc$getMatch());
				}
			};
		}
		
		return null;
	}
	
	public static @Nullable DatumProvider findDatumProvider(Level level, BlockPos pos, @Nullable BlockState state, @Nullable BlockEntity be) {
		if(be == null) be = level.getBlockEntity(pos);
		if(be != null) {
			//If it implements, self return
			if(be instanceof DatumProvider provider) return provider;
			
			//Mana voids -> empty
			if(be instanceof TileManaVoid) return () -> Datum.EMPTY;
			
			//Corporea retainers -> their pending request, if they have one
			if(be instanceof RetainerDuck quack) return () -> {
				if(quack.inc$hasPendingRequest()) return DataTypes.SOLIDIFIED_REQUEST.datumOf(quack.inc$asSolidifiedRequest());
				else return Datum.EMPTY;
			};
			
			//Corporea crystal cubes -> a corporea request, encapsulating its displayed item + count
			if(be instanceof TileCorporeaCrystalCube cube) return () -> {
				ItemStack target = cube.getRequestTarget();
				if(target.isEmpty()) return Datum.EMPTY;
				else return DataTypes.SOLIDIFIED_REQUEST.datumOf(SolidifiedRequest.create(target, cube.getItemCount()));
			};
			
			//Comparators -> their output signal (mojang use a blockstate for this plsssssssss)
			if(be instanceof ComparatorBlockEntity comparator) return () -> DataTypes.INTEGER.datumOf(comparator.getOutputSignal());
		}
		
		//Read the blockstate if it's needed
		if(state == null) {
			if(be != null) state = be.getBlockState();
			else state = level.getBlockState(pos);
		}
		final BlockState s = state; //Lambda moment
		
		//Just look at the first integer property on this BlockState and hope it's a cool one!!!
		//Basically I tried to allowlist a bunch of interesting properties from BlockStateProperties, but it turned out to be pretty much all of them lmao...
		//Lots of fun stuff about power levels, crop ages, note block tunings, ...
		Collection<Property<?>> props = s.getProperties(); //This method allocates smh
		for(Property<?> prop : props) {
			if(prop instanceof IntegerProperty intProp) return () -> DataTypes.INTEGER.datumOf(s.getValue(intProp));
		}
		
		//Iterating twice is intentional btw; I want to prefer numbers to booleans if there's both
		for(Property<?> prop : props) {
			if(prop instanceof BooleanProperty boolProp) return () -> DataTypes.INTEGER.datumOf(s.getValue(boolProp) ? 1 : 0);
		}
		
		return null;
	}
	
	public static @Nullable DataLensProvider findDataLensProvider(Level level, BlockPos pos, @Nullable BlockState state, @Nullable BlockEntity be) {
		if(be != null) be = level.getBlockEntity(pos);
		if(be instanceof TilePrism prism && ItemLens.getLens(prism.getItem(0)) instanceof NotManaLens dataLens) {
			return dataLens::getDataLens;
		}
		
		return null;
	}
}
