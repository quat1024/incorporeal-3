package agency.highlysuspect.incorporeal.computer;

import agency.highlysuspect.incorporeal.block.entity.IncBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.common.block.tile.TileMod;

import java.util.Objects;

public class DataStorageBlockEntity extends TileMod {
	public DataStorageBlockEntity(BlockPos $$1, BlockState $$2) {
		super(IncBlockEntityTypes.DATA_STORAGE, $$1, $$2);
	}
	
	private @Nullable Object data;
	
	public @Nullable Object getData() {
		return data;
	}
	
	public void setData(@Nullable Object data) {
		boolean changed = !Objects.equals(this.data, data);
		
		this.data = data;
		
		if(changed) {
			setChanged();
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
		}
	}
	
	@Override
	public void writePacketNBT(CompoundTag cmp) {
		if(data != null) cmp.put("data", DataTypes.save(data));
	}
	
	@Override
	public void readPacketNBT(CompoundTag cmp) {
		data = DataTypes.load(cmp);
	}
}
