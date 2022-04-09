package agency.highlysuspect.incorporeal.util;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import vazkii.botania.common.helper.ItemNBTHelper;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MoreNbtHelpers {
	public static final int BLOCKPOS_SET_TYPE = 10; //new CompoundTag().getId();, also i swear there's a class containing these constants now, but i can't find it
	
	public static ListTag writeBlockPosSet(Set<BlockPos> set) {
		ListTag list = new ListTag();
		for(BlockPos pos : set) {
			list.add(NbtUtils.writeBlockPos(pos));
		}
		return list;
	}
	
	public static Set<BlockPos> readBlockPosSet(ListTag list) {
		Set<BlockPos> result = new HashSet<>();
		for(Tag tagg : list) {
			if(!(tagg instanceof CompoundTag taggg)) continue;
			result.add(NbtUtils.readBlockPos(taggg));
		}
		return result;
	}
	
	//Like the methods in botania ItemNbtHelper, but for UUIDs
	public static UUID getUuid(ItemStack stack, String key, UUID defaultValue) {
		return ItemNBTHelper.verifyExistance(stack, key) ? stack.getOrCreateTag().getUUID(key) : defaultValue; 
	}
	
	public static void setUuid(ItemStack stack, String key, UUID uuid) {
		stack.getOrCreateTag().putUUID(key, uuid);
	}
}
