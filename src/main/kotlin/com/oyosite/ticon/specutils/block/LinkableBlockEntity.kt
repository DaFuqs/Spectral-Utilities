package com.oyosite.ticon.specutils.block

import net.minecraft.core.BlockPos

interface LinkableBlockEntity {
    val targetPos: BlockPos?

    fun setTargetPos(target: BlockPos?): Boolean
}