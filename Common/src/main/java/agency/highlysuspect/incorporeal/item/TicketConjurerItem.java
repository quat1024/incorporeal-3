package agency.highlysuspect.incorporeal.item;

import agency.highlysuspect.incorporeal.computer.types.DataType;
import agency.highlysuspect.incorporeal.computer.types.DataTypes;
import agency.highlysuspect.incorporeal.computer.types.Datum;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Iterator;

/**
 * The ticket conjurer. It's like a portable Corporea Index, but gives you Tickets instead,
 * and can be used to obtain all sorts of tickets, not just Corporea Requests.
 * @see TicketItem
 */
public class TicketConjurerItem<T> extends Item {
	public TicketConjurerItem(DataType<T> type, Properties props) {
		super(props);
		this.type = type;
	}
	
	public final DataType<T> type;
	
	public ItemStack cycleType(ItemStack stack) {
		//Loop over the registry of data types in registration order
		Iterator<DataType<?>> typerator = DataTypes.REGISTRY.iterator();
		while(typerator.hasNext()) {
			DataType<?> check = typerator.next();
			if(type == check) {
				DataType<?> next;
				if(typerator.hasNext()) next = typerator.next();
				else next = DataTypes.REGISTRY.iterator().next(); //the first one
				
				//closest I can get to an "ItemStack#withItem", i suppose:
				ItemStack result = new ItemStack(next.conjurerItem());
				result.setCount(stack.getCount());
				result.setTag(stack.getTag() == null ? null : stack.getTag().copy());
				return result;
			}
		}
		
		throw new IllegalStateException("Iterated off the end of the DataTypes registry without finding myself");
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack cycled = cycleType(player.getItemInHand(hand));
		player.setItemInHand(hand, cycled);
		return InteractionResultHolder.consume(cycled);
	}
	
	//because im lazy and just mixed in to the top of TileCorporeaIndex, returning TRUE will CANCEL further handling by it.
	public static boolean handleChatMessage(ServerPlayer player, String message) {
		if(player.isSpectator()) return false; //botania also handles this, but i'm too lazy to inject under this check
		
		if(player.getMainHandItem().getItem() instanceof TicketConjurerItem conj) return conj.handleChatMessage(player, message, player.getOffhandItem());
		else if(player.getOffhandItem().getItem() instanceof TicketConjurerItem conj) return conj.handleChatMessage(player, message, player.getMainHandItem());
		else return false;
	}
	
	private boolean handleChatMessage(ServerPlayer player, String message, ItemStack otherHandStack) {
		//Ask the DataType to parse this request
		Datum<?> datum = type.parseToDatum(message, otherHandStack);
		
		//Create a ticket for that datum
		ItemStack ticket = datum.produceTicket();
		
		//Give it to the player
		if(!player.getInventory().add(ticket)) player.drop(ticket, false);
		
		//We're done
		return true;
	}
}
