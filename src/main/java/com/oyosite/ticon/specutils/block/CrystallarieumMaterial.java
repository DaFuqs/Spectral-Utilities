package com.oyosite.ticon.specutils.block;

import de.dafuqs.spectrum.blocks.crystallarieum.*;
import de.dafuqs.spectrum.registries.*;
import net.fabricmc.fabric.api.itemgroup.v1.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.*;
import net.minecraft.resources.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.material.*;

import java.util.*;

public class CrystallarieumMaterial {

	private final Block smallBud;
	private final Block largeBud;
	private final Block cluster;
	private final Item pureItem;
	private final Block pureBlock;

	public CrystallarieumMaterial(
			Block smallBud,
			Block largeBud,
			Block cluster,
			Item pureItem,
			Block pureBlock
	) {
		this.smallBud = smallBud;
		this.largeBud = largeBud;
		this.cluster = cluster;
		this.pureItem = pureItem;
		this.pureBlock = pureBlock;
	}

	public static class Builder {
		private Block smallBud;
		private Block largeBud;
		private Block cluster;
		private Item pureItem;
		private Block pureBlock;

		public Builder createBuds(Block baseBlock) {
			this.smallBud = new SpectrumClusterBlock(
					crystallarieumGrowableBlockSettings(baseBlock),
					SpectrumClusterBlock.GrowthStage.SMALL
			);
			this.largeBud = new SpectrumClusterBlock(
					crystallarieumGrowableBlockSettings(baseBlock),
					SpectrumClusterBlock.GrowthStage.LARGE
			);
			this.cluster = new SpectrumClusterBlock(
					crystallarieumGrowableBlockSettings(baseBlock),
					SpectrumClusterBlock.GrowthStage.CLUSTER
			);
			this.pureBlock = new Block(baseBlock.properties());
			this.pureItem = new Item(new Item.Properties());
			return this;
		}

		public static BlockBehaviour.Properties crystallarieumGrowableBlockSettings(Block baseBlock) {
			return BlockBehaviour.Properties
					.ofFullCopy(baseBlock)
					.strength(1.5F)
					.noOcclusion()
					.forceSolidOn()
					.requiresCorrectToolForDrops()
					.pushReaction(PushReaction.DESTROY);
		}

		public CrystallarieumMaterial build() {
			return new CrystallarieumMaterial(
					smallBud, largeBud, cluster, pureItem, pureBlock
			);
		}
	}

	public static void register(ResourceLocation id, CrystallarieumMaterial material) {
		String matName = id.getPath();

		Map<String, Block> blocks = Map.of(
				"small_" + matName + "_bud", material.smallBud,
				"large_" + matName + "_bud", material.largeBud,
				matName + "_cluster", material.cluster,
				"pure_" + matName + "_block", material.pureBlock
		);

		for (var entry : blocks.entrySet()) {
			ResourceLocation identifier = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), entry.getKey());
			Registry.register(BuiltInRegistries.BLOCK, identifier, entry.getValue());
			Registry.register(BuiltInRegistries.ITEM, identifier, new BlockItem(entry.getValue(), SpectrumItems.IS.of()));
		}

		ResourceLocation identifier = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "pure_" + matName);
		Registry.register(BuiltInRegistries.ITEM, identifier, material.pureItem);
	}

	public void addEntries(FabricItemGroupEntries entries) {
		entries.accept(this.smallBud);
		entries.accept(this.largeBud);
		entries.accept(this.cluster);
		entries.accept(this.pureItem);
		entries.accept(this.pureBlock);
	}

}

