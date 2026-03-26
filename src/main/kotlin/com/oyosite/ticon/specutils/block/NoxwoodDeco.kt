package com.oyosite.ticon.specutils.block

import com.oyosite.ticon.specutils.SpectralUtilities.Companion.id
import de.dafuqs.spectrum.blocks.decoration.FlexLanternBlock
import de.dafuqs.spectrum.registries.SpectrumBlocks
import de.dafuqs.spectrum.registries.SpectrumItems
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Tuple
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.RedstoneLampBlock
import net.minecraft.world.level.block.RotatedPillarBlock
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.level.material.PushReaction
import java.util.*
import java.util.function.ToIntFunction

object NoxwoodDeco {
    @JvmField
	val LAMPS: MutableMap<ResourceLocation?, Block?> = LinkedHashMap<ResourceLocation?, Block?>()
    val LANTERNS: MutableMap<ResourceLocation?, Block?> = LinkedHashMap<ResourceLocation?, Block?>()
    val LIGHTS: MutableMap<ResourceLocation?, Block?> = LinkedHashMap<ResourceLocation?, Block?>()
    val ALL_DECO: MutableMap<ResourceLocation?, Block?> = LinkedHashMap<ResourceLocation?, Block?>()

    init {
        for (pair in NoxwoodType.Companion.COMBINATIONS) {
            /*ResourceLocation lampId = idFor(pair, "lamp");
			Block lampBlock = noxwoodLampBlock();
			LAMPS.put(lampId, lampBlock);
			ALL_DECO.put(lampId, lampBlock);*/

            val lanternId = idFor(pair, "lantern")
            val lanternBlock = noxwoodLanternBlock()
            LANTERNS.put(lanternId, lanternBlock)
            ALL_DECO.put(lanternId, lanternBlock)

            /*ResourceLocation lightId = idFor(pair, "light");
			Block lightBlock = noxwoodLightBlock();
			LIGHTS.put(lightId, lightBlock);
			ALL_DECO.put(lightId, lightBlock);*/
        }
    }

    @JvmStatic
	fun registerAll() {
        for (entry in ALL_DECO.entries) {
            val id: ResourceLocation = entry.key!!
            val block: Block = entry.value!!
            Registry.register<Block?, Block?>(BuiltInRegistries.BLOCK, id, block)
            Registry.register<Item?, BlockItem?>(BuiltInRegistries.ITEM, id, BlockItem(block, SpectrumItems.IS.of()))
        }
    }

    fun idFor(pair: Tuple<NoxwoodType?, NoxwoodType?>, blockName: String?): ResourceLocation {
        return id(
            pair.getA()!!.name.lowercase(Locale.getDefault()) + "_" +
                    pair.getB()!!.name.lowercase(Locale.getDefault()) + "_noxwood_" + blockName
        )
    }

    fun noxwoodLampBlock(): Block {
        return RedstoneLampBlock(
            SpectrumBlocks.noxcap(MapColor.CRIMSON_NYLIUM)
                .lightLevel(SpectrumBlocks.LANTERN_LIGHT_PROVIDER)
        )
    }

    fun noxwoodLanternBlock(): Block {
        return FlexLanternBlock(
            BlockBehaviour.Properties.ofFullCopy(Blocks.LANTERN)
                .lightLevel(ToIntFunction { value: BlockState? -> 13 })
                .pushReaction(PushReaction.DESTROY)
        )
    }

    fun noxwoodLightBlock(): Block {
        return RotatedPillarBlock(
            SpectrumBlocks.noxcap(MapColor.CRIMSON_NYLIUM).lightLevel(ToIntFunction { state: BlockState? -> 15 })
        )
    }

    enum class NoxwoodType {
        CHESTNUT, SLATE, EBONY, IVORY;

        override fun toString(): String {
            return name.lowercase(Locale.getDefault())
        }

        companion object {
            val COMBINATIONS: MutableList<Tuple<NoxwoodType?, NoxwoodType?>> =
                combinations

            val combinations: MutableList<Tuple<NoxwoodType?, NoxwoodType?>>
                get() {
                    val results: MutableList<Tuple<NoxwoodType?, NoxwoodType?>> =
                        ArrayList<Tuple<NoxwoodType?, NoxwoodType?>>()
                    for (type1 in entries) {
                        for (type2 in entries) {
                            if (type1 != type2) {
                                results.add(Tuple<NoxwoodType?, NoxwoodType?>(type1, type2))
                            }
                        }
                    }
                    return results
                }
        }
    }
}