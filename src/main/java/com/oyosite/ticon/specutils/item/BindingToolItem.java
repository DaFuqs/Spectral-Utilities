package com.oyosite.ticon.specutils.item;

import com.oyosite.ticon.specutils.block.*;
import com.oyosite.ticon.specutils.data_components.*;
import net.minecraft.core.*;
import net.minecraft.network.chat.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.entity.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class BindingToolItem extends Item {

	public BindingToolItem(Properties settings) {
		super(settings);
	}

	@Override
	public @NotNull InteractionResult useOn(UseOnContext context) {
		BlockPos pos = context.getClickedPos();
		BlockEntity be = context.getLevel().getBlockEntity(pos);
		if (be instanceof LinkableBlockEntity linkableBlockEntity) {
			ItemStack usedStack = context.getItemInHand();

			@Nullable BlockPos targetPos = getTarget(usedStack);
			if (targetPos == null) return InteractionResult.FAIL;
			if (!linkableBlockEntity.setTargetPos(targetPos)) return InteractionResult.FAIL;

			return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
		}

		setTarget(context.getItemInHand(), pos);

		return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
	}

	@Override
	public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand usedHand) {
		ItemStack stack = player.getItemInHand(usedHand);
		if (player.isCrouching()) {
			setTarget(stack, null);
			return InteractionResultHolder.success(stack);
		}
		return InteractionResultHolder.pass(stack);
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
		super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

		BlockPos pos = getTarget(stack);
		if (pos == null) {
			tooltipComponents.add(Component.translatable("item.specutils.binding_tool.tooltip.unlinked"));
		} else
			tooltipComponents.add(Component.translatable("item.specutils.binding_tool.tooltip.linked_to", pos.getX(), pos.getY(), pos.getZ()));
	}

	private @Nullable BlockPos getTarget(net.minecraft.world.item.ItemStack stack) {
		return stack.get(SpectralUtilitiesDataComponents.LINKED_POSITION);
	}

	private void setTarget(ItemStack stack, @Nullable BlockPos pos) {
		if (pos == null) {
			stack.remove(SpectralUtilitiesDataComponents.LINKED_POSITION);
		} else {
			stack.set(SpectralUtilitiesDataComponents.LINKED_POSITION, pos);
		}
	}

}