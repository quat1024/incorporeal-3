package agency.highlysuspect.incorporeal.item;

import agency.highlysuspect.incorporeal.corporea.IncInventoryHelper;
import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import agency.highlysuspect.incorporeal.mixin.TileCorporeaIndexAccessor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import vazkii.botania.api.corporea.CorporeaHelper;
import vazkii.botania.common.block.tile.corporea.TileCorporeaIndex;

import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The ticket conjurer. It's like a portable Corporea Index, but gives you Corporea Tickets instead.
 * @see CorporeaTicketItem
 */
public class TicketConjurerItem extends Item {
	public TicketConjurerItem(Properties $$0) {
		super($$0);
	}
	
	private static Map<Pattern, TileCorporeaIndex.IRegexStacker> patterns() {
		return TileCorporeaIndexAccessor.inc$getPatterns();
	}
	
	//because im lazy and just mixed in to the top of TileCorporeaIndex, returning TRUE will CANCEL further handling by it.
	public boolean handleChatMessage(ServerPlayer player, String message) {
		if(player.isSpectator()) return false; //botania also handles this, but i'm too lazy to inject under this check
		
		//If you say "2 of this", what do you mean?
		ItemStack thisOrderStack;
		if(player.getMainHandItem().getItem() == this) {
			thisOrderStack = player.getOffhandItem();
		} else if(player.getOffhandItem().getItem() == this) {
			thisOrderStack = player.getMainHandItem();
		} else {
			//Huh, looks like the player wasn't holding a ticket conjurer in the first place
			return false;
		}
		
		//The rest is pretty much pasted from botania code
		String msg = message.toLowerCase(Locale.ROOT).trim();
		
		String name = "";
		int count = 0;
		for(Pattern pattern : patterns().keySet()) {
			Matcher matcher = pattern.matcher(msg);
			if(matcher.matches()) {
				TileCorporeaIndex.IRegexStacker stacker = patterns().get(pattern);
				count = stacker.getCount(matcher);
				name = stacker.getName(matcher).toLowerCase(Locale.ROOT).trim();
				//no "break", none in botania either tbh
			}
		}
		
		if(name.equals("this")) {
			if(thisOrderStack.isEmpty()) return false; //You said "2 of this", but weren't holding anything.
			else name = thisOrderStack.getHoverName().getString().toLowerCase(Locale.ROOT).trim();
		}
		
		ItemStack ticket = IncItems.CORPOREA_TICKET.produce(SolidifiedRequest.create(CorporeaHelper.instance().createMatcher(name), count));
		IncInventoryHelper.giveToPlayer(ticket, player);
		return true;
	}
}
