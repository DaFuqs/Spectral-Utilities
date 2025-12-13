package com.oyosite.ticon.specutils.item;

import com.oyosite.ticon.specutils.*;
import com.oyosite.ticon.specutils.block.*;
import de.dafuqs.fractal.api.*;
import de.dafuqs.spectrum.api.energy.color.*;
import de.dafuqs.spectrum.registries.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;

@SuppressWarnings("Unused")
public class ItemRegistry {
    
    public static final Item[] ENDER_FLASKS;
    public static final Item BINDING_TOOL = register("binding_tool", new BindingTool(new Item.Properties().stacksTo(1)));
    
    static {
        ENDER_FLASKS = new Item[DyeColor.values().length];
        for(DyeColor color : DyeColor.values()) {
            ENDER_FLASKS[color.ordinal()] = register(color.getName() + "_ender_flask", new EnderFlask(new Item.Properties().stacksTo(1), InkColor.ofDyeColor(color)));
        }
        
        ItemSubGroupEvents.modifyEntriesEvent(SpectrumItemGroups.EQUIPMENT.getIdentifier()).register(entries -> {
            entries.accept(BINDING_TOOL);
        });
        ItemSubGroupEvents.modifyEntriesEvent(SpectrumItemGroups.ENERGY.getIdentifier()).register(entries -> {
            entries.accept(BlockRegistry.BASALT_AUXILIARY_INK_SUPPLIER);
            entries.accept(BlockRegistry.CALCITE_AUXILIARY_INK_SUPPLIER);
            for(Item i : ENDER_FLASKS) {
                entries.accept(i);
            }
		});
        ItemSubGroupEvents.modifyEntriesEvent(SpectrumItemGroups.DECORATION.getIdentifier()).register(entries -> {
            for(Block i : NoxwoodDeco.ALL_DECO.values()) {
                entries.accept(i);
            }
        });
        ItemSubGroupEvents.modifyEntriesEvent(SpectrumItemGroups.FUNCTIONAL.getIdentifier()).register(entries -> {
            entries.accept(BlockRegistry.MOONSTONE_GROW_LAMP);
        });
        ItemSubGroupEvents.modifyEntriesEvent(SpectrumItemGroups.PURE_RESOURCES.getIdentifier()).register(entries -> {
            BlockRegistry.DRAGONBONE.addEntries(entries);
        });
    }

    public static void register() {
    
    }
    
    private static Item register(String name, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, SpectralUtilities.id(name), item);
    }

}