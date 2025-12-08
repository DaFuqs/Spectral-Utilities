package com.oyosite.ticon.specutils.mixin;

import com.oyosite.ticon.specutils.block.*;
import com.oyosite.ticon.specutils.config.*;
import de.dafuqs.spectrum.blocks.jade_vines.*;
import de.dafuqs.spectrum.helpers.*;
import de.dafuqs.spectrum.registries.*;
import me.shedaniel.autoconfig.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(JadeVineRootsBlock.class)
public class JadeVineRootsBlockMixin {


    @SuppressWarnings("StatementWithEmptyBody")
    @Inject(
            method = "onNaturesStaffUse(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/player/PlayerEntity;)Z",
            at = @At("HEAD")
    )
    void dropJadeJelly(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfoReturnable<Boolean> cir){
        if(!AutoConfig.getConfigHolder(CommonConfig.class).getConfig().getJadeVinesDropJadeJellyWhenRevived())return;
        int i;
        for(i = 1;world.getBlockState(pos.down(i)).getBlock() == SpectrumBlocks.JADE_VINE_ROOTS;i++);

        BlockState vine = world.getBlockState(pos.down(i));
        if(vine.getBlock() == SpectrumBlocks.JADE_VINES) {
            Vec3d c = pos.down(i).toCenterPos();
            ItemScatterer.spawn(world, c.x, c.y, c.z, new ItemStack(SpectrumItems.JADE_JELLY, world.random.nextInt(3) + 3));
        }
    }

    @Inject(at=@At("RETURN"),method = "canGrow(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)Z", locals = LocalCapture.CAPTURE_FAILEXCEPTION, cancellable = true)
    void modifyCanGrow(World world, BlockPos blockPos, CallbackInfoReturnable<Boolean> cir, BlockEntity be){
        if(cir.getReturnValue() || !(be instanceof JadeVineRootsBlockEntity jv))return;
        long dayTime = world.getTimeOfDay();
        if(TimeHelper.getDay(dayTime + 1000L) == TimeHelper.getDay(jv.getLastGrownTime() + 1000L))return;

        for(int i = 0; i < 8; i++){
            BlockPos pos = blockPos.up(1+i);
            BlockState state = world.getBlockState(pos);
            if(state.getBlock() instanceof MoonstoneGrowLampBlock) {
                cir.setReturnValue(state.get(MoonstoneGrowLampBlock.Companion.getBRIGHTNESS()) > 7 + i);
                return;
            }
            if(state.isOpaque())break;
        }
    }
}
