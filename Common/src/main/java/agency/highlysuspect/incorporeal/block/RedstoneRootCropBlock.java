package agency.highlysuspect.incorporeal.block;

import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import vazkii.botania.common.item.ModItems;

/**
 * Incorporeal edits Botania's "redstone root" to be a plantable crop. This is the crop block.
 * The interaction is hooked through platform-specific use-item APIs.
 */
public class RedstoneRootCropBlock extends CropBlock {
	public RedstoneRootCropBlock(Properties props) {
		super(props);
		
		registerDefaultState(defaultBlockState().setValue(AGE, 0));
	}
	
	//The default age prop goes to 7 so tbh i should bite the bullet and just draw another sprite lol
	public static final int MAX_AGE = 6;
	public static final IntegerProperty AGE = IntegerProperty.create("age", 0, MAX_AGE);
	public static final VoxelShape[] SHAPES = Util.make(new VoxelShape[MAX_AGE + 1], arr -> {
		for(int i = 0; i <= MAX_AGE; i++) {
			double yea = (MAX_AGE - i) / 32d;
			arr[i] = Shapes.create(yea, 0, yea, 1 - yea, 3/16d, 1 - yea);
		}
	});
	
	@Override
	public IntegerProperty getAgeProperty() {
		return AGE;
	}
	
	@Override
	public int getMaxAge() {
		return MAX_AGE;
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		return SHAPES[state.getValue(AGE)];
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		//super.createBlockStateDefinition(builder); //Adds the default age property with 7 parameters
		builder.add(AGE);
	}
	
	/// Things relating to the mixin crimes below
	
	@Override
	public Item asItem() {
		return ModItems.redstoneRoot;
	}
	
	//Weird cropblock thing
	@Override
	protected ItemLike getBaseSeedId() {
		return ModItems.redstoneRoot;
	}
	
	//Hooked after Fabric's spectator check, and after checking that the player is holding a redstone root item.
	public InteractionResult hookRedstoneRootClick(Player player, Level level, ItemStack held, InteractionHand hand, BlockHitResult hit) {
		if(hit == null || hit.getType() != HitResult.Type.BLOCK) return InteractionResult.PASS;
		
		//From here on out, I'm basically copying BlockItem, inlining most of the customization points that aren't used
		
		//preparing
		UseOnContext useContext = new UseOnContext(player, hand, hit);
		BlockPlaceContext blockPlaceContext = new BlockPlaceContext(useContext);
		BlockPos pos = blockPlaceContext.getClickedPos();
		
		if(!blockPlaceContext.canPlace()) return InteractionResult.FAIL;
		
		//updatePlacementContext -> skip
		BlockState placementState = this.getStateForPlacement(blockPlaceContext);
		if(placementState == null) return InteractionResult.FAIL;
		
		CollisionContext ctx = CollisionContext.of(player);
		if(!placementState.canSurvive(level, pos) || !level.isUnobstructed(placementState, pos, ctx)) return InteractionResult.FAIL;
		
		//setBlock - doing it for real!!!
		level.setBlock(pos, placementState, 11);
		
		//post placement
		//block entity stuff -> skip
		if(player instanceof ServerPlayer serverPlayer) {
			CriteriaTriggers.PLACED_BLOCK.trigger(serverPlayer, pos, held);
		}
		SoundType soundType = SoundType.CROP;
		level.playSound(player, pos, soundType.getPlaceSound(), SoundSource.BLOCKS, (soundType.getVolume() + 1f) / 2f, soundType.getPitch() * 0.8f);
		level.gameEvent(player, GameEvent.BLOCK_PLACE, pos);
		if(!player.getAbilities().instabuild) {
			held.shrink(1);
		}
		
		//still do not understand the point of sidedSuccess
		return InteractionResult.sidedSuccess(level.isClientSide);
	}
}
