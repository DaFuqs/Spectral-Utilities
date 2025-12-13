package com.oyosite.ticon.specutils.block;

import net.minecraft.core.*;

public interface LinkableBlockEntity {
	BlockPos getTargetPos();

	boolean setTargetPos(BlockPos target);
}