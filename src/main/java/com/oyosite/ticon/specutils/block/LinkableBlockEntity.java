package com.oyosite.ticon.specutils.block;

import net.minecraft.core.BlockPos;

public interface LinkableBlockEntity {
    BlockPos getTargetPos();
    boolean setTargetPos(BlockPos target);
}