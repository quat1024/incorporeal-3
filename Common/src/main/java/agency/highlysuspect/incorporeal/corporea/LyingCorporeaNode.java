package agency.highlysuspect.incorporeal.corporea;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import vazkii.botania.api.corporea.CorporeaRequest;
import vazkii.botania.api.corporea.CorporeaRequestMatcher;
import vazkii.botania.api.corporea.CorporeaSpark;
import vazkii.botania.common.impl.corporea.AbstractCorporeaNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class LyingCorporeaNode extends AbstractCorporeaNode {
	public LyingCorporeaNode(Level world, BlockPos pos, CorporeaSpark spark, List<ItemStack> spoofStacks) {
		super(world, pos, spark);
		this.spoofStacks = spoofStacks;
	}
	
	protected final List<ItemStack> spoofStacks;
	
	protected abstract int countItemsInInventory();
	protected abstract List<ItemStack> extractEverything(CorporeaRequest request);
	
	//"mocked request"
	@Override
	public List<ItemStack> countItems(CorporeaRequest request) {
		//1. Filter the list of spoof items to the ones that match this corporea request.
		CorporeaRequestMatcher matcher = request.getMatcher();
		List<ItemStack> matchingSpoofStacks = spoofStacks.stream().filter(matcher).collect(Collectors.toList());
		if(matchingSpoofStacks.isEmpty()) return Collections.emptyList();
		
		//2. Count the total number of items in the wrapped inventory.
		int howMany = countItemsInInventory();
		if(howMany == 0) return Collections.emptyList();
		
		//3. Report the results as big stacks of each matching spoof item
		//These will probably be "overstacked". I think botania handles overstacks correctly.
		List<ItemStack> result = new ArrayList<>();
		for(ItemStack spoof : matchingSpoofStacks) {
			request.trackFound(howMany);
			request.trackSatisfied(howMany);
			
			ItemStack copy = spoof.copy();
			copy.setCount(howMany);
			result.add(copy);
		}
		return result;
	}
	
	//"real request"
	@Override
	public List<ItemStack> extractItems(CorporeaRequest request) {
		List<ItemStack> result = new ArrayList<>();
		
		for(ItemStack spoof : spoofStacks) {
			if(!request.getMatcher().test(spoof)) continue;
			
			//Indiscriminantly dump the contents of the inventory into the corporea request.
			result.addAll(extractEverything(request));
		}
		
		return result;
	}
}
