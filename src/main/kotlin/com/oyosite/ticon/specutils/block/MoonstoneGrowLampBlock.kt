package com.oyosite.ticon.specutils.block

import com.oyosite.ticon.specutils.SpectralUtilities.Companion.id
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.IntegerProperty

class MoonstoneGrowLampBlock(properties: Properties) : Block(properties.lightLevel { value: BlockState ->
    value.getValue(BRIGHTNESS)
}) {
    init {
        registerDefaultState(
            getStateDefinition().any().setValue(BRIGHTNESS, 0).setValue(
                OVERCHARGE, false
            )
        )
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        builder.add(BRIGHTNESS)
        builder.add(OVERCHARGE)
    }

    override fun neighborChanged(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        neighborBlock: Block,
        neighborPos: BlockPos,
        movedByPiston: Boolean
    ) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston)

        if (level.isClientSide) {
            return
        }

        val b: Int = state.getValue(BRIGHTNESS)!!
        val r = level.getBestNeighborSignal(pos)
        var finalizedState = if (r == b) null else state.setValue(BRIGHTNESS, r)

        val o: Boolean = level.getBlockState(pos.above()).`is`(OVERCHARGE_BLOCKS)
        if (state.getValue(OVERCHARGE) != o) {
            finalizedState = (finalizedState ?: state).setValue(
                OVERCHARGE, o
            )
        }

        if (finalizedState != null) {
            level.setBlockAndUpdate(pos, finalizedState)
        }
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        var state = super.getStateForPlacement(context)
        state =
            state!!.setValue(BRIGHTNESS, context.level.getBestNeighborSignal(context.getClickedPos()))
        state = state.setValue(
            OVERCHARGE, context.level.getBlockState(context.getClickedPos().above()).`is`(
                OVERCHARGE_BLOCKS
            )
        )
        return state
    }

    override fun getLightBlock(state: BlockState, level: BlockGetter, pos: BlockPos): Int {
        return 15 - state.getValue(BRIGHTNESS)
    }

    companion object {
        @JvmField
		val BRIGHTNESS: IntegerProperty = IntegerProperty.create("brightness", 0, 15)
        @JvmField
		val OVERCHARGE: BooleanProperty = BooleanProperty.create("overcharge")

        val OVERCHARGE_BLOCKS: TagKey<Block?> = TagKey.create<Block?>(Registries.BLOCK, id("overcharge_blocks"))
    }
}