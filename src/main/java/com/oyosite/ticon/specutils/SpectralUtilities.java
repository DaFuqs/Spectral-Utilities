package com.oyosite.ticon.specutils;

import com.oyosite.ticon.specutils.block.*;
import com.oyosite.ticon.specutils.config.*;
import com.oyosite.ticon.specutils.data_components.*;
import com.oyosite.ticon.specutils.item.*;
import me.shedaniel.autoconfig.*;
import me.shedaniel.autoconfig.serializer.*;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.*;
import net.minecraft.resources.*;
import org.slf4j.*;

public class SpectralUtilities implements ModInitializer {
	
	public static final String MOD_ID = "specutils";
	public static final Logger LOGGER = LoggerFactory.getLogger("spectral-utilities");
	
	public static ResourceLocation id(String name) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
	}
	
	@Override
	public void onInitialize() {
		SpectralUtilitiesDataComponents.register();
		ItemRegistry.register();
		BlockRegistry.register();
		
		AutoConfig.register(CommonConfig.class, GsonConfigSerializer::new);
		
		//The second event may be redundant, but I want to be safe.
		ServerLifecycleEvents.SERVER_STARTING.register(minecraftServer -> EnderFlask.scoreboard = minecraftServer.getScoreboard());
		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((minecraftServer, closeableResourceManager, b) -> EnderFlask.scoreboard = minecraftServer.getScoreboard());
		
		// TODO: Add items to item groups
		// ItemSubGroupEvents.modifyEntriesEvent(ItemGroupIDs.SUBTAB_ENERGY).register(...)
	}
}