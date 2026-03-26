package com.oyosite.ticon.specutils.datagen.tags

import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider.FabricTagBuilder
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey


interface SpectralUtilitiesTagGenerator<T>{

    fun getTagBuilderForExternalUse(tag: TagKey<T>): FabricTagProvider<T>.FabricTagBuilder


}

context(g: SpectralUtilitiesTagGenerator<T>)
inline operator fun <reified T> TagKey<T>.invoke(block: FabricTagProvider<T>.FabricTagBuilder.()->Unit) = g.getTagBuilderForExternalUse(this).block()

context(b: FabricTagProvider<T>.FabricTagBuilder)
inline operator fun <reified T> T.unaryPlus(): FabricTagProvider<T>.FabricTagBuilder = b.add(this)
context(b: FabricTagProvider<T>.FabricTagBuilder)
inline operator fun <reified T> ResourceLocation.unaryPlus(): FabricTagProvider<T>.FabricTagBuilder = b.add(this)
context(b: FabricTagProvider<T>.FabricTagBuilder)
inline operator fun <reified T> Collection<T>.unaryPlus(): FabricTagProvider<T>.FabricTagBuilder = b.add(*this.toTypedArray())


inline operator fun <reified T> FabricTagProvider<T>.FabricTagBuilder.plus(obj: T): FabricTagProvider<T>.FabricTagBuilder = add(obj)
inline operator fun <reified T> FabricTagProvider<T>.FabricTagBuilder.plus(id: ResourceLocation): FabricTagProvider<T>.FabricTagBuilder = add(id)
inline operator fun <reified T> FabricTagProvider<T>.FabricTagBuilder.plus(arr: Collection<T>): FabricTagProvider<T>.FabricTagBuilder = add(*arr.toTypedArray())


