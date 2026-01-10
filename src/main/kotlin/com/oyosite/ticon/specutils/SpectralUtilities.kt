package com.oyosite.ticon.specutils

import com.oyosite.ticon.specutils.block.BlockRegistry
import com.oyosite.ticon.specutils.config.CommonConfig
import com.oyosite.ticon.specutils.data_components.SpectralUtilitiesDataComponents
import com.oyosite.ticon.specutils.item.EnderFlask
import com.oyosite.ticon.specutils.item.ItemRegistry
import me.shedaniel.autoconfig.AutoConfig
import me.shedaniel.autoconfig.annotation.Config
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.packs.resources.CloseableResourceManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class SpectralUtilities : ModInitializer {
    override fun onInitialize() {
        SpectralUtilitiesDataComponents.register()
        ItemRegistry.register()
        BlockRegistry.register()

        AutoConfig.register<CommonConfig?>(
            CommonConfig::class.java
        ) { definition: Config?, configClass: Class<CommonConfig?>? ->
            GsonConfigSerializer(
                definition,
                configClass
            )
        }

        ServerLifecycleEvents.SERVER_STARTED.register(ServerLifecycleEvents.ServerStarted { minecraftServer: MinecraftServer? ->
            EnderFlask.scoreboard = minecraftServer!!.getScoreboard()
            EnderFlask.level = minecraftServer.overworld()
        })
        ServerLifecycleEvents.SERVER_STOPPING.register(ServerLifecycleEvents.ServerStopping { minecraftServer: MinecraftServer? ->
            EnderFlask.scoreboard = null
            EnderFlask.level = null
        })
        ClientLifecycleEvents.CLIENT_STARTED.register(ClientLifecycleEvents.ClientStarted { minecraft: Minecraft? ->
            EnderFlask.level = minecraft!!.level
        })
        ClientLifecycleEvents.CLIENT_STOPPING.register(ClientLifecycleEvents.ClientStopping { minecraft: Minecraft? ->
            EnderFlask.level = null
        })
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register(ServerLifecycleEvents.EndDataPackReload { minecraftServer: MinecraftServer?, closeableResourceManager: CloseableResourceManager?, b: Boolean ->
            EnderFlask.scoreboard = minecraftServer!!.getScoreboard()
        })
    }

    companion object {
        const val MOD_ID: String = "specutils"
        val LOGGER: Logger = LoggerFactory.getLogger("spectral-utilities")

        @JvmStatic
		fun id(name: String): ResourceLocation {
            return ResourceLocation.fromNamespaceAndPath(MOD_ID, name)
        }
    }
}