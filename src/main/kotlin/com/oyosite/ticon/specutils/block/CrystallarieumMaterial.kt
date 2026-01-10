package com.oyosite.ticon.specutils.block

import de.dafuqs.spectrum.blocks.crystallarieum.SpectrumClusterBlock
import de.dafuqs.spectrum.registries.SpectrumItems
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries
import net.minecraft.client.renderer.RenderType
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.PushReaction
import java.util.Map

class CrystallarieumMaterial(
    private val smallBud: Block,
    private val largeBud: Block,
    private val cluster: Block,
    private val pureItem: Item,
    private val pureBlock: Block
) {
    class Builder {
        private var smallBud: Block? = null
        private var largeBud: Block? = null
        private var cluster: Block? = null
        private var pureItem: Item? = null
        private var pureBlock: Block? = null

        fun createBuds(baseBlock: Block): Builder {
            this.smallBud = SpectrumClusterBlock(
                crystallarieumGrowableBlockSettings(baseBlock),
                SpectrumClusterBlock.GrowthStage.SMALL
            )
            this.largeBud = SpectrumClusterBlock(
                crystallarieumGrowableBlockSettings(baseBlock),
                SpectrumClusterBlock.GrowthStage.LARGE
            )
            this.cluster = SpectrumClusterBlock(
                crystallarieumGrowableBlockSettings(baseBlock),
                SpectrumClusterBlock.GrowthStage.CLUSTER
            )
            this.pureBlock = Block(baseBlock.properties())
            this.pureItem = Item(Item.Properties())
            return this
        }

        fun build(): CrystallarieumMaterial {
            return CrystallarieumMaterial(
                smallBud!!, largeBud!!, cluster!!, pureItem!!, pureBlock!!
            )
        }

        companion object {
            fun crystallarieumGrowableBlockSettings(baseBlock: Block): BlockBehaviour.Properties {
                return BlockBehaviour.Properties
                    .ofFullCopy(baseBlock)
                    .strength(1.5f)
                    .noOcclusion()
                    .forceSolidOn()
                    .requiresCorrectToolForDrops()
                    .pushReaction(PushReaction.DESTROY)
            }
        }
    }

    fun addEntries(entries: FabricItemGroupEntries) {
        entries.accept(this.smallBud)
        entries.accept(this.largeBud)
        entries.accept(this.cluster)
        entries.accept(this.pureItem)
        entries.accept(this.pureBlock)
    }

    @Environment(EnvType.CLIENT)
    fun putBlockRenderLayerMap() {
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.cutout(), this.smallBud, this.largeBud, cluster)
    }

    companion object {
        @JvmStatic
        fun register(id: ResourceLocation, material: CrystallarieumMaterial) {
            val matName = id.getPath()

            val blocks = Map.of<String?, Block?>(
                "small_" + matName + "_bud", material.smallBud,
                "large_" + matName + "_bud", material.largeBud,
                matName + "_cluster", material.cluster,
                "pure_" + matName + "_block", material.pureBlock
            )

            for (entry in blocks.entries) {
                val identifier = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), entry.key)
                Registry.register<Block?, Block?>(BuiltInRegistries.BLOCK, identifier, entry.value)
                Registry.register<Item?, BlockItem?>(
                    BuiltInRegistries.ITEM,
                    identifier,
                    BlockItem(entry.value, SpectrumItems.IS.of())
                )
            }

            val identifier = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "pure_" + matName)
            Registry.register<Item?, Item?>(BuiltInRegistries.ITEM, identifier, material.pureItem)
        }
    }
}

