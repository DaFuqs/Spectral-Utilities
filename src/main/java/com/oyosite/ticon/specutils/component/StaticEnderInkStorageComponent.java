package com.oyosite.ticon.specutils.component;

import com.oyosite.ticon.specutils.ink.*;
import de.dafuqs.spectrum.api.energy.color.*;
import de.dafuqs.spectrum.api.energy.storage.*;
import de.dafuqs.spectrum.helpers.*;
import de.dafuqs.spectrum.registries.*;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.resources.*;
import net.minecraft.server.*;
import net.minecraft.world.item.*;
import net.minecraft.world.scores.*;
import org.ladysnake.cca.api.v3.component.sync.*;

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
		for (DyeColor dyeColor : SpectrumColorHelper.VANILLA_DYE_COLORS) {
			storages[dyeColor.ordinal()] = new ColorLockedInkStorage(CAPACITY, InkColor.ofDyeColor(dyeColor), 0);
		}
		return storages;
	}
	
	@Override
	public void readFromNbt(CompoundTag compoundTag, HolderLookup.Provider provider) {
		compoundTag.getAllKeys().forEach(key -> {
			UUID player = UUID.fromString(key);
			CompoundTag values = compoundTag.getCompound(key);
			SingleInkStorage[] storages = createStorages();
			
			values.getAllKeys().forEach(s -> {
				long value = values.getLong(s);
				InkColor inkColor = SpectrumRegistries.INK_COLOR.get(ResourceLocation.tryParse(s));
				
				storages[inkColor.getDyeColor().get().ordinal()].setEnergy(Map.of(inkColor, value), value);
			});
			
			PLAYER_ENDER_INK_STORAGE.put(player, storages);
		});
	}
	
	@Override
	public void writeToNbt(CompoundTag tag, HolderLookup.Provider provider) {
		PLAYER_ENDER_INK_STORAGE.forEach((uuid, singleInkStorages) -> {
			CompoundTag colors = new CompoundTag();
			for (SingleInkStorage singleInkStorage : singleInkStorages) {
				InkColor storedColor = singleInkStorage.getStoredColor();
				long storedEnergy = singleInkStorage.getEnergy(storedColor);
				if (storedEnergy > 0) {
					colors.putLong(SpectrumRegistries.INK_COLOR.getKey(storedColor).toString(), storedEnergy);
				}
			}
			tag.put(uuid.toString(), colors);
		});
	}
}