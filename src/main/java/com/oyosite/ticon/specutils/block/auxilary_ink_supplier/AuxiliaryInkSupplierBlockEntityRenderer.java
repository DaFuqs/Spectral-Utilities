package com.oyosite.ticon.specutils.block.auxilary_ink_supplier
;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;

public class AuxiliaryInkSupplierBlockEntityRenderer implements BlockEntityRenderer<AuxiliaryInkSupplierBlockEntity> {
    
    @Override
    public void render(AuxiliaryInkSupplierBlockEntity entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        Minecraft client = Minecraft.getInstance();
        val inkStorageStack = entity.(0);
        if (!inkStorageStack.isEmpty) {
            matrices.push()
            val time = (entity.world !!.time % 50000L).toFloat() + tickDelta
            val height = 1.0 + sin(time.toDouble() / 8.0) / 6.0
            matrices.translate(0.5, 0.5 + height, 0.5)
            matrices.multiply(client.blockEntityRenderDispatcher.camera.rotation)
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f))
            MinecraftClient.getInstance().itemRenderer.renderItem(
                    inkStorageStack,
                    ModelTransformationMode.GROUND,
                    light,
                    overlay,
                    matrices,
                    vertexConsumers,
                    entity.world,
                    0
            )
            matrices.pop()
        }
    }
   
}