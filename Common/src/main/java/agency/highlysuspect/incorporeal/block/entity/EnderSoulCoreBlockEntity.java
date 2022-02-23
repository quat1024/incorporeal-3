package agency.highlysuspect.incorporeal.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class EnderSoulCoreBlockEntity extends AbstractSoulCoreBlockEntity implements Container {
	public EnderSoulCoreBlockEntity(BlockPos pos, BlockState state) {
		super(IncBlockEntityTypes.ENDER_SOUL_CORE, pos, state);
	}
	
	//updated once per tick
	private @Nullable PlayerEnderChestContainer enderChest = null;
	
	@Override
	protected int getMaxMana() {
		return 5000;
	}
	
	@Override
	protected void tick() {
		enderChest = findPlayer().map(ServerPlayer::getEnderChestInventory).orElse(null);
	}
	
	@Override
	public int getContainerSize() {
		return 27;
	}
	
	@SuppressWarnings("SimplifiableConditionalExpression")
	@Override
	public boolean isEmpty() {
		return enderChest != null ? enderChest.isEmpty() : true;
	}
	
	@Override
	public ItemStack getItem(int i) {
		return enderChest != null ? enderChest.getItem(i) : ItemStack.EMPTY; 
	}
	
	@Override
	public ItemStack removeItem(int i, int i1) {
		return enderChest != null ? enderChest.removeItem(i, i1) : ItemStack.EMPTY;
	}
	
	@Override
	public ItemStack removeItemNoUpdate(int i) {
		return enderChest != null ? enderChest.removeItemNoUpdate(i) : ItemStack.EMPTY;
	}
	
	@Override
	public void setItem(int i, ItemStack itemStack) {
		if(enderChest != null) enderChest.setItem(i, itemStack);
	}
	
	@Override
	public boolean stillValid(Player player) {
		return true;
	}
	
	@Override
	public void clearContent() {
		//No. Lol
	}
	
	@Override
	public int getMaxStackSize() {
		return enderChest != null ? enderChest.getMaxStackSize() : 1;
	}
}
