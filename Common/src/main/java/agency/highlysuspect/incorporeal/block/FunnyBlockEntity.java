package agency.highlysuspect.incorporeal.block;

import agency.highlysuspect.incorporeal.Bigfunny;
import agency.highlysuspect.incorporeal.net.FunnyEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.subtile.RadiusDescriptor;
import vazkii.botania.api.subtile.TileEntityFunctionalFlower;

import java.util.EnumMap;
import java.util.Map;

/**
 * "If this shit post gets 50 :HYPERJOKE: reactions I will create a functional flower that plays
 * noteblock midi despacito and put it in incorporeal" - quat, Aug 8 2018, unaware it's about
 * to make the biggest mistake of its life.
 * 
 * It's been fucking years. This flower isn't even funny anymore.
 * 
 * All Botania flowers are required by intergalactic law to be named after a shitty pun off a
 * real life flower, and the Sweet Alexum is named after the Sweet Alyssum.
 */
public class FunnyBlockEntity extends TileEntityFunctionalFlower {
	public FunnyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int range, int ticksBetweenNotes, int pitchShift, double sparkleHeight) {
		super(type, pos, state);
		this.range = range;
		this.ticksBetweenNotes = ticksBetweenNotes;
		this.pitchShift = pitchShift;
		this.sparkleHeight = sparkleHeight;
	}
	
	public static FunnyBlockEntity big(BlockPos pos, BlockState state) {
		return new FunnyBlockEntity(IncBlockEntityTypes.FUNNY_BIG, pos, state, 4, 4, 0, 0.75);
	}
	
	public static FunnyBlockEntity small(BlockPos pos, BlockState state) {
		return new FunnyBlockEntity(IncBlockEntityTypes.FUNNY_BIG, pos, state, 2, 3, 7, 0.6);
	}
	
	private final int range;
	private final int ticksBetweenNotes;
	private final int pitchShift;
	private final double sparkleHeight;
	
	private final int NOTE_MANA_COST = 10;
	
	private int clock = -1;
	
	@Override
	public void tickFlower() {
		super.tickFlower();
		
		assert level != null;
		if(level.isClientSide()) return;
		
		BlockPos pos = getEffectivePos();
		
		if(redstoneSignal == 15) {
			if(clock != -1) setChanged();
			clock = -1; return;
		} else if(redstoneSignal > 0 || getMana() < NOTE_MANA_COST) {
			return;
		}
		
		clock++;
		setChanged();
		
		int ticksBetween = ticksBetweenNotes / (overgrowth || overgrowthBoost ? 2 : 1);
		if(ticksBetween == 0) ticksBetween = 1;
		
		int tick = clock;
		if(tick < 0 || tick % ticksBetween != 0) return;
		tick /= ticksBetween;
		
		//Find the nearby instruments 
		Map<NoteBlockInstrument, BlockPos> insts = findInsts(pos);
		
		//play music and draw particle effect
		FunnyEffect effect = new FunnyEffect(pos, sparkleHeight);
		
		boolean dirtyMana = false;
		//noinspection ConstantConditions
		dirtyMana |= doIt(tick, effect, insts, NoteBlockInstrument.FLUTE);
		dirtyMana |= doIt(tick, effect, insts, NoteBlockInstrument.SNARE);
		dirtyMana |= doIt(tick, effect, insts, NoteBlockInstrument.BASEDRUM);
		dirtyMana |= doIt(tick, effect, insts, NoteBlockInstrument.BASS);
		
		if(dirtyMana) sync();
		if(!effect.isEmpty() && level instanceof ServerLevel slevel) {
			effect.sendToAllWatching(slevel, pos);
		}
	}
	
	private EnumMap<NoteBlockInstrument, BlockPos> findInsts(BlockPos pos) {
		EnumMap<NoteBlockInstrument, BlockPos> result = new EnumMap<>(NoteBlockInstrument.class);
		assert level != null;
		
		for(BlockPos bp : BlockPos.betweenClosed(pos.offset(-range, 0, -range), pos.offset(range, 1, range))) {
			BlockState state = level.getBlockState(bp);
			if(state.hasProperty(BlockStateProperties.NOTEBLOCK_INSTRUMENT)) {
				result.put(state.getValue(BlockStateProperties.NOTEBLOCK_INSTRUMENT), bp.immutable());
			}
		}
		
		return result;
	}
	
	//returns whether the mana level changed, requiring a sync
	private boolean doIt(int tick, FunnyEffect sparkleData, Map<NoteBlockInstrument, BlockPos> insts, NoteBlockInstrument inst) {
		assert level != null;
		if(getMana() < NOTE_MANA_COST) return false;
		
		BlockPos noteblockPos = insts.get(inst);
		if(noteblockPos == null) return false;
		
		//@Nullable ("primitive type members cannot be annotated" What the fuck)
		byte[] notes = Bigfunny.notesForTick(tick, inst);
		if(notes == null) return false;
		
		sparkleData.addLineTo(noteblockPos, notes);
		
		for(int note : notes) {
			if(getMana() > NOTE_MANA_COST) {
				addMana(-NOTE_MANA_COST);
				float convertedPitch = (float) Math.pow(2, (note - 12 + pitchShift) / 12d);
				level.playSound(null, noteblockPos, inst.getSoundEvent(), SoundSource.RECORDS, 3f, convertedPitch);
			}
		}
		
		return true;
	}
	
	@Override
	public boolean acceptsRedstone() {
		return true;
	}
	
	@Override
	public void readFromPacketNBT(CompoundTag tag) {
		super.readFromPacketNBT(tag);
		clock = tag.getInt("Clock");
	}
	
	@Override
	public void writeToPacketNBT(CompoundTag tag) {
		super.writeToPacketNBT(tag);
		tag.putInt("Clock", clock);
	}
	
	@Override
	public int getMaxMana() {
		return 2000;
	}
	
	@Override
	public int getColor() {
		return 0xbb4422;
	}
	
	@Nullable
	@Override
	public RadiusDescriptor getRadius() {
		return new RadiusDescriptor.Square(getEffectivePos(), range);
	}
}
