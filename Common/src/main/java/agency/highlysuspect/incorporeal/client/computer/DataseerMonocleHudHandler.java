package agency.highlysuspect.incorporeal.client.computer;

import agency.highlysuspect.incorporeal.IncTags;
import agency.highlysuspect.incorporeal.computer.capabilities.DataLensProvider;
import agency.highlysuspect.incorporeal.computer.capabilities.DatumAcceptor;
import agency.highlysuspect.incorporeal.computer.capabilities.DatumProvider;
import agency.highlysuspect.incorporeal.computer.capabilities.NotCapabilities;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.item.ICosmeticAttachable;
import vazkii.botania.common.handler.EquipmentHandler;

import java.util.Collections;
import java.util.List;

public class DataseerMonocleHudHandler {
	public static void doIt(PoseStack pose, float partialTicks) {
		Minecraft mc = Minecraft.getInstance();
		Player player = mc.player;
		if(player == null || !hasDataseerMonocle(player)) return;
		Level level = player.level;
		
		HitResult ray = mc.hitResult;
		if(ray == null || ray.getType() == HitResult.Type.MISS) return;
		
		//TODO: meh (cases where an entity is inside a block, etc)
		// I should split NotCapabilities up, but i think i git reset that code
		
		//Anyway this is Kinda Janky
		BlockPos pos;
		@Nullable BlockState state;
		@Nullable BlockEntity be;
		List<Entity> entitiesInTheBlockspace;
		
		if(ray.getType() == HitResult.Type.BLOCK && ray instanceof BlockHitResult bhr) {
			pos = bhr.getBlockPos();
			state = level.getBlockState(pos);
			be = level.getBlockEntity(pos);
			//non-null so NotCapabilities assumes i already checked and doesn't try to look up entitites inside the blockspace......
			//told u its janky
			entitiesInTheBlockspace = Collections.emptyList();
		} else if(ray.getType() == HitResult.Type.ENTITY && ray instanceof EntityHitResult ehr) {
			pos = new BlockPos(ray.getLocation());
			state = null;
			be = null;
			entitiesInTheBlockspace = List.of(ehr.getEntity());
		} else return;
		
		@Nullable DatumProvider provider = NotCapabilities.findDatumProvider(level, pos, state, be, entitiesInTheBlockspace, true);
		@Nullable DatumAcceptor acceptor = NotCapabilities.findDatumAcceptor(level, pos, state, be, entitiesInTheBlockspace, true);
		@Nullable DataLensProvider lensProvider = NotCapabilities.findDataLensProvider(level, pos, state, be, true);
		
		if(provider == null && acceptor == null && lensProvider == null) return;
		
		int halfwidth = mc.getWindow().getGuiScaledWidth() / 2;
		int halfheight = mc.getWindow().getGuiScaledHeight() / 2;
		
		//DatumRenderers.drawCentered(mc, pose, new TextComponent("i know the hud is trash"), halfwidth, 10, 0xFF0000);
		
		if(provider != null) {
			pose.pushPose();
			pose.translate(halfwidth, halfheight - 50, 0);
			DatumRenderers.draw(mc, pose, partialTicks, provider.readDatum(false)); //doIt false
			pose.popPose();
		}
		
		if(acceptor != null) {
			DatumRenderers.drawCentered(mc, pose, new TextComponent("Can accept"), halfwidth, halfheight - 35, 0xFF8800);
		}
		
		if(lensProvider != null) {
			ItemStack wow = lensProvider.hahaOopsLeakyAbstraction();
			Component yea = wow.getHoverName();
			DatumRenderers.drawCentered(mc, pose, yea, halfwidth, halfheight - 20, 0x0088FF);
		}
	}
	
	//Pasta from ItemMonocle
	public static boolean hasDataseerMonocle(LivingEntity living) {
		return !EquipmentHandler.findOrEmpty(stack -> {
			if (!stack.isEmpty()) {
				Item item = stack.getItem();
				if (stack.is(IncTags.Items.DATA_VIEWERS)) { //change
					return true;
				}
				if (item instanceof ICosmeticAttachable attach) {
					ItemStack cosmetic = attach.getCosmeticItem(stack);
					return !cosmetic.isEmpty() && cosmetic.is(IncTags.Items.DATA_VIEWERS); //change
				}
			}
			return false;
		}, living).isEmpty();
	}
	
//		
//		screen = new FlexElement("screen");
//		screen.flexDirection = FlexElement.Direction.COLUMN;
//		screen.mainAxisAlignment = Alignment.CENTER;
//		screen.crossAxisAlignment = Alignment.CENTER;
//		
//		providerAcceptor = new FlexElement("provider-acceptor");
//		providerAcceptor.flexDirection = FlexElement.Direction.ROW;
//		providerAcceptor.mainAxisAlignment = Alignment.CENTER;
//		providerAcceptor.crossAxisAlignment = Alignment.END;
//		
//		lens = new FlexElement("lens");
//		lens.flexDirection = FlexElement.Direction.COLUMN;
//		providerAcceptor.mainAxisAlignment = Alignment.START;
//		providerAcceptor.crossAxisAlignment = Alignment.CENTER;
//		
//		screen.addChildren(providerAcceptor, lens);
}
