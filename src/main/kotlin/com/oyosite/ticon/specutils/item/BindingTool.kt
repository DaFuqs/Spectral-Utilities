package com.oyosite.ticon.specutils.item

import com.oyosite.ticon.specutils.block.LinkableBlockEntity
import com.oyosite.ticon.specutils.data_components.SpectralUtilitiesDataComponents
import net.fabricmc.fabric.api.item.v1.FabricItem
import net.minecraft.core.BlockPos
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level

class BindingTool(settings: Properties) : Item(settings) {
    constructor(settings: Properties.()->Unit): this(Properties().apply(settings).component(
        SpectralUtilitiesDataComponents.LINKED_POSITION, null))

    var ItemStack.target: BlockPos?
        get() = components[SpectralUtilitiesDataComponents.LINKED_POSITION]//getSubNbt("target_data")?.run{BlockPos(getInt("x"),getInt("y"),getInt("z"))}
        set(value) {applyComponents(DataComponentPatch.builder().set(SpectralUtilitiesDataComponents.LINKED_POSITION, value).build())}

    override fun useOn(context: UseOnContext): InteractionResult? {
        val pos = context.clickedPos
        val be = context.level.getBlockEntity(pos)
        (be as? LinkableBlockEntity)?.let{
            if(context.itemInHand.target?.let(be::setTargetPos) == false) return InteractionResult.FAIL
            return InteractionResult.SUCCESS
        }
        context.itemInHand.target = pos
        return InteractionResult.SUCCESS
    }

    override fun use(world: Level, user: Player, hand: InteractionHand): InteractionResultHolder<ItemStack?> {
        val stack = user.getItemInHand(hand)
        if(user.isCrouching)stack.target = null
        return InteractionResultHolder.pass(stack)
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext?,
        tooltip: MutableList<Component>,
        tooltipFlag: TooltipFlag?
    ) {
        super.appendHoverText(stack, context, tooltip, tooltipFlag)
        val pos = stack.target
        if(pos == null) tooltip.add(Component.translatable("item.specutils.binding_tool.tooltip.unlinked"))
        else tooltip.add(Component.translatable("item.specutils.binding_tool.tooltip.linked_to", pos.x, pos.y, pos.z))
    }
}