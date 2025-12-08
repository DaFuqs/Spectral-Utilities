package com.oyosite.ticon.specutils.mixin;

import de.dafuqs.spectrum.blocks.crystallarieum.*;
import net.minecraft.core.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import org.spongepowered.asm.mixin.*;

@Mixin(SpectrumClusterBlock.class)
public abstract class CrystallarieumGrowableBlockMixin extends Block {
    
    @Shadow @Final
	protected SpectrumClusterBlock.GrowthStage growthStage;
    
    public CrystallarieumGrowableBlockMixin(Properties properties) {
        super(properties);
    }
    
    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }
    
    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        switch (growthStage){
            case SMALL -> {return 1;}
            case LARGE -> {return 8;}
            case CLUSTER -> {return 15;}
        }
        return 0;
    }
    
}
