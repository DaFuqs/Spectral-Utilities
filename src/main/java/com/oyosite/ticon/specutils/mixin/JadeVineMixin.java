package com.oyosite.ticon.specutils.mixin;

import com.oyosite.ticon.specutils.block.MoonstoneGrowLampBlock;
import de.dafuqs.spectrum.blocks.jade_vines.JadeVine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(JadeVine.class)
public interface JadeVineMixin {

    @Inject(at = @At("RETURN"),method = "isExposedToSunlight(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)Z", cancellable = true)
    private static void modifyIsExposedToSunlight(World world, BlockPos blockPos, CallbackInfoReturnable<Boolean> cir){
        boolean oldReturn = cir.getReturnValue();
        if(oldReturn)return;
        BlockState state = world.getBlockState(blockPos.up());
        boolean overrideReturn = (state.getBlock() instanceof MoonstoneGrowLampBlock)? state.get(MoonstoneGrowLampBlock.Companion.getOVERCHARGE()) : false;
        if(!overrideReturn)return;
        cir.setReturnValue(true);
    }
}
