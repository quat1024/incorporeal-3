package agency.highlysuspect.incorporeal.block;

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
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.block.IWandable;

/**
 * The Unstable Cube block entity. This is a bit of a strange one, mainly because of how syncing the speed works.
 * 
 * Clearly, when a player clicks the Unstable Cube, it should start to spin. Other players should be able to see
 * the spinning cube, and I should also know the speed on the server side, so it can emit a redstone signal. The
 * trick here is in how that data is synced.
 * 
 * In order to produce a spinning animation, I could simply send the angle of the cube to every player in the area
 * every tick, but that is super pointless, wastes a bunch of bandwidth and creates a crappy looking animation over
 * networked MP. Instead, I keep track of the cube's rotational speed on the server and client separately.
 * 
 * Additionally I don't want the cube to suddenly snap from one angle to another if I can avoid it, which means I
 * want to avoid the server ever telling the client the exact angle of the cube. Instead I only ever sync its rotational
 * speed. And finally I don't bother syncing it to the player who did click the cube, and let their own client
 * take care of it, to avoid a round-trip.
 * 
 * This is a lot of effort put into a block that mayyyybe 10 people will place in their world, ever.
 */
public class UnstableCubeBlockEntity extends BlockEntity implements IWandable {
	public UnstableCubeBlockEntity(DyeColor color, BlockPos pos, BlockState state) {
		super(IncBlockEntityTypes.UNSTABLE_CUBES.get(color), pos, state);
	}
	
	private float serverSpeed; //Synced to clients (other than the puncher's client) when someone punches the unstable cube.
	private int power; //redstone power
	
	//Used on client only, fields exist on the server too for code simplicity I guess.
	public float angle;
	public float speed;
	public float bump;
	public final float bumpDecay = 0.8f;
	public long nextLightningTick = 0;
	
	public static void serverTick(Level level, BlockPos pos, BlockState state, UnstableCubeBlockEntity self) {
		if(self.serverSpeed == 0) self.serverSpeed = 8;
		if(self.serverSpeed > 1f) {
			self.serverSpeed *= 0.96;
			//setChanged() without a comparator update.
			//This method gets called pretty much every tick, so there's no need to send a million comparator updates.
			level.blockEntityChanged(pos);
		}
		
		int newPower = Mth.clamp(Mth.floor(Inc.rangeRemap(self.serverSpeed, 0, 90, 0, 15)), 0, 15);
		if(self.power != newPower) {
			self.power = newPower;
			level.updateNeighborsAt(pos, state.getBlock());
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
		if(level == null) return;
		
		if(level.isClientSide()) {
			speed = Mth.clamp(speed + 15, 0, 200);
			bump = 1;
			nextLightningTick = level.getGameTime();
		} else {
			serverSpeed = Mth.clamp(serverSpeed + 15, 0, 200);
			setChanged();
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
		//Also set the client speed, since this gets called thru ClientboundBlockEntityDataPacket on the client.
		serverSpeed = speed = tag.getFloat("speed");
	}
	
	public int getPower() {
		return power;
	}
}
