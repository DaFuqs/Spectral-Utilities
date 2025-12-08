package com.oyosite.ticon.specutils.block.auxilary_ink_supplier;

import com.mojang.blaze3d.vertex.*;
import com.mojang.math.*;
import net.minecraft.client.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.world.item.*;

public class AuxiliaryInkSupplierBlockEntityRenderer implements BlockEntityRenderer<AuxiliaryInkSupplierBlockEntity> {
    
    @Override
    public void render(AuxiliaryInkSupplierBlockEntity entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        Minecraft client = Minecraft.getInstance();
        ItemStack inkStorageStack = entity.getItem(0);
        if (inkStorageStack.isEmpty()) {
            return;
        }
        
        matrices.pushPose();
        float time = (entity.getLevel().getGameTime() % 50000L) + tickDelta;
        double height = 1.0 + Math.sin(time / 8.0) / 6.0;
        matrices.translate(0.5, 0.5 + height, 0.5);
        matrices.mulPose(client.getBlockEntityRenderDispatcher().camera.rotation());
        matrices.mulPose(Axis.YP.rotationDegrees(180.0f));
        Minecraft.getInstance().getItemRenderer().renderStatic(
                inkStorageStack,
                ItemDisplayContext.GROUND,
                light,
                overlay,
                matrices,
                vertexConsumers,
                entity.getLevel(),
                0
        );
        matrices.popPose();
    }
   
}