package com.oyosite.ticon.specutils.datagen.tags

import com.oyosite.ticon.specutils.SpectralUtilities
import com.oyosite.ticon.specutils.block.BlockRegistry
import com.oyosite.ticon.specutils.block.MoonstoneGrowLampBlock
import com.oyosite.ticon.specutils.block.auxilary_ink_supplier.AuxiliaryInkSupplierBlockEntity
import de.dafuqs.spectrum.registries.SpectrumBlockTags
import de.dafuqs.spectrum.registries.SpectrumBlocks
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.Block
import java.util.concurrent.CompletableFuture

class BlockTagGen(output: FabricDataOutput, registriesFuture: CompletableFuture<HolderLookup.Provider>) : FabricTagProvider.BlockTagProvider(output, registriesFuture), SpectralUtilitiesTagGenerator<Block> {

    val CRYSTALLARIEUM_GROWABLE_BUDS = TagKey.create(Registries.BLOCK,
        ResourceLocation.parse("spectrum:crystallarieum_growable_buds")
    )
    val CRYSTALLARIEUM_GROWABLE_CLUSTERS = TagKey.create(Registries.BLOCK, ResourceLocation.parse("spectrum:crystallarieum_growable_clusters"))

    override fun addTags(arg: HolderLookup.Provider) {
        AuxiliaryInkSupplierBlockEntity.INK_PROVIDERS{
            +SpectrumBlocks.COLOR_PICKER
        }
        AuxiliaryInkSupplierBlockEntity.INK_RECEIVERS{
            +SpectrumBlocks.CRYSTALLARIEUM
            +SpectrumBlocks.CINDERHEARTH
        }

        MoonstoneGrowLampBlock.OVERCHARGE_BLOCKS{
            +SpectrumBlocks.SHIMMERSTONE_BLOCK
        }

        SpectrumBlockTags.CRYSTAL_APOTHECARY_HARVESTABLE{
            +BlockRegistry.DRAGONBONE.cluster
        }

        CRYSTALLARIEUM_GROWABLE_BUDS{
            +BlockRegistry.DRAGONBONE.largeBud
            +BlockRegistry.DRAGONBONE.smallBud
        }

        CRYSTALLARIEUM_GROWABLE_CLUSTERS{
            +BlockRegistry.DRAGONBONE.cluster
        }


    }

    override fun getTagBuilderForExternalUse(tag: TagKey<Block>): FabricTagProvider<Block>.FabricTagBuilder = getOrCreateTagBuilder(tag)


    //override fun getTagBuilderForExternalUse(tag: TagKey<Block>): FabricTagBuilder = getOrCreateTagBuilder(tag)



}