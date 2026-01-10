package com.oyosite.ticon.specutils.block.auxilary_ink_supplier

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.world.item.ItemDisplayContext
import kotlin.math.sin

@Environment(EnvType.CLIENT)
class AuxiliaryInkSupplierBlockEntityRenderer<T : AuxiliaryInkSupplierBlockEntity?>(context: BlockEntityRendererProvider.Context?) :
    BlockEntityRenderer<T?> {
    override fun render(
        entity: T?,
        tickDelta: Float,
        matrices: PoseStack,
        vertexConsumers: MultiBufferSource,
        light: Int,
        overlay: Int
    ) {
        val client = Minecraft.getInstance()
        val inkStorageStack = entity!!.getItem(0)
        if (inkStorageStack.isEmpty()) {
            return
        }

        matrices.pushPose()
        val time = (entity.getLevel()!!.getGameTime() % 50000L) + tickDelta
        val height = 1.0 + sin(time / 8.0) / 6.0
        matrices.translate(0.5, 0.5 + height, 0.5)
        matrices.mulPose(client.getBlockEntityRenderDispatcher().camera.rotation())
        matrices.mulPose(Axis.YP.rotationDegrees(180.0f))
        Minecraft.getInstance().getItemRenderer().renderStatic(
            inkStorageStack,
            ItemDisplayContext.GROUND,
            light,
            overlay,
            matrices,
            vertexConsumers,
            entity.getLevel(),
            0
        )
        matrices.popPose()
    }
}