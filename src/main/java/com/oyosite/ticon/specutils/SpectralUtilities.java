package com.oyosite.ticon.specutils

import com.oyosite.ticon.specutils.block.BlockRegistry;
import com.oyosite.ticon.specutils.config.CommonConfig;
import com.oyosite.ticon.specutils.item.EnderFlask;
import com.oyosite.ticon.specutils.item.ItemRegistry;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.resources.*;
import net.minecraft.server.*;
import net.minecraft.server.packs.resources.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpectralUtilities implements ModInitializer {

    public static final String  MOD_ID = "specutils";
    
    public static ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
    }
    
    public static final Logger LOGGER = LoggerFactory.getLogger("spectral-utilities");
    
    @Override
    public void onInitialize() {
        ItemRegistry.register();
        BlockRegistry.register();

        AutoConfig.register(CommonConfig.class, GsonConfigSerializer::new);

        //The second event may be redundant, but I want to be safe.
        ServerLifecycleEvents.SERVER_STARTING.register(new ServerLifecycleEvents.ServerStarting() {
            @Override
            public void onServerStarting(MinecraftServer minecraftServer) {
                EnderFlask.scoreboard = it.scoreboard
            }
        });
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register(new ServerLifecycleEvents.EndDataPackReload() {
            @Override
            public void endDataPackReload(MinecraftServer minecraftServer, CloseableResourceManager closeableResourceManager, boolean b) {
                server, _, _ -> EnderFlask.scoreboard = server.scoreboard
            }
        });

        // TODO: Add items to item groups
        // ItemSubGroupEvents.modifyEntriesEvent(ItemGroupIDs.SUBTAB_ENERGY).register(...)
    }
}