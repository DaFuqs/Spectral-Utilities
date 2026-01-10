package com.oyosite.ticon.specutils.item

import com.oyosite.ticon.specutils.SpectralUtilities.Companion.id
import com.oyosite.ticon.specutils.block.BlockRegistry
import com.oyosite.ticon.specutils.block.NoxwoodDeco
import de.dafuqs.fractal.api.ItemSubGroupEvents
import de.dafuqs.spectrum.api.energy.color.InkColor
import de.dafuqs.spectrum.registries.SpectrumItemGroups
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.Item

object ItemRegistry {
    val ENDER_FLASKS: Array<Item> = DyeColor.entries.map { register(it.getName() + "_ender_flask", EnderFlask(Item.Properties().stacksTo(1), InkColor.ofDyeColor(it))) }.toTypedArray()

    init {

        /*ENDER_FLASKS = Array<Item>(DyeColor.entries.size){null}
        for (color in DyeColor.entries) {
            ENDER_FLASKS[color.ordinal] = register(
                color.getName() + "_ender_flask",
                EnderFlask(Item.Properties().stacksTo(1), InkColor.ofDyeColor(color))
            )
        }*/

        ItemSubGroupEvents.modifyEntriesEvent(SpectrumItemGroups.ENERGY.getIdentifier())
            .register(ItemSubGroupEvents.ModifyEntries { entries: FabricItemGroupEntries? ->
                entries!!.accept(BlockRegistry.BASALT_AUXILIARY_INK_SUPPLIER)
                entries.accept(BlockRegistry.CALCITE_AUXILIARY_INK_SUPPLIER)
                for (i in ENDER_FLASKS) {
                    entries.accept(i)
                }
            })
        ItemSubGroupEvents.modifyEntriesEvent(SpectrumItemGroups.DECORATION.getIdentifier())
            .register(ItemSubGroupEvents.ModifyEntries { entries: FabricItemGroupEntries? ->
                for (i in NoxwoodDeco.ALL_DECO.values) {
                    entries!!.accept(i)
                }
            })
        ItemSubGroupEvents.modifyEntriesEvent(SpectrumItemGroups.FUNCTIONAL.getIdentifier())
            .register(ItemSubGroupEvents.ModifyEntries { entries: FabricItemGroupEntries? ->
                entries!!.accept(BlockRegistry.MOONSTONE_GROW_LAMP)
            })
        ItemSubGroupEvents.modifyEntriesEvent(SpectrumItemGroups.PURE_RESOURCES.getIdentifier())
            .register(ItemSubGroupEvents.ModifyEntries { entries: FabricItemGroupEntries ->
                BlockRegistry.DRAGONBONE.addEntries(entries)
            })
    }

    fun register() {
    }

    private fun register(name: String, item: Item): Item {
        return Registry.register<Item?, Item>(BuiltInRegistries.ITEM, id(name), item)
    }
}