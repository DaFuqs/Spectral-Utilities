package com.oyosite.ticon.specutils.mixin;

import net.minecraft.world.level.block.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import static de.dafuqs.spectrum.blocks.crystallarieum.SpectrumClusterBlock.GrowthStage.CLUSTER;

@Mixin(CrystallarieumGrowableBlock.class)
public abstract class CrystallarieumGrowableBlockMixin extends net.minecraft.world.level.block.Block {
    @Shadow @Final public CrystallarieumGrowableBlock.GrowthStage growthStage;

    public CrystallarieumGrowableBlockMixin(Settings settings) {
        super(settings);
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        switch (growthStage){
            case SMALL -> {return 1;}
            case LARGE -> {return 8;}
            case CLUSTER -> {return 15;}
        }
        return 0;
    }
    
}
