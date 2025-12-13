package com.oyosite.ticon.specutils.data_components;

import com.oyosite.ticon.specutils.*;
import net.minecraft.core.*;
import net.minecraft.core.component.*;
import net.minecraft.core.registries.*;

import java.util.function.*;

public class SpectralUtilitiesDataComponents {

	public static final DataComponentType<BlockPos> LINKED_POSITION = register("linked_position",
			(builder) -> builder.persistent(BlockPos.CODEC).networkSynchronized(BlockPos.STREAM_CODEC));

	private static <T> DataComponentType<T> register(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
		return Registry.register(
				BuiltInRegistries.DATA_COMPONENT_TYPE,
				SpectralUtilities.id(name),
				((DataComponentType.Builder) builder.apply(DataComponentType.builder())).build()
		);
	}

	public static void register() {
	}
}
