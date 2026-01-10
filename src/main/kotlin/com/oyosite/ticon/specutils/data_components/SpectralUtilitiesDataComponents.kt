package com.oyosite.ticon.specutils.data_components

import com.oyosite.ticon.specutils.SpectralUtilities.Companion.id
import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import java.util.function.UnaryOperator

object SpectralUtilitiesDataComponents {
    @JvmField
    val LINKED_POSITION: DataComponentType<BlockPos?> = register(
        "linked_position"
    ) { builder: DataComponentType.Builder<BlockPos?> ->
        builder.persistent(BlockPos.CODEC).networkSynchronized(BlockPos.STREAM_CODEC)
    }

    private fun <T> register(
        name: String,
        builder: UnaryOperator<DataComponentType.Builder<T>>
    ): DataComponentType<T?> {
        return Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            id(name),
            (builder.apply(DataComponentType.builder<T>())).build()
        )
    }

    fun register() {
    }
}
