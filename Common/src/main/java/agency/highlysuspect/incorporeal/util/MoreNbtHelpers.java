package agency.highlysuspect.incorporeal.util;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;

import java.util.HashSet;
import java.util.Set;

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
}
