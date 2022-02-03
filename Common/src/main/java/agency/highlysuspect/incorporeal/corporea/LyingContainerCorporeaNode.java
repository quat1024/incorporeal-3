package agency.highlysuspect.incorporeal.corporea;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import vazkii.botania.api.corporea.ICorporeaRequest;
import vazkii.botania.api.corporea.ICorporeaRequestMatcher;
import vazkii.botania.api.corporea.ICorporeaSpark;
import vazkii.botania.common.impl.corporea.VanillaCorporeaNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The corporea node for the Red String Liar.
 * 
 * Takes a list of "spoof stacks" as input. The node does a very specific thing:
 * - When counting items inside the Container, always reports them as copies of the spoof stacks.
 * - When actually extracting items from the Container, extracts the real items.
 */
public class LyingContainerCorporeaNode extends VanillaCorporeaNode {
	public LyingContainerCorporeaNode(Level world, BlockPos pos, ICorporeaSpark spark, Container wrappedContainer, List<ItemStack> spoofStacks) {
		super(world, pos, wrappedContainer, spark);
		
		this.spoofStacks = spoofStacks;
	}
	
	private final List<ItemStack> spoofStacks;
	
	//"mocked request"
	@Override
	public List<ItemStack> countItems(ICorporeaRequest request) {
		//1. Filter the list of spoof items to the ones that match this corporea request.
		ICorporeaRequestMatcher matcher = request.getMatcher();
		List<ItemStack> matchingSpoofStacks = spoofStacks.stream().filter(matcher).collect(Collectors.toList());
		
		if(matchingSpoofStacks.isEmpty()) return Collections.emptyList();
		
		//2. Count the total number of items in the wrapped inventory.
		int howMany = IncInventoryHelper.countItems(inv);
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
	public List<ItemStack> extractItems(ICorporeaRequest request) {
		List<ItemStack> result = new ArrayList<>();
		
		for(ItemStack spoof : spoofStacks) {
			if(!request.getMatcher().test(spoof)) continue;
			
			//Indiscriminantly dump the contents of the inventory into the corporea request.
			//This is done using VanillaCorporeaNode's item manipulation code, which only accepts a CorporeaRequest,
			//but I want to pull the whole inventory, so i made this DifferentMatcherCorporeaRequest thing.
			result.addAll(iterateOverSlots(new DifferentMatcherCorporeaRequest(WildcardCorporeaRequestMatcher.INSTANCE, request), true));
		}
		
		return result;
	}
	
	public static record DifferentMatcherCorporeaRequest(ICorporeaRequestMatcher newMatcher, ICorporeaRequest delegate) implements ICorporeaRequest {
		@Override
		public ICorporeaRequestMatcher getMatcher() {
			return newMatcher;
		}
		
		@Override
		public int getStillNeeded() {
			return delegate.getStillNeeded();
		}
		
		@Override
		public int getFound() {
			return delegate.getFound();
		}
		
		@Override
		public int getExtracted() {
			return delegate.getExtracted();
		}
		
		@Override
		public void trackSatisfied(int count) {
			delegate.trackSatisfied(count);
		}
		
		@Override
		public void trackFound(int count) {
			delegate.trackFound(count);
		}
		
		@Override
		public void trackExtracted(int count) {
			delegate.trackExtracted(count);
		}
	}
}
