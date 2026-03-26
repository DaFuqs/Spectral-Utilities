package com.oyosite.ticon.specutils.block

import com.oyosite.ticon.specutils.SpectralUtilities.Companion.id
import com.oyosite.ticon.specutils.block.CrystallarieumMaterial.Companion.register
import com.oyosite.ticon.specutils.block.NoxwoodDeco.registerAll
import com.oyosite.ticon.specutils.block.auxilary_ink_supplier.AuxiliaryInkSupplierBlock
import com.oyosite.ticon.specutils.block.auxilary_ink_supplier.AuxiliaryInkSupplierBlockEntity
import com.oyosite.ticon.specutils.block.auxilary_ink_supplier.AuxiliaryInkSupplierBlockEntityRenderer
import de.dafuqs.spectrum.registries.SpectrumBlocks
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers
import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState

object BlockRegistry {
    var CALCITE_AUXILIARY_INK_SUPPLIER: Block = register(
        "calcite_auxiliary_ink_supplier",
        AuxiliaryInkSupplierBlock(BlockBehaviour.Properties.of()),
        Item.Properties()
    )
    var BASALT_AUXILIARY_INK_SUPPLIER: Block = register(
        "basalt_auxiliary_ink_supplier",
        AuxiliaryInkSupplierBlock(BlockBehaviour.Properties.of()),
        Item.Properties()
    )
    var MOONSTONE_GROW_LAMP: Block = register(
        "moonstone_grow_lamp",
        MoonstoneGrowLampBlock(BlockBehaviour.Properties.of().strength(0.6f)),
        Item.Properties()
    )

    var DRAGONBONE: CrystallarieumMaterial = CrystallarieumMaterial.Builder()
        .createBuds(SpectrumBlocks.CRACKED_DRAGONBONE)
        .build()

    private fun <T : BlockEntity?> registerBlockEntity(
        name: String,
        factory: BlockEntitySupplier<T?>,
        vararg blocks: Block?
    ): BlockEntityType<T?> {
        return Registry.register<BlockEntityType<*>?, BlockEntityType<T?>>(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            id(name),
            BlockEntityType.Builder.of<T?>(factory, *blocks).build()
        )
    }

    fun register() {
        register(id("dragonbone"), DRAGONBONE)
        registerAll()
    }

    private fun register(name: String, block: Block, properties: Item.Properties): Block {
        val id = id(name)
        Registry.register<Block?, Block?>(BuiltInRegistries.BLOCK, id, block)
        Registry.register<Item?, BlockItem?>(BuiltInRegistries.ITEM, id, BlockItem(block, properties))
        return block
    }

    @Environment(EnvType.CLIENT)
    fun registerClient() {
        BlockEntityRenderers.register<AuxiliaryInkSupplierBlockEntity?>(
            BlockEntities.AUXILIARY_INK_SUPPLIER_TYPE
        ) { context: BlockEntityRendererProvider.Context? ->
            AuxiliaryInkSupplierBlockEntityRenderer(context)
        }

        for (block in NoxwoodDeco.LAMPS.values) {
            BlockRenderLayerMap.INSTANCE.putBlock(block, RenderType.cutout())
        }

        DRAGONBONE.putBlockRenderLayerMap()
    }

    object BlockEntities {
        @JvmField
        var AUXILIARY_INK_SUPPLIER_TYPE: BlockEntityType<AuxiliaryInkSupplierBlockEntity?> =
            registerBlockEntity<AuxiliaryInkSupplierBlockEntity?>(
                "auxiliary_ink_supplier",
                { pos: BlockPos, state: BlockState? ->
                    AuxiliaryInkSupplierBlockEntity(
                        pos,
                        state
                    )
                },
                CALCITE_AUXILIARY_INK_SUPPLIER,
                BASALT_AUXILIARY_INK_SUPPLIER
            )
    }
}