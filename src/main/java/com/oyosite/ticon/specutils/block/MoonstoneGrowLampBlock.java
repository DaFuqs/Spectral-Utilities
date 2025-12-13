package com.oyosite.ticon.specutils.block;

import com.oyosite.ticon.specutils.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.*;
import net.minecraft.tags.*;
import net.minecraft.world.item.context.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import org.jetbrains.annotations.*;

public class MoonstoneGrowLampBlock extends Block {

	public static final IntegerProperty BRIGHTNESS = IntegerProperty.create("brightness", 0, 15);
	public static final BooleanProperty OVERCHARGE = BooleanProperty.create("overcharge");

	public static final TagKey<Block> OVERCHARGE_BLOCKS = TagKey.create(Registries.BLOCK, SpectralUtilities.id("overcharge_blocks"));

	public MoonstoneGrowLampBlock(BlockBehaviour.Properties properties) {
		super(properties.lightLevel(value -> value.getValue(MoonstoneGrowLampBlock.BRIGHTNESS)));
		registerDefaultState(getStateDefinition().any().setValue(BRIGHTNESS, 0).setValue(OVERCHARGE, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(BRIGHTNESS);
		builder.add(OVERCHARGE);
	}

	@Override
	protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
		super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);

		if (level.isClientSide) {
			return;
		}

		int b = state.getValue(BRIGHTNESS);
		int r = level.getBestNeighborSignal(pos);
		BlockState finalizedState = r == b ? null : state.setValue(BRIGHTNESS, r);

		boolean o = level.getBlockState(pos.above()).is(OVERCHARGE_BLOCKS);
		if (state.getValue(OVERCHARGE) != o) {
			finalizedState = (finalizedState == null ? state : finalizedState).setValue(OVERCHARGE, o);
		}

		if (finalizedState != null) {
			level.setBlockAndUpdate(pos, finalizedState);
		}
	}

	@Override
	public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState state = super.getStateForPlacement(context);
		state.setValue(BRIGHTNESS, context.getLevel().getBestNeighborSignal(context.getClickedPos()));
		state.setValue(OVERCHARGE, context.getLevel().getBlockState(context.getClickedPos().above()).is(OVERCHARGE_BLOCKS));
		return state;
	}

	@Override
	protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
		return 15 - state.getValue(BRIGHTNESS);
	}

}