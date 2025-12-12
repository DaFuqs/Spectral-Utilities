package com.oyosite.ticon.specutils.block;

import com.oyosite.ticon.specutils.*;
import de.dafuqs.spectrum.blocks.decoration.*;
import de.dafuqs.spectrum.registries.*;
import net.fabricmc.fabric.api.object.builder.v1.block.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.*;
import net.minecraft.resources.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.material.*;

import java.util.*;
import java.util.function.*;

public class NoxwoodDeco {

	public enum NoxwoodType {
		CHESTNUT, SLATE, EBONY, IVORY;

		@Override
		public String toString() {
			return name().toLowerCase();
		}

		public static void forEachCombination(java.util.function.Consumer<Pair<NoxwoodType, NoxwoodType>> block) {
			for (NoxwoodType type1 : values()) {
				for (NoxwoodType type2 : values()) {
					block.accept(new Pair<>(type1, type2));
				}
			}
		}

		public static final List<Pair<NoxwoodType, NoxwoodType>> combinations = new ArrayList<>();

		static {
			forEachCombination(combinations::add);
		}

		public static final List<Pair<NoxwoodType, NoxwoodType>> nonDuplicateCombinations =
				combinations.stream().filter(p -> !p.first.equals(p.second)).toList();

		public static <T> List<T> mapCombinations(Function<Pair<NoxwoodType, NoxwoodType>, T> transform) {
			return combinations.stream().map(transform).toList();
		}
	}

	public static final Map<ResourceLocation, Block> LAMPS = new LinkedHashMap<>();
	public static final Map<ResourceLocation, Block> LANTERNS = new LinkedHashMap<>();
	public static final Map<ResourceLocation, Block> LIGHTS = new LinkedHashMap<>();
	public static final List<Map.Entry<ResourceLocation, Block>> ALL_DECO = new ArrayList<>();

	static {
		for (Pair<NoxwoodType, NoxwoodType> pair : NoxwoodType.nonDuplicateCombinations) {
			ResourceLocation lampId = idFor(pair, "lamp");
			Block lampBlock = noxwoodLampBlock();
			LAMPS.put(lampId, lampBlock);
			ALL_DECO.add(Map.entry(lampId, lampBlock));

			ResourceLocation lanternId = idFor(pair, "lantern");
			Block lanternBlock = noxwoodLanternBlock();
			LANTERNS.put(lanternId, lanternBlock);
			ALL_DECO.add(Map.entry(lanternId, lanternBlock));

			ResourceLocation lightId = idFor(pair, "light");
			Block lightBlock = noxwoodLightBlock();
			LIGHTS.put(lightId, lightBlock);
			ALL_DECO.add(Map.entry(lightId, lightBlock));
		}
	}

	public static void registerAll() {
		for (Map.Entry<ResourceLocation, Block> entry : ALL_DECO) {
			ResourceLocation id = entry.getKey();
			Block block = entry.getValue();
			Registry.register(BuiltInRegistries.BLOCK, id, block);
			Registry.register(BuiltInRegistries.ITEM, id, new BlockItem(block, SpectrumItems.IS.of()));
		}
	}

	public static ResourceLocation idFor(Pair<NoxwoodType, NoxwoodType> pair, String blockName) {
		return SpectralUtilities.id(
				pair.first.name().toLowerCase() + "_" +
						pair.second.name().toLowerCase() + "_noxwood_" + blockName
		);
	}

	public static Block noxwoodLanternBlock() {
		return new RedstoneLampBlock(
				SpectrumBlocks.noxcap(MapColor.CRIMSON_NYLIUM).lightLevel(SpectrumBlocks.LANTERN_LIGHT_PROVIDER)
		);
	}

	public static Block noxwoodLampBlock() {
		return new FlexLanternBlock(
				FabricBlockSettings.copyOf(Blocks.LANTERN)
						.luminance(13)
						.pistonBehavior(PushReaction.DESTROY)
		);
	}

public static Block noxwoodLightBlock() {
	return new RotatedPillarBlock(
			SpectrumBlocks.noxcap(MapColor.CRIMSON_NYLIUM).lightLevel(state -> 15)
	);
}

// Simple Pair helper since Java doesn't have Kotlin's Pair
public static class Pair<A, B> {
	public final A first;
	public final B second;

	public Pair(A first, B second) {
		this.first = first;
		this.second = second;
	}
}
}