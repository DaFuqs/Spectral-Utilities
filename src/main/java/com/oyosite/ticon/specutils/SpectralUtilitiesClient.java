package com.oyosite.ticon.specutils;

import com.oyosite.ticon.specutils.block.BlockRegistry;
import com.oyosite.ticon.specutils.block.auxilary_ink_supplier.AuxiliaryInkSupplierBlockEntityRenderer;
import net.fabricmc.api.*;
import net.minecraft.client.renderer.blockentity.*;

@Environment(EnvType.CLIENT)
public class SpectralUtilitiesClient implements ClientModInitializer {
    
    @Override
    public void onInitializeClient() {
        BlockRegistry.registerClient();
        
        BlockEntityRenderers.register(BlockRegistry.BlockEntities.AUXILIARY_INK_SUPPLIER_TYPE, AuxiliaryInkSupplierBlockEntityRenderer::new);
    }
}