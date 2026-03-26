package com.oyosite.ticon.specutils.component

import com.oyosite.ticon.specutils.SpectralUtilities.Companion.id
import net.minecraft.server.MinecraftServer
import net.minecraft.world.scores.Scoreboard
import org.ladysnake.cca.api.v3.component.ComponentKey
import org.ladysnake.cca.api.v3.component.ComponentRegistry
import org.ladysnake.cca.api.v3.scoreboard.ScoreboardComponentFactoryRegistry
import org.ladysnake.cca.api.v3.scoreboard.ScoreboardComponentFactoryV2
import org.ladysnake.cca.api.v3.scoreboard.ScoreboardComponentInitializer

class ScoreboardComponentEntrypoint : ScoreboardComponentInitializer {
    override fun registerScoreboardComponentFactories(registry: ScoreboardComponentFactoryRegistry) {
        registry.registerScoreboardComponent<StaticEnderInkStorageComponent?>(
            ENDER_FLASK,
            ScoreboardComponentFactoryV2 { scoreboard: Scoreboard?, server: MinecraftServer? ->
                StaticEnderInkStorageComponent(
                    scoreboard,
                    server
                )
            })
    }

    companion object {
        val ENDER_FLASK: ComponentKey<StaticEnderInkStorageComponent?> =
            ComponentRegistry.getOrCreate<StaticEnderInkStorageComponent?>(
                id("ender_flask"), StaticEnderInkStorageComponent::class.java
            )
    }
}