package com.oyosite.ticon.specutils.component
;
import com.oyosite.ticon.specutils.ink.ColorLockedInkStorage;
import de.dafuqs.spectrum.api.energy.color.InkColor;
import de.dafuqs.spectrum.api.energy.storage.SingleInkStorage;
import de.dafuqs.spectrum.helpers.*;
import net.fabricmc.fabric.impl.client.indigo.renderer.helper.*;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.*;
import net.minecraft.world.scores.*;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import java.util.*;

public class StaticEnderInkStorageComponent implements AutoSyncedComponent {
    
    public static final long CAPACITY = 409600L;
    private static final Map<UUID, SingleInkStorage[]> PLAYER_ENDER_INK_STORAGE = new HashMap<>();
    
    public StaticEnderInkStorageComponent(Scoreboard scoreboard, MinecraftServer server) {
    
    }
    
    public SingleInkStorage get(UUID player, DyeColor color) {
        return PLAYER_ENDER_INK_STORAGE.putIfAbsent(player, createStorages())[color.ordinal()];
    }
    
    public void set(UUID player, DyeColor color, ColorLockedInkStorage storage) {
        PLAYER_ENDER_INK_STORAGE.putIfAbsent(player, createStorages())[color.ordinal()] = storage;
    }
    
    protected SingleInkStorage[] createStorages() {
        ColorLockedInkStorage[] storages = new ColorLockedInkStorage[DyeColor.values().length];
        for(DyeColor dyeColor : SpectrumColorHelper.VANILLA_DYE_COLORS) {
            storages[dyeColor.ordinal()] = new ColorLockedInkStorage(CAPACITY, InkColor.ofDyeColor(dyeColor), 0);
        }
        return storages;
    }
    
    @Override
    public void readFromNbt(CompoundTag compoundTag, HolderLookup.Provider provider) {
        tag.keys.forEach {
            val playerTag = tag.getCompound(it)
            val uuid = UUID.fromString(it)
            val storages = arrayOfNulls<SingleInkStorage?>(16).apply { PLAYER_ENDER_INK_STORAGE[uuid] = this }
            for(i in 0 until 16) {
                if(!playerTag.contains(i.toString()))continue
                storages[i] = ColorLockedInkStorage(409600L, InkColor.of(DyeColor.byId(i)), playerTag.getLong(i.toString()))
            }
        }
        
    }
    
    @Override
    public void writeToNbt(CompoundTag compoundTag, HolderLookup.Provider provider) {
        PLAYER_ENDER_INK_STORAGE.forEach { player, inkData ->
                val playerTag = NbtCompound()
            inkData.forEachIndexed { i, it ->
                    it?.getEnergy(InkColor.ofDyeColor(DyeColor.byId(i)))?.run{playerTag.putLong(i.toString(), this)}
            }
            tag.put(player.toString(), playerTag)
        }
    }
}