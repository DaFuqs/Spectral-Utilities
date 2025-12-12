package com.oyosite.ticon.specutils.block;

import de.dafuqs.spectrum.blocks.crystallarieum.*;
import de.dafuqs.spectrum.registries.*;
import net.fabricmc.fabric.api.object.builder.v1.block.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.*;
import net.minecraft.resources.*;
import net.minecraft.tags.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.material.*;

import java.util.*;

public class CrystallarieumMaterial {

	private final Ingredient input;
	private final Block smallBud;
	private final Block largeBud;
	private final Block cluster;
	private final ResourceLocation output;
	private final Item pureItem;
	private final boolean registerPureItem;
	private final boolean registerBlocks;
	private ResourceLocation recipeID;

	public CrystallarieumMaterial(
			Ingredient input,
			Block smallBud,
			Block largeBud,
			Block cluster,
			ResourceLocation output,
			Item pureItem,
			boolean registerPureItem,
			boolean registerBlocks
	) {
		this.input = input;
		this.smallBud = smallBud;
		this.largeBud = largeBud;
		this.cluster = cluster;
		this.output = output;
		this.pureItem = pureItem;
		this.registerPureItem = registerPureItem;
		this.registerBlocks = registerBlocks;
	}

	public static class Builder {
		private Ingredient input;
		private Block smallBud;
		private Block largeBud;
		private Block cluster;
		private ResourceLocation output;
		private Item pureItem;
		private boolean registerPureItem = true;
		private boolean registerBlocks = true;
		private ResourceLocation recipeID;

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
			return this;
		}

		public Builder input(Ingredient ingredient) {
			this.input = ingredient;
			return this;
		}

		public Builder input(Item item) {
			return input(Ingredient.of(item));
		}

		public Builder input(TagKey<Item> tag) {
			return input(Ingredient.of(tag));
		}

		public Builder output(ResourceLocation id) {
			this.output = id;
			return this;
		}

		public Builder pureItem(ItemLike item, boolean register) {
			this.pureItem = item.asItem();
			this.registerPureItem = register;
			return this;
		}

		public Builder createPureItem(Item.Properties properties) {
			this.pureItem = new Item(properties);
			return this;
		}

		public CrystallarieumMaterial build() {
			CrystallarieumMaterial mat = new CrystallarieumMaterial(
					input, smallBud, largeBud, cluster, output, pureItem,
					registerPureItem, registerBlocks
			);
			mat.recipeID = this.recipeID;
			return mat;
		}
	}

	public static CrystallarieumMaterial of(java.util.function.Consumer<Builder> builderConsumer) {
		Builder builder = new Builder();
		builderConsumer.accept(builder);
		return builder.build();
	}

	public static void register(ResourceLocation id, CrystallarieumMaterial crystallarieumMaterial) {
		CrystallarieumMaterial mat = crystallarieumMaterial;
		String matName = id.getPath();

		if (mat.registerBlocks) {
			List<AbstractMap.SimpleEntry<String, Block>> blocks = List.of(
					new AbstractMap.SimpleEntry<>("small_" + matName + "_bud", mat.smallBud),
					new AbstractMap.SimpleEntry<>("large_" + matName + "_bud", mat.largeBud),
					new AbstractMap.SimpleEntry<>(matName + "_cluster", mat.cluster)
			);

			for (var entry : blocks) {
				ResourceLocation identifier = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), entry.getKey());
				Registry.register(BuiltInRegistries.BLOCK, identifier, entry.getValue());
				Registry.register(BuiltInRegistries.ITEM, identifier, new BlockItem(entry.getValue(), SpectrumItems.IS.of()));
			}
		}

		if (mat.registerPureItem && mat.pureItem != null) {
			ResourceLocation identifier = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "pure_" + matName);
			Registry.register(BuiltInRegistries.ITEM, identifier, mat.pureItem);
		}
	}

	public static BlockBehaviour.Properties crystallarieumGrowableBlockSettings(Block baseBlock) {
		return FabricBlockSettings.create()
				.mapColor(baseBlock.defaultMapColor())
				.sounds(baseBlock.defaultBlockState().getSoundType())
				.strength(1.5f)
				.solid()
				.pistonBehavior(PushReaction.DESTROY)
				.requiresTool()
				.nonOpaque();
	}
}

