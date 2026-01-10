package com.oyosite.ticon.specutils.datagen

import com.oyosite.ticon.specutils.block.BlockRegistry
import com.oyosite.ticon.specutils.block.CrystallarieumMaterial
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider
import net.minecraft.advancements.critereon.*
import net.minecraft.core.Holder
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry
import net.minecraft.world.level.storage.loot.entries.LootItem
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction
import net.minecraft.world.level.storage.loot.predicates.MatchTool
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator
import java.util.concurrent.CompletableFuture


class LootTableGen(output: FabricDataOutput, registryLookup: CompletableFuture<HolderLookup.Provider>) : FabricBlockLootTableProvider(output, registryLookup) {
    override fun generate() {
        dropSelf(BlockRegistry.MOONSTONE_GROW_LAMP)
        dropSelf(BlockRegistry.BASALT_AUXILIARY_INK_SUPPLIER)
        dropSelf(BlockRegistry.CALCITE_AUXILIARY_INK_SUPPLIER)

        addDrop(BlockRegistry.DRAGONBONE)



    }

    fun addDrop(mat: CrystallarieumMaterial) = mat.run{
        listOf(smallBud, largeBud, cluster).forEach{ addBudDrop(it, if(it == cluster) pureItem else null) }
    }

    val silkTouchPredicate = enchantedItem(registries.lookup(Registries.ENCHANTMENT).get()[Enchantments.SILK_TOUCH].get())

    fun addBudDrop(bud: Block, clusterDrop: ItemLike?){
        add(bud,
            if(clusterDrop != null)lootTable(
                pool().add(AlternativesEntry.alternatives(
                    item(bud).toolCondition(silkTouchPredicate),
                    item(clusterDrop).apply(countRange(3,5))
                ))
            )
            else lootTable(
                pool().add(item(bud).toolCondition(
                    silkTouchPredicate
                ))//toolCondition(enchantedItem(registries.lookup(Registries.ENCHANTMENT).get()[Enchantments.SILK_TOUCH].get())))
            )
        )
    }

    fun countRange(min: Int, max: Int) = SetItemCountFunction.setCount(UniformGenerator.between(min.toFloat(), max.toFloat()))

    fun LootPoolSingletonContainer.Builder<*>.toolCondition(itemPredicate: ItemPredicate.Builder) = `when`(MatchTool.toolMatches(itemPredicate))

    fun pool() = LootPool.lootPool()
    fun item(item: ItemLike): LootPoolSingletonContainer.Builder<*> = LootItem.lootTableItem(item)
    fun atLeast(i: Int) = MinMaxBounds.Ints.atLeast(i)
    fun between(a: Int, b: Int) = MinMaxBounds.Ints.between(a,b)
    fun exactly(i: Int) = MinMaxBounds.Ints.exactly(i)
    fun enchantment(enchantment: Holder<Enchantment>, minLevel: Int = 1, maxLevel: Int? = null) =
        EnchantmentPredicate(enchantment, maxLevel?.let{between(minLevel, it)}?:atLeast(minLevel))
    fun enchantedItem(enchantment: Holder<Enchantment>, minLevel: Int = 1, maxLevel: Int? = null) =
        enchantedItem(enchantment(enchantment, minLevel, maxLevel))
    fun enchantedItem(vararg predicates: EnchantmentPredicate) =
        ItemPredicate.Builder.item().withSubPredicate(ItemSubPredicates.ENCHANTMENTS,
            ItemEnchantmentsPredicate.enchantments(predicates.toMutableList()))

        // LootItemCondition.Builder.enchantment(enchantment(enchantment, minLevel, maxLevel))

    //fun enchantment(enchantment: Enchantment, minLevel: Int = 1, maxLevel: Int? = null) = (enchantment, if(maxLevel==null)atLeast(minLevel) else if(maxLevel<=minLevel)exactly(minLevel) else between(minLevel, maxLevel))



    fun lootTable(pool: LootPool.Builder) = LootTable.lootTable().pool(pool.build())




}