package com.oyosite.ticon.specutils.mixin;

import de.dafuqs.spectrum.blocks.crystallarieum.*;
import net.minecraft.core.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import org.jetbrains.annotations.*;
import org.spongepowered.asm.mixin.*;

@Mixin(SpectrumClusterBlock.class)
public abstract class SpectrumClusterBlockMixin extends Block {

	@Shadow
	@Final
	protected SpectrumClusterBlock.GrowthStage growthStage;

	public SpectrumClusterBlockMixin(Properties properties) {
		super(properties);
	}

	@Override
	protected boolean hasAnalogOutputSignal(@NotNull BlockState state) {
		return true;
	}

	@Override
	protected int getAnalogOutputSignal(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos) {
		switch (growthStage) {
			case SMALL -> {
				return 1;
			}
			case LARGE -> {
				return 8;
			}
			case CLUSTER -> {
				return 15;
			}
		}
		return 0;
	}

}
