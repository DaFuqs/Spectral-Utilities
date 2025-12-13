package com.oyosite.ticon.specutils.block;

import com.oyosite.ticon.specutils.*;
import de.dafuqs.spectrum.blocks.decoration.*;
import de.dafuqs.spectrum.registries.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.*;
import net.minecraft.resources.*;
import net.minecraft.util.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.material.*;

import java.util.*;

public class NoxwoodDeco {

	public enum NoxwoodType {
		CHESTNUT, SLATE, EBONY, IVORY;

		public static final List<Tuple<NoxwoodType, NoxwoodType>> COMBINATIONS = getCombinations();

		@Override
		public String toString() {
			return name().toLowerCase();
		}

		public static List<Tuple<NoxwoodType, NoxwoodType>> getCombinations() {
			List<Tuple<NoxwoodType, NoxwoodType>> results = new ArrayList<>();
			for (NoxwoodType type1 : values()) {
				for (NoxwoodType type2 : values()) {
					if (type1 != type2) {
						results.add(new Tuple<>(type1, type2));
					}
				}
			}
			return results;
		}
	}

	public static final Map<ResourceLocation, Block> LAMPS = new LinkedHashMap<>();
	public static final Map<ResourceLocation, Block> LANTERNS = new LinkedHashMap<>();
	public static final Map<ResourceLocation, Block> LIGHTS = new LinkedHashMap<>();
	public static final Map<ResourceLocation, Block> ALL_DECO = new LinkedHashMap<>();

	static {
		for (Tuple<NoxwoodType, NoxwoodType> pair : NoxwoodType.COMBINATIONS) {
			/*ResourceLocation lampId = idFor(pair, "lamp");
			Block lampBlock = noxwoodLampBlock();
			LAMPS.put(lampId, lampBlock);
			ALL_DECO.put(lampId, lampBlock);*/

			ResourceLocation lanternId = idFor(pair, "lantern");
			Block lanternBlock = noxwoodLanternBlock();
			LANTERNS.put(lanternId, lanternBlock);
			ALL_DECO.put(lanternId, lanternBlock);

			/*ResourceLocation lightId = idFor(pair, "light");
			Block lightBlock = noxwoodLightBlock();
			LIGHTS.put(lightId, lightBlock);
			ALL_DECO.put(lightId, lightBlock);*/
		}
	}

	public static void registerAll() {
		for (Map.Entry<ResourceLocation, Block> entry : ALL_DECO.entrySet()) {
			ResourceLocation id = entry.getKey();
			Block block = entry.getValue();
			Registry.register(BuiltInRegistries.BLOCK, id, block);
			Registry.register(BuiltInRegistries.ITEM, id, new BlockItem(block, SpectrumItems.IS.of()));
		}
	}

	public static ResourceLocation idFor(Tuple<NoxwoodType, NoxwoodType> pair, String blockName) {
		return SpectralUtilities.id(
				pair.getA().name().toLowerCase() + "_" +
						pair.getB().name().toLowerCase() + "_noxwood_" + blockName
		);
	}

	public static Block noxwoodLampBlock() {
		return new RedstoneLampBlock(
				SpectrumBlocks.noxcap(MapColor.CRIMSON_NYLIUM)
						.lightLevel(SpectrumBlocks.LANTERN_LIGHT_PROVIDER)
		);
	}

	public static Block noxwoodLanternBlock() {
		return new FlexLanternBlock(
				BlockBehaviour.Properties.ofFullCopy(Blocks.LANTERN)
						.lightLevel(value -> 13)
						.pushReaction(PushReaction.DESTROY)
		);
	}

	public static Block noxwoodLightBlock() {
		return new RotatedPillarBlock(
				SpectrumBlocks.noxcap(MapColor.CRIMSON_NYLIUM).lightLevel(state -> 15)
		);
	}

}