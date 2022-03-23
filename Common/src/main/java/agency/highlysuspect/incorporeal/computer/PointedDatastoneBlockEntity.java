package agency.highlysuspect.incorporeal.computer;

import agency.highlysuspect.incorporeal.IncBlockEntityTypes;
import agency.highlysuspect.incorporeal.computer.capabilities.DatumAcceptor;
import agency.highlysuspect.incorporeal.computer.capabilities.DatumProvider;
import agency.highlysuspect.incorporeal.computer.types.Datum;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.common.block.tile.TileMod;

public class PointedDatastoneBlockEntity extends TileMod implements DatumAcceptor, DatumProvider {
	public PointedDatastoneBlockEntity(BlockPos pos, BlockState state) {
		super(IncBlockEntityTypes.POINTED_DATASTONE, pos, state);
	}
	
	private @NotNull Datum<?> datum = Datum.EMPTY;
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
	
	public int signal() {
		return signal;
	}
	
	@Override
	public void writePacketNBT(CompoundTag cmp) {
		cmp.put("Datum", datum.save());
	}
	
	@Override
	public void readPacketNBT(CompoundTag cmp) {
		datum = Datum.load(cmp.getCompound("Datum"));
		signal = datum.signal();
	}
}
