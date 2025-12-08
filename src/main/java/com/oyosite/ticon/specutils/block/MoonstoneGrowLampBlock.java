package com.oyosite.ticon.specutils.block;

import com.oyosite.ticon.specutils.SpectralUtilities;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;

public class MoonstoneGrowLampBlock extends Block {

    public MoonstoneGrowLampBlock(BlockBehaviour.Properties properties) {
        super(properties.lightLevel(Companion::luminosity));
        defaultState = defaultState.with(BRIGHTNESS, 0).with(OVERCHARGE, false)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) { builder.add(BRIGHTNESS, OVERCHARGE) }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? = defaultState.with(BRIGHTNESS, ctx.world.getReceivedRedstonePower(ctx.blockPos))
        .with(OVERCHARGE, ctx.world.getBlockState(ctx.blockPos.up()).isIn(OVERCHARGE_BLOCKS))

    override fun neighborUpdate(
        state: BlockState,
        world: World,
        pos: BlockPos,
        sourceBlock: Block?,
        sourcePos: BlockPos?,
        notify: Boolean
    ) {
        if(world.isClient)return
        val b = state[BRIGHTNESS]
        val r = world.getReceivedRedstonePower(pos)
        var finalizedState: BlockState? = null
        if(r!=b) finalizedState = state.with(BRIGHTNESS, r)

        val o = world.getBlockState(pos.up()).isIn(OVERCHARGE_BLOCKS)
        if(state[OVERCHARGE]!=o) finalizedState = (finalizedState?:state).with(OVERCHARGE, o)

        finalizedState?.let { world.setBlockState(pos, it, NOTIFY_LISTENERS) }
    }

    override fun getOpacity(state: BlockState, world: BlockView?, pos: BlockPos?): Int = 15 - state[BRIGHTNESS]

    companion object{
        val BRIGHTNESS: IntProperty = IntProperty.of("brightness", 0, 15)
        val OVERCHARGE: BooleanProperty = BooleanProperty.of("overcharge")

        val OVERCHARGE_BLOCKS = TagKey.of(RegistryKeys.BLOCK, SpectralUtilities.id("overcharge_blocks"))

        private fun luminosity(state: BlockState) = state[BRIGHTNESS]
    }

}