package agency.highlysuspect.incorporeal.corporea;

import com.mojang.authlib.GameProfile;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PlayerHeadBlock;
import net.minecraft.world.level.block.PlayerWallHeadBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.botania.api.corporea.CorporeaHelper;
import vazkii.botania.api.corporea.ICorporeaNode;
import vazkii.botania.api.corporea.ICorporeaRequestMatcher;
import vazkii.botania.api.corporea.ICorporeaSpark;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Used to implement the corporea-spark-on-player-head thing, which is the replacement for the corporea soul core mechanic.
 * Botania fires an event on each platform's event-bus abstraction whenever a corporea index is used; each of my own platform
 * initializers plug in to that event.
 * 
 * @see IncFabric
 * @see IncForge
 */
@SuppressWarnings("JavadocReference") //above
public class PlayerHeadHandler {
	//returns true to cancel the request
	public static boolean onIndexRequest(ServerPlayer requester, ICorporeaRequestMatcher request, int requestCount, ICorporeaSpark indexSpark) {
		Set<UUID> headUuids = new HashSet<>();
		
		//i can feel the server tps dropping already. i'm not even in game yet, im just typing!
		//the one saving grace is that getNodesOnNetwork is cached for one tick i guess
		//iterate the whole network looking for player heads
		for(ICorporeaNode node : CorporeaHelper.instance().getNodesOnNetwork(indexSpark)) {
			Level level = node.getWorld();
			BlockPos pos = node.getPos();
			BlockState state = level.getBlockState(pos);
			Block block = state.getBlock();
			
			if(block instanceof PlayerHeadBlock || block instanceof PlayerWallHeadBlock) {
				//it's a player head, make sure it has a proper owner uuid
				AbstractSkullBlock skull = (AbstractSkullBlock) block;
				SkullBlock.Type type = skull.getType();
				if(type == SkullBlock.Types.PLAYER && level.getBlockEntity(pos) instanceof SkullBlockEntity skullBlockEntity) {
					GameProfile ownerProfile = skullBlockEntity.getOwnerProfile();
					if(ownerProfile != null && ownerProfile.getId() != null) {
						//found one
						headUuids.add(ownerProfile.getId());
					}
				}
			}
		}
		
		//if there's at least one head on the network & none belong to the player, nope, cancel
		if(!headUuids.isEmpty() && !headUuids.contains(requester.getUUID())) {
			requester.sendMessage(new TranslatableComponent("incorporeal.no_soul_core").withStyle(ChatFormatting.RED), Util.NIL_UUID);
			return true;
		}
		
		return false;
	}
}
