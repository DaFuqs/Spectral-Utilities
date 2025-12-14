package com.oyosite.ticon.specutils.mixin;

import com.llamalad7.mixinextras.injector.*;
import com.oyosite.ticon.specutils.block.*;
import com.oyosite.ticon.specutils.config.*;
import de.dafuqs.spectrum.blocks.jade_vines.*;
import de.dafuqs.spectrum.helpers.*;
import de.dafuqs.spectrum.registries.*;
import me.shedaniel.autoconfig.*;
import net.minecraft.core.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(JadeVineRootsBlock.class)
public abstract class JadeVineRootsBlockMixin {

	@Shadow
	public abstract BlockPos getLowestRootsPos(@NotNull Level world, @NotNull BlockPos blockPos);

	@Inject(method = "onNaturesStaffUse(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/entity/player/Player;)Z", at = @At("HEAD"))
	void dropJadeJelly(Level level, BlockPos pos, BlockState state, Player player, CallbackInfoReturnable<Boolean> cir) {
		if (!AutoConfig.getConfigHolder(CommonConfig.class).getConfig().jadeVinesDropJadeJellyWhenRevived) {
			return;
		}

		int i;
		for (i = 1; level.getBlockState(pos.below(i)).getBlock() == SpectrumBlocks.JADE_VINE_ROOTS; i++) ;

		BlockState vine = level.getBlockState(pos.below(i));
		if (vine.getBlock() == SpectrumBlocks.JADE_VINES) {
			Vec3 c = pos.below(i).getCenter();
			Containers.dropItemStack(level, c.x, c.y, c.z, new ItemStack(SpectrumItems.JADE_JELLY, level.random.nextInt(3) + 3));
		}
	}

	@ModifyReturnValue(at = @At("RETURN"), method = "canGrow(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Z")
	boolean modifyCanGrow(boolean original, @NotNull Level world, @NotNull BlockPos blockPos) {
		BlockEntity blockEntity = world.getBlockEntity(this.getLowestRootsPos(world, blockPos));

		if (original || !(blockEntity instanceof JadeVineRootsBlockEntity jv)) {
			return original;
		}

		long dayTime = world.getDayTime();
		if (TimeHelper.getDay(dayTime + 1000L) == TimeHelper.getDay(jv.getLastGrownTime() + 1000L)) {
			return false;
		}

		for (int i = 0; i < 8; i++) {
			BlockPos pos = blockPos.above(1 + i);
			BlockState state = world.getBlockState(pos);
			if (state.getBlock() instanceof MoonstoneGrowLampBlock) {
				return state.getValue(MoonstoneGrowLampBlock.BRIGHTNESS) > 7 + i;
			}
			if (state.isSolid()) {
				break;
			}
		}
		return original;
	}
}
