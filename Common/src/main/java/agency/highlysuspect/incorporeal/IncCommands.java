package agency.highlysuspect.incorporeal;

import agency.highlysuspect.incorporeal.util.ServerPlayerDuck;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;

public class IncCommands {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher, boolean dedicatedServer) {
		var rootBuilder = Commands.literal(Inc.MODID);
		
		rootBuilder = rootBuilder.then(Commands.literal("break-bound-pearls")
			//No argument form - break pearls on yourself
			.executes(IncCommands::bumpEpoch)
			//One argument form - break pearls for another player
			.then(Commands.argument("player", EntityArgument.player())
				.requires(s -> s.hasPermission(2))
				.executes(ctx -> bumpEpoch(ctx, EntityArgument.getPlayer(ctx, "player")))));
		
		dispatcher.register(rootBuilder);
	}
	
	private static int bumpEpoch(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		return bumpEpoch(ctx, ctx.getSource().getPlayerOrException());
	}
	
	private static int bumpEpoch(CommandContext<CommandSourceStack> ctx, ServerPlayer player) {
		((ServerPlayerDuck) player).inc$bumpEpoch();
		
		int epoch = ((ServerPlayerDuck) player).inc$getEpoch();
		ctx.getSource().sendSuccess(new TextComponent("Bumped bound pearl epoch from " + (epoch - 1) + " to " + epoch), false);
		return 0;
	}
}
