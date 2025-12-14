package com.oyosite.ticon.specutils.mixin;

import com.oyosite.ticon.specutils.block.*;
import de.dafuqs.spectrum.blocks.jade_vines.*;
import net.minecraft.core.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.state.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(JadeVine.class)
public interface JadeVineMixin {

	@Inject(at = @At("RETURN"), method = "isExposedToSunlight(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Z", cancellable = true)
	private static void modifyIsExposedToSunlight(Level world, BlockPos blockPos, CallbackInfoReturnable<Boolean> cir) {
		if (!cir.getReturnValue()) {
			return;
		}

		BlockState state = world.getBlockState(blockPos.above());
		boolean overchargedGrowLampAbove = (state.getBlock() instanceof MoonstoneGrowLampBlock) ? state.getValue(MoonstoneGrowLampBlock.OVERCHARGE) : false;
		if (overchargedGrowLampAbove) {
			cir.setReturnValue(false);
		}
	}
}
