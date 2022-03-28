package agency.highlysuspect.incorporeal.computer.capabilities;

import agency.highlysuspect.incorporeal.IncBlocks;
import agency.highlysuspect.incorporeal.block.CorporeaSolidifierBlock;
import agency.highlysuspect.incorporeal.computer.DatastoneBlock;
import agency.highlysuspect.incorporeal.computer.NotManaLens;
import agency.highlysuspect.incorporeal.computer.types.DataTypes;
import agency.highlysuspect.incorporeal.computer.types.Datum;
import agency.highlysuspect.incorporeal.corporea.RetainerDuck;
import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import agency.highlysuspect.incorporeal.mixin.CorporeaItemStackMatcherAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ComparatorBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.corporea.ICorporeaRequestMatcher;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.block.tile.corporea.TileCorporeaCrystalCube;
import vazkii.botania.common.block.tile.corporea.TileCorporeaIndex;
import vazkii.botania.common.block.tile.mana.TilePrism;
import vazkii.botania.common.item.lens.ItemLens;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
	public static @Nullable DatumAcceptor findDatumAcceptor(ServerLevel level, BlockPos pos, @Nullable BlockState state, @Nullable BlockEntity be, @Nullable List<Entity> entitiesInTheBlockspace, boolean directBind) {
		//Lookup the block entity if it wasn't provided
		if(be == null) be = level.getBlockEntity(pos);
		if(be != null) {
			//if it self-implements just return it
			if(be instanceof DatumAcceptor acceptor) return acceptor;
			
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
		
		//Read the blockstate if it's needed
		if(state == null) {
			if(be != null) state = be.getBlockState();
			else state = level.getBlockState(pos);
		}
		final BlockState s = state; //Lambda moment
		final BlockPos posCopy = pos.immutable();
		
		//mana voids: trash can
		if(s.getBlock() == ModBlocks.manaVoid) return datum -> {};
		
		//Corporea solidifiers -> produce ticket
		if(s.getBlock() == IncBlocks.CORPOREA_SOLIDIFIER) return datum -> IncBlocks.CORPOREA_SOLIDIFIER.receiveDatum(level, posCopy, datum);
		
		//datastone blocks: extend a stack of pointed datastones
		if(s.getBlock() instanceof DatastoneBlock db) return datum -> db.extendColumn(level, posCopy, datum);
		
		//Read the list of entities if it's needed
		if(entitiesInTheBlockspace == null) {
			entitiesInTheBlockspace = level.getEntities(null, new AABB(pos));
		}
		
		Collections.shuffle(entitiesInTheBlockspace, level.getRandom()); //don't prefer any given entity ordering
		
		for(Entity ent : entitiesInTheBlockspace) {
			//item frames: rotate according to the number passed in
			if(ent instanceof ItemFrame frame) return new FrameCaps(frame);
		}
		
		return null;
	}
	
	public static @Nullable DatumProvider findDatumProvider(ServerLevel level, BlockPos pos, @Nullable BlockState state, @Nullable BlockEntity be, @Nullable List<Entity> entitiesInTheBlockspace, boolean directBind) {
		if(be == null) be = level.getBlockEntity(pos);
		if(be != null) {
			//If it implements, self return
			if(be instanceof DatumProvider provider) return provider;
			
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
		final BlockPos posCopy = pos.immutable(); //also watch out for this
		
		//Mana voids -> empty
		if(s.getBlock() == ModBlocks.manaVoid) return () -> Datum.EMPTY;
		
		//Datastone blocks -> retract the column of pointed datastone
		if(s.getBlock() instanceof DatastoneBlock db) return () -> db.retractColumn(level, posCopy); 
		
		//Blockstate property reading is only possible when the funnel is directly bound to this block.
		//This helps prevent accidentally triggering the behavior, because it can be somewhat surprising.
		if(directBind) {
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
		}
		
		//Read the list of entities if it's needed
		if(entitiesInTheBlockspace == null) {
			entitiesInTheBlockspace = level.getEntities(null, new AABB(pos));
		}
		
		Collections.shuffle(entitiesInTheBlockspace, level.getRandom()); //don't prefer any given entity ordering
		
		for(Entity ent : entitiesInTheBlockspace) {
			//item frames -> request, as a Corporea Funnel would
			if(ent instanceof ItemFrame frame) return new FrameCaps(frame);
		}
		
		return null;
	}
	
	public static @Nullable DataLensProvider findDataLensProvider(Level level, BlockPos pos, @Nullable BlockState state, @Nullable BlockEntity be, boolean directBind) {
		if(be != null) be = level.getBlockEntity(pos);
		if(be instanceof TilePrism prism && ItemLens.getLens(prism.getItem(0)) instanceof NotManaLens dataLens) {
			return dataLens::getDataLens;
		}
		
		return null;
	}
	
	//Corporea funnels have this thing going on:
	//rotation 0 -> 1 item
	//rotation 1 -> 2 items
	//rotation 2 -> 4 items
	//rotation 3 -> 8 items
	//rotation 4 -> 16 items
	//rotation 5 -> 32 items
	//rotation 6 -> 48 items (!)
	//rotation 7 -> 64 items
	//This method implements the inverse function, rounding down to fill in the gaps.
	private static int magicNumberToFrameRotation(int magicNumber) {
		if(magicNumber >= 64) return 7;
		else if(magicNumber >= 48) return 6;
		else if(magicNumber >= 32) return 5;
		else if(magicNumber >= 16) return 4;
		else if(magicNumber >= 8) return 3;
		else if(magicNumber >= 4) return 2;
		else if(magicNumber >= 2) return 1;
		else return 0;
	}
	
	//And this method implements it forwards.
	private static int frameRotationToMagicNumber(int frameRotation) {
		return switch(frameRotation % 8) {
			case 0 -> 1;
			case 1 -> 2;
			case 2 -> 4;
			case 3 -> 8;
			case 4 -> 16;
			case 5 -> 32;
			case 6 -> 48;
			case 7 -> 64;
			default -> throw new IllegalArgumentException();
		};
	}
	
	private static record FrameCaps(ItemFrame frame) implements DatumAcceptor, DatumProvider, PositionTweakable {
		@Override
		public void acceptDatum(@NotNull Datum<?> datum) {
			if(datum.type() == DataTypes.INTEGER) {
				int oldRotation = frame.getRotation();
				int rotation = magicNumberToFrameRotation(datum.castAndGet());
				frame.setRotation(rotation);
				
				if(oldRotation != rotation) {
					frame.playSound(frame.getRotateItemSound(), 1f, 1f);
				}
			}
		}
		
		@Override
		public @NotNull Datum<?> readDatum() {
			return DataTypes.SOLIDIFIED_REQUEST.datumOf(SolidifiedRequest.create(
				frame.getItem(),
				frameRotationToMagicNumber(frame.getRotation())
			));
		}
		
		@Override
		public Vec3 tweakPosition(Level level, BlockPos pos) {
			return frame.getBoundingBox().getCenter();
		}
	}
}
