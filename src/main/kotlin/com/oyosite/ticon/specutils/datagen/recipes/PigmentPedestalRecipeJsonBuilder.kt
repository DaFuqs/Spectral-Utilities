package com.oyosite.ticon.specutils.datagen.recipes

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.mojang.serialization.JsonOps
import de.dafuqs.spectrum.registries.SpectrumRecipeTypes
import net.minecraft.advancements.Advancement
import net.minecraft.advancements.Criterion
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.level.ItemLike
import java.util.function.Consumer

open class PigmentPedestalRecipeJsonBuilder(result: ItemLike, count: Int = 1) {

    val json = JsonObject()

    var id = BuiltInRegistries.ITEM.getKey(result.asItem())
    var advancementBuilder: Advancement.Builder = Advancement.Builder.recipeAdvancement()
    var advancementId: ResourceLocation? = null

    init {
        val resultJson = JsonObject()
        resultJson.addProperty("item", BuiltInRegistries.ITEM.getKey(result.asItem()).toString())
        resultJson.addProperty("count", count)
        json.add("result", resultJson)
    }

    fun time(time: Int) = apply { json.addProperty("time", time) }
    fun tier(tier: String) = apply { json.addProperty("tier", tier) }
    fun powders(cyan: Int = 0, magenta: Int = 0, yellow: Int = 0, black: Int = 0, white: Int = 0) = apply {
        json.addProperty("cyan", cyan)
        json.addProperty("magenta", magenta)
        json.addProperty("yellow", yellow)
        json.addProperty("black", black)
        json.addProperty("white", white)
    }

    fun experience(xp: Double) = apply { json.addProperty("experience", xp) }
    fun pattern(row1: String, row2: String? = null, row3: String? = null) = apply { json.add("pattern", JsonArray().apply{add(row1);row2?.let(::add);row3?.let(::add)}) }
    fun ingredient(key: Char, value: Ingredient) = apply {
        val keys = json.getAsJsonObject("key")?:JsonObject().also{json.add("key", it)}
        keys.add(key.toString(), Ingredient.CODEC.encode(value, JsonOps.INSTANCE, JsonObject()).orThrow)
    }

    //values entries should not be null, but are nullable for convenience.
    fun ingredient(key: Char, vararg values: ItemLike?) = ingredient(key, Ingredient.of(*values))

    fun ingredient(key: Char, tag: TagKey<Item>) = ingredient(key, Ingredient.of(tag))

    fun id(id: ResourceLocation) = apply{this.id = id}
    fun id(transform: ResourceLocation.()->ResourceLocation) = apply {id = id.transform()}

    fun criterion(criterion: String, conditions: Criterion<*>) = apply { advancementBuilder.addCriterion(criterion, conditions) }

    /*fun offerTo(exporter: RecipeOutput) = exporter.accept(id, PigmentPedestalRecipeJsonProvider(this), advancementBuilder.build())

    class PigmentPedestalRecipeJsonProvider(val builder: PigmentPedestalRecipeJsonBuilder): RecipeJsonProvider{
        override fun serialize(json: JsonObject) {
            builder.json.entrySet().forEach { json.add(it.key, it.value) }
            json.addProperty("required_advancement", advancementId.toString())
        }


        override fun getRecipeId(): ResourceLocation = builder.id

        override fun getSerializer(): RecipeSerializer<*> = SpectrumRecipeTypes.SHAPED_PEDESTAL_RECIPE_SERIALIZER

        override fun toAdvancementJson(): JsonObject = builder.advancementBuilder.toJson()

        override fun getAdvancementId(): ResourceLocation = builder.advancementId?:builder.id.withPrefixedPath("recipes/pedestal/")

    }*/


}