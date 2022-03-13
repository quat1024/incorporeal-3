package agency.highlysuspect.incorporeal.computer;

import agency.highlysuspect.incorporeal.block.entity.IncBlockEntityTypes;
import agency.highlysuspect.incorporeal.computer.types.DataType;
import agency.highlysuspect.incorporeal.computer.types.DataTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.common.block.tile.TileMod;

import java.util.Objects;

public class DataStorageBlockEntity extends TileMod {
	public DataStorageBlockEntity(BlockPos $$1, BlockState $$2) {
		super(IncBlockEntityTypes.DATA_STORAGE, $$1, $$2);
	}
	
	private @NotNull DataType.Datum<?> datum = DataType.Datum.EMPTY;
	
	public DataType.Datum<?> getDatum() {
		return datum;
	}
	
	public void setDatum(@NotNull DataType.Datum<?> datum) {
		boolean changed = !Objects.equals(this.datum, datum);
		
		this.datum = datum;
		
		if(changed) {
			setChanged();
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
		}
	}
	
	@Override
	public void writePacketNBT(CompoundTag cmp) {
		cmp.put("data", datum.save());
	}
	
	@Override
	public void readPacketNBT(CompoundTag cmp) {
		datum = DataType.Datum.load(cmp.getCompound("data"));
	}
}
