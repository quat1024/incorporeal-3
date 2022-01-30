package agency.highlysuspect.incorporeal.block.entity;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.NotVanillaPacketDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.block.IWandable;

public class UnstableCubeBlockEntity extends BlockEntity implements IWandable {
	public UnstableCubeBlockEntity(BlockPos pos, BlockState state) {
		super(IncBlockEntityTypes.UNSTABLE_CUBE, pos, state);
	}
	
	private float serverSpeed; //Synced to clients (other than the puncher's client) when someone punches the unstable cube.
	private int power; //redstone power
	
	//Used on client only.
	public float angle;
	public float speed;
	public float bump;
	public long nextLightningTick = 0;
	
	public static void serverTick(Level level, BlockPos pos, BlockState state, UnstableCubeBlockEntity self) {
		if(self.serverSpeed == 0) self.serverSpeed = 8;
		if(self.serverSpeed > 1f) {
			self.serverSpeed *= 0.96;
			level.blockEntityChanged(pos); //setChanged(), without the comparator update
		}
		
		int newPower = Mth.clamp(Mth.floor(Inc.rangeRemap(self.serverSpeed, 0, 90, 0, 15)), 0, 15);
		if(self.power != newPower) {
			self.power = newPower;
			self.setChanged();
		}
	}
	
	//TODO set up the capability thingie.
	@Override
	public boolean onUsedByWand(@Nullable Player player, ItemStack stack, Direction side) {
		punch(player);
		return true;
	}
	
	public void punch(@Nullable Player puncher) {
		serverSpeed = Mth.clamp(serverSpeed + 15, 0, 200);
		setChanged();
		
		if(level == null) return;
		
		if(level.isClientSide()) {
			bump = 1;
			nextLightningTick = level.getGameTime();
		} else {
			NotVanillaPacketDispatcher.dispatchToNearbyPlayersExcept(this, puncher);
		}
	}
	
	@Nullable
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}
	
	@Override
	protected void saveAdditional(CompoundTag tag) {
		tag.putFloat("speed", serverSpeed);
	}
	
	@Override
	public void load(CompoundTag tag) {
		//Also set the client speed, since this gets called thru getUpdatePacket.
		serverSpeed = speed = tag.getFloat("speed");
	}
	
	public int getPower() {
		return power;
	}
}
