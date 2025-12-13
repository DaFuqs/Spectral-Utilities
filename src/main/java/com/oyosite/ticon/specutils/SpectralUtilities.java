package com.oyosite.ticon.specutils;

import com.oyosite.ticon.specutils.block.*;
import com.oyosite.ticon.specutils.config.*;
import com.oyosite.ticon.specutils.data_components.*;
import com.oyosite.ticon.specutils.item.*;
import me.shedaniel.autoconfig.*;
import me.shedaniel.autoconfig.serializer.*;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.*;
import net.minecraft.client.*;
import net.minecraft.resources.*;
import net.minecraft.server.*;
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

		ServerLifecycleEvents.SERVER_STARTED.register(minecraftServer -> {
			EnderFlask.scoreboard = minecraftServer.getScoreboard();
			EnderFlask.level = minecraftServer.overworld();
		});
		ServerLifecycleEvents.SERVER_STOPPING.register(minecraftServer -> {
			EnderFlask.scoreboard = null;
			EnderFlask.level = null;
		});
		ClientLifecycleEvents.CLIENT_STARTED.register(new ClientLifecycleEvents.ClientStarted() {
			@Override
			public void onClientStarted(Minecraft minecraft) {
				EnderFlask.level = minecraft.level;
			}
		});
		ClientLifecycleEvents.CLIENT_STOPPING.register(new ClientLifecycleEvents.ClientStopping() {
			@Override
			public void onClientStopping(Minecraft minecraft) {
				EnderFlask.level = null;
			}
		});
		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((minecraftServer, closeableResourceManager, b) -> EnderFlask.scoreboard = minecraftServer.getScoreboard());
	}
}