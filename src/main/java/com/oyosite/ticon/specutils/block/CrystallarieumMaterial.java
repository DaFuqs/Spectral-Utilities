package com.oyosite.ticon.specutils.block
		;

import com.oyosite.ticon.specutils.*;
import com.oyosite.ticon.specutils.block.CrystallarieumMaterial.*;
import de.dafuqs.spectrum.registries.*;
import net.minecraft.resources.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.*;

public record CrystallarieumMaterial(
		Ingredient input,
		Block smallBud,
		Block largeBud,
		Block cluster,
		ResourceLocation output,
		Item pureItem,
		boolean registerPureItem,
		boolean registerBlocks
) {
	
	private static ResourceLocation recipeID = null;
	
	record Builder(
		Ingredient input,
		Block smallBud,
		Block largeBud,
		Block cluster,
		ResourceLocation output,
		Item pureItem,
		boolean registerPureItem,
		boolean registerBlocks,
		ResourceLocation recipeID
	) {
		
		
		fun createBuds(baseBlock:Block) =
		
		apply {
			smallBud = CrystallarieumGrowableBlock(crystallarieumGrowableBlockSettings(baseBlock), CrystallarieumGrowableBlock.GrowthStage.SMALL)
			largeBud = CrystallarieumGrowableBlock(crystallarieumGrowableBlockSettings(baseBlock), CrystallarieumGrowableBlock.GrowthStage.LARGE)
			cluster = CrystallarieumGrowableBlock(crystallarieumGrowableBlockSettings(baseBlock), CrystallarieumGrowableBlock.GrowthStage.CLUSTER)
		}
		
		fun input(ingredient:Ingredient) =
		
		apply {
			input = ingredient
		}
		
		fun input(item:Item) =
		
		input(Ingredient.ofItems(item))
		
		fun input(tag:TagKey<Item>) =
		
		input(Ingredient.fromTag(tag))
		
		fun output(id:Identifier) =
		
		apply {
			output = id
		}
		
		fun pureItem(item:ItemConvertible, register:Boolean=false) =
		
		apply {
			pureItem = item.asItem();
			registerPureItem = register
		}
		
		fun createPureItem(settings:Item.Settings) =
		
		apply {
			pureItem = Item(settings)
		}
		
		fun createPureItem(settings:Item.Settings.()->Unit)=
		
		createPureItem(FabricItemSettings().
		
		apply(settings))
		
		
		fun build() =
		
		CrystallarieumMaterial(
				input!!,
				smallBud!!,
				largeBud!!,
				cluster!!,
				output,
				pureItem!!,
				registerPureItem,
				registerBlocks
				).
		
		apply {
			recipeID = this @Builder.recipeID
		}
	}
	
	companion object
	
	{
		operator fun invoke(builder:Builder. () -> Unit) =Builder().apply(builder).build()
		
		context(T)
		fun<T> KProperty1<T, CrystallarieumMaterial >.register() {
		val mat = get(this @T)
		val matName = name.lowercase()
		if (mat.registerBlocks) mat.run {
			listOf(
					"small_${matName}_bud"to smallBud,
					"large_${matName}_bud"to largeBud,
					"${matName}_cluster"to cluster
			).forEach {
				(id, block) ->
						val identifier = Identifier.tryParse( if (this @T is Namespaced)this @T.namespace else
				SpectralUtilities.MODID, id)
				Registry.register(Registries.BLOCK, identifier, block)
				Registry.register(Registries.ITEM, identifier, BlockItem(block, SpectrumItems.IS.of()))
			}
		}
		if (mat.registerPureItem) mat.pureItem ?.let {
			pure ->
					val identifier = Identifier.tryParse( if (this @T is Namespaced)this @T.namespace else
			SpectralUtilities.MODID, "pure_$matName")
			Registry.register(Registries.ITEM, identifier, pure)
		}
		
	}
		
		fun crystallarieumGrowableBlockSettings (baseBlock:Block):AbstractBlock.Settings {
		return FabricBlockSettings.create().mapColor(baseBlock.defaultMapColor).sounds(baseBlock.getSoundGroup(baseBlock.defaultState)).strength(1.5f)
				.solid().pistonBehavior(PistonBehavior.DESTROY).requiresTool().nonOpaque()
	}
	}
	
	
}