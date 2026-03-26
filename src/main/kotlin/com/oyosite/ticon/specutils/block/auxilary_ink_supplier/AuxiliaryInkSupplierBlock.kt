package com.oyosite.ticon.specutils.block.auxilary_ink_supplier

import com.mojang.serialization.MapCodec
import com.oyosite.ticon.specutils.block.BlockRegistry
import de.dafuqs.spectrum.api.energy.InkStorageItem
import de.dafuqs.spectrum.blocks.FluidLogging
import de.dafuqs.spectrum.blocks.InWorldInteractionBlock
import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import java.util.*
import java.util.function.ToIntFunction
import kotlin.math.max

class AuxiliaryInkSupplierBlock(settings: Properties) :
    InWorldInteractionBlock(settings.lightLevel(ToIntFunction { state: BlockState? ->
        max(
            5,
            state!!.getValue<FluidLogging.State?>(FluidLogging.ANY_INCLUDING_NONE)!!.getLuminance()
        )
    })) {
    init {
        registerDefaultState(
            getStateDefinition().any().setValue<FluidLogging.State?, FluidLogging.State?>(
                FluidLogging.ANY_INCLUDING_NONE,
                FluidLogging.State.NOT_LOGGED
            )
        )
    }

    override fun codec(): MapCodec<out BaseEntityBlock?>? {
        return null
    }

    override fun getRenderShape(state: BlockState): RenderShape {
        return RenderShape.MODEL
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return AuxiliaryInkSupplierBlockEntity(pos, state)
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        builder.add(FluidLogging.ANY_INCLUDING_NONE)
    }

    override fun getFluidState(state: BlockState): FluidState {
        return state.getValue<FluidLogging.State?>(FluidLogging.ANY_INCLUDING_NONE)!!.getFluidState()
    }

    override fun isCollisionShapeFullBlock(state: BlockState, level: BlockGetter, pos: BlockPos): Boolean {
        return false
    }

    override fun useItemOn(
        stack: ItemStack,
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hitResult: BlockHitResult
    ): ItemInteractionResult {
        if (level.isClientSide()) return ItemInteractionResult.SUCCESS

        val blockEntity: Optional<AuxiliaryInkSupplierBlockEntity> =
            level.getBlockEntity<AuxiliaryInkSupplierBlockEntity>(
                pos,
                BlockRegistry.BlockEntities.AUXILIARY_INK_SUPPLIER_TYPE
            )
        if (blockEntity.isEmpty) return ItemInteractionResult.CONSUME

        val auxiliaryInkSupplierBlockEntity = blockEntity.get()
        if ((stack.isEmpty || stack.item is InkStorageItem<*>) && this.exchangeStack(
                level,
                pos,
                player,
                hand,
                stack,
                auxiliaryInkSupplierBlockEntity,
                0
            )
        ) {
            auxiliaryInkSupplierBlockEntity.inventoryChanged()
            auxiliaryInkSupplierBlockEntity.setOwner(player)
        }

        return ItemInteractionResult.CONSUME
    }

    override fun getLightBlock(state: BlockState, level: BlockGetter, pos: BlockPos): Int {
        return 0
    }

    public override fun getShape(
        state: BlockState,
        world: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        return SHAPE
    }

    override fun <T : BlockEntity?> getTicker(
        world: Level,
        state: BlockState,
        type: BlockEntityType<T?>
    ): BlockEntityTicker<T?>? {
        return createTickerHelper<AuxiliaryInkSupplierBlockEntity?, T?>(
            type,
            BlockRegistry.BlockEntities.AUXILIARY_INK_SUPPLIER_TYPE,
            if (world.isClientSide) null else BlockEntityTicker { world: Level, blockPos: BlockPos?, blockState: BlockState?, blockEntity: AuxiliaryInkSupplierBlockEntity ->
                AuxiliaryInkSupplierBlockEntity.serverTick(
                    world,
                    blockPos,
                    blockState,
                    blockEntity
                )
            })
    }

    companion object {
        protected val BASE_SHAPE: VoxelShape = box(4.0, 0.0, 4.0, 12.0, 10.0, 12.0)
        protected val TOP_SHAPE: VoxelShape = box(3.0, 10.0, 3.0, 13.0, 14.0, 13.0)
        protected val SHAPE: VoxelShape = Shapes.or(BASE_SHAPE, TOP_SHAPE)
    }
}