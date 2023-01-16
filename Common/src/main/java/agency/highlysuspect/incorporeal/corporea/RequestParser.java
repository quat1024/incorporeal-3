package agency.highlysuspect.incorporeal.corporea;

import agency.highlysuspect.incorporeal.mixin.TileCorporeaIndexAccessor;
import net.minecraft.world.item.ItemStack;
import vazkii.botania.api.corporea.CorporeaHelper;
import vazkii.botania.api.corporea.CorporeaRequestMatcher;
import vazkii.botania.common.block.block_entity.corporea.CorporeaIndexBlockEntity;

import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestParser {
	private static Map<Pattern, CorporeaIndexBlockEntity.IRegexStacker> patterns() {
		return TileCorporeaIndexAccessor.inc$getPatterns();
	}
	
	public static CorporeaRequestMatcher parseMatcher(String message, ItemStack thisOrderStack) {
		//Yeah, uh... You could say this is a little bit hacky.
		return parseRequest("1 " + message, thisOrderStack).matcher();
	}
	
	public static int parseCount(String message, ItemStack thisOrderStack) {
		//Same here.
		//My defense is that it uses the same machinery that the corporea index uses, so all the funky numeric aliases like "stack of" will work.
		return parseRequest(message + " stone", thisOrderStack).count();
	}
	
	public static SolidifiedRequest parseRequest(String message, ItemStack thisOrderStack) {
		//Adapted from Botania's corporea index code, unsurprisingly.
		String msg = message.toLowerCase(Locale.ROOT).trim();
		
		String name = "";
		int count = 0;
		for(Pattern pattern : patterns().keySet()) {
			Matcher matcher = pattern.matcher(msg);
			if(matcher.matches()) {
				CorporeaIndexBlockEntity.IRegexStacker stacker = patterns().get(pattern);
				count = stacker.getCount(matcher);
				name = stacker.getName(matcher).toLowerCase(Locale.ROOT).trim();
				//no "break", none in botania either tbh
			}
		}
		
		//One difference from Botania is that "2 of this" will give you a matcher for literally that item stack.
		CorporeaRequestMatcher requestMatcher;
		if(name.equals("this")) {
			//Botania does something more like this:
			//requestMatcher = CorporeaHelper.instance().createMatcher(thisOrderStack.getHoverName().getString().toLowerCase(Locale.ROOT).trim());
			
			//I'd rather give you a real CorporeaRequestItemStackMatcher if I can, though
			requestMatcher = CorporeaHelper.instance().createMatcher(thisOrderStack, false);
		} else {
			requestMatcher = CorporeaHelper.instance().createMatcher(name);
		}
		
		return SolidifiedRequest.create(requestMatcher, count);
	}
}
