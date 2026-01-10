package com.oyosite.ticon.specutils.component

import com.oyosite.ticon.specutils.ink.ColorLockedInkStorage
import de.dafuqs.spectrum.api.energy.color.InkColor
import de.dafuqs.spectrum.api.energy.storage.SingleInkStorage
import de.dafuqs.spectrum.registries.SpectrumRegistries
import de.dafuqs.spectrum.registries.SpectrumRegistryKeys
import net.minecraft.core.Holder
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.world.level.Level
import net.minecraft.world.scores.Scoreboard
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent
import java.util.*
import java.util.Map
import java.util.function.Consumer
import kotlin.collections.get

class StaticEnderInkStorageComponent(scoreboard: Scoreboard?, server: MinecraftServer?) : AutoSyncedComponent {
    operator fun get(level: Level, player: UUID, color: InkColor): SingleInkStorage? {
        return PLAYER_ENDER_INK_STORAGE.computeIfAbsent(player) { uuid: UUID? ->
            createStorages(
                level.registryAccess()
            )
        }[color]
    }

    operator fun set(level: Level, player: UUID, color: InkColor, storage: ColorLockedInkStorage) {
        PLAYER_ENDER_INK_STORAGE.computeIfAbsent(player) {
            createStorages(
                level.registryAccess()
            )
        }[color] = storage
    }

    protected fun createStorages(provider: HolderLookup.Provider): MutableMap<InkColor, SingleInkStorage> {
        val inkRegistry = provider.lookupOrThrow<InkColor>(SpectrumRegistryKeys.INK_COLOR)
        val storages: MutableMap<InkColor, SingleInkStorage> = LinkedHashMap()
        inkRegistry.listElements().forEach { inkColor: Holder.Reference<InkColor> ->
            storages[inkColor.value()] = ColorLockedInkStorage(
                CAPACITY, inkColor.value(), 0
            )
        }
        return storages
    }

    override fun readFromNbt(compoundTag: CompoundTag, provider: HolderLookup.Provider) {
        compoundTag.allKeys.forEach(Consumer { key: String? ->
            val player = UUID.fromString(key)
            val values = compoundTag.getCompound(key)
            val storages = createStorages(provider)

            values.allKeys.forEach(Consumer { s: String? ->
                val value = values.getLong(s)
                val inkColor = SpectrumRegistries.INK_COLOR.get(ResourceLocation.tryParse(s))
                storages[inkColor]!!.setEnergy(Map.of<InkColor, Long>(inkColor, value), value)
            })
            PLAYER_ENDER_INK_STORAGE[player] = storages
        })
    }

    override fun writeToNbt(tag: CompoundTag, provider: HolderLookup.Provider) {
        PLAYER_ENDER_INK_STORAGE.forEach { (uuid: UUID, singleInkStorages: MutableMap<InkColor, SingleInkStorage>) ->
            val colors = CompoundTag()
            for (singleInkStorage in singleInkStorages.values) {
                val storedColor = singleInkStorage.getStoredColor()
                val storedEnergy = singleInkStorage.getEnergy(storedColor)
                if (storedEnergy > 0) {
                    colors.putLong(SpectrumRegistries.INK_COLOR.getKey(storedColor).toString(), storedEnergy)
                }
            }
            tag.put(uuid.toString(), colors)
        }
    }

    companion object {
        const val CAPACITY: Long = 409600L
        private val PLAYER_ENDER_INK_STORAGE: MutableMap<UUID, MutableMap<InkColor, SingleInkStorage>> = HashMap()
    }
}