package agency.highlysuspect.incorporeal.computer;

import agency.highlysuspect.incorporeal.computer.types.DataType;
import agency.highlysuspect.incorporeal.computer.types.DataTypes;
import agency.highlysuspect.incorporeal.computer.types.Datum;
import agency.highlysuspect.incorporeal.IncItems;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import vazkii.botania.common.helper.ItemNBTHelper;

import java.util.Iterator;
import java.util.Objects;

/**
 * The ticket conjurer. It's like a portable Corporea Index, but gives you Tickets instead,
 * and can be used to obtain all sorts of tickets, not just Corporea Requests.
 * @see TicketItem
 */
public class TicketConjurerItem extends Item {
	public TicketConjurerItem(Properties $$0) {
		super($$0);
	}
	
	private static final String TYPE_KEY = "type";
	public DataType<?> getType(ItemStack stack) {
		String typeId = ItemNBTHelper.getString(stack, TYPE_KEY, null);
		
		return DataTypes.REGISTRY.optionalGet(typeId == null ? null : new ResourceLocation(typeId)).orElse(DataTypes.SOLIDIFIED_REQUEST);
	}
	
	public void setType(ItemStack stack, DataType<?> type) {
		ItemNBTHelper.setString(stack, TYPE_KEY, Objects.requireNonNull(DataTypes.REGISTRY.getKey(type)).toString());
	}
	
	public void cycleType(ItemStack stack) {
		DataType<?> currentType = getType(stack);
		
		//Loop over the registry of data types in registration order
		Iterator<DataType<?>> typerator = DataTypes.REGISTRY.iterator();
		while(typerator.hasNext()) {
			DataType<?> check = typerator.next();
			if(currentType == check) {
				DataType<?> next;
				if(typerator.hasNext()) next = typerator.next();
				else next = DataTypes.REGISTRY.iterator().next(); //the first one
				
				setType(stack, next);
				break;
			}
		}
	}
	
	@Override
	public Component getName(ItemStack stack) {
		//Oops it's a mess
		return new TranslatableComponent(this.getDescriptionId(stack) + '.' + DataTypes.REGISTRY.getKey(getType(stack)).toString());
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack held = player.getItemInHand(hand);
		cycleType(held);
		player.setItemInHand(hand, held);
		
		return InteractionResultHolder.consume(held);
	}
	
	@Override
	public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> list) {
		if(!allowdedIn(tab)) return; //Nice mojmap typo LOL
		
		//add a conjurer for each DataType in the game.
		for(DataType<?> type : DataTypes.REGISTRY) {
			ItemStack stack = new ItemStack(this);
			setType(stack, type);
			list.add(stack);
		}
	}
	
	//because im lazy and just mixed in to the top of TileCorporeaIndex, returning TRUE will CANCEL further handling by it.
	public boolean handleChatMessage(ServerPlayer player, String message) {
		if(player.isSpectator()) return false; //botania also handles this, but i'm too lazy to inject under this check
		
		ItemStack mainHand = player.getMainHandItem();
		ItemStack offHand = player.getOffhandItem();
		
		//If you say "2 of this", what do you mean?
		ItemStack otherHandStack;
		//Which DataType should be asked about this chat message?
		DataType<?> type;
		
		if(mainHand.getItem() == this) {
			type = getType(mainHand);
			otherHandStack = offHand;
		} else if(offHand.getItem() == this) {
			type = getType(offHand);
			otherHandStack = mainHand;
		} else {
			//Huh, looks like the player wasn't holding a ticket conjurer in the first place
			return false;
		}
		
		//Ask the DataType to parse this request
		Datum<?> datum = type.parseToDatum(message, otherHandStack);
		
		//Create a ticket for that datum
		ItemStack ticket = IncItems.TICKET.produce(datum);
		
		//Give it to the player
		if(!player.getInventory().add(ticket)) player.drop(ticket, false);
		
		//We're done
		return true;
	}
}
