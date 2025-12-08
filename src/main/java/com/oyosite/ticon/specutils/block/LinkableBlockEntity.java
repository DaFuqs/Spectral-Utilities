package com.oyosite.ticon.specutils.block;

import net.minecraft.core.BlockPos;

public interface LinkableBlockEntity {
    net.minecraft.core.BlockPos getTargetPos();
    boolean canBind(BlockPos target);
}