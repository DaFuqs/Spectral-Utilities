package com.oyosite.ticon.specutils.component;

import com.oyosite.ticon.specutils.ink.*;
import de.dafuqs.spectrum.api.energy.color.*;
import de.dafuqs.spectrum.api.energy.storage.*;
import de.dafuqs.spectrum.registries.*;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.resources.*;
import net.minecraft.server.*;
import net.minecraft.world.level.*;
import net.minecraft.world.scores.*;
import org.ladysnake.cca.api.v3.component.sync.*;

import java.util.*;

public class StaticEnderInkStorageComponent implements AutoSyncedComponent {

	public static final long CAPACITY = 409600L;
	private static final Map<UUID, Map<InkColor, SingleInkStorage>> PLAYER_ENDER_INK_STORAGE = new HashMap<>();

	public StaticEnderInkStorageComponent(Scoreboard scoreboard, MinecraftServer server) {

	}

	public SingleInkStorage get(Level level, UUID player, InkColor color) {
		return PLAYER_ENDER_INK_STORAGE.computeIfAbsent(player, uuid -> createStorages(level.registryAccess())).get(color);
	}

	public void set(Level level, UUID player, InkColor color, ColorLockedInkStorage storage) {
		PLAYER_ENDER_INK_STORAGE.computeIfAbsent(player, uuid -> createStorages(level.registryAccess())).put(color, storage);
	}

	protected Map<InkColor, SingleInkStorage> createStorages(HolderLookup.Provider provider) {
		HolderLookup.RegistryLookup<InkColor> inkRegistry = provider.lookupOrThrow(SpectrumRegistryKeys.INK_COLOR);
		Map<InkColor, SingleInkStorage> storages = new LinkedHashMap<>();
		inkRegistry.listElements().forEach(inkColor -> storages.put(inkColor.value(), new ColorLockedInkStorage(CAPACITY, inkColor.value(), 0)));
		return storages;
	}

	@Override
	public void readFromNbt(CompoundTag compoundTag, HolderLookup.Provider provider) {
		compoundTag.getAllKeys().forEach(key -> {
			UUID player = UUID.fromString(key);
			CompoundTag values = compoundTag.getCompound(key);
			Map<InkColor, SingleInkStorage> storages = createStorages(provider);

			values.getAllKeys().forEach(s -> {
				long value = values.getLong(s);
				InkColor inkColor = SpectrumRegistries.INK_COLOR.get(ResourceLocation.tryParse(s));
				storages.get(inkColor).setEnergy(Map.of(inkColor, value), value);
			});

			PLAYER_ENDER_INK_STORAGE.put(player, storages);
		});
	}

	@Override
	public void writeToNbt(CompoundTag tag, HolderLookup.Provider provider) {
		PLAYER_ENDER_INK_STORAGE.forEach((uuid, singleInkStorages) -> {
			CompoundTag colors = new CompoundTag();
			for (SingleInkStorage singleInkStorage : singleInkStorages.values()) {
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