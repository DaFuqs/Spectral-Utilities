package com.oyosite.ticon.specutils.item

import com.oyosite.ticon.specutils.*;
import de.dafuqs.spectrum.api.energy.color.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.*;
import net.minecraft.world.item.*;

@SuppressWarnings("Unused")
public class ItemRegistry {
    
    public static final Item[] ENDER_FLASKS;
    public static final Item BINDING_TOOL = register("binding_tool", new BindingTool(new Item.Properties().stacksTo(1)));
    
    static {
        ENDER_FLASKS = new Item[DyeColor.values().length];
        for(DyeColor color : DyeColor.values()) {
            ENDER_FLASKS[color.ordinal()] = register(color.getName() + "_ender_flask", new EnderFlask(new Item.Properties().stacksTo(1), InkColor.ofDyeColor(color)));
        }
        
        //ItemGroupEvents.modifyEntriesEvent(SpectrumItemGroups.ENERGY).register{}
    }
    
    public static void register() {
    
    }
    
    private static Item register(String name, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, SpectralUtilities.id(name), item);
    }

}