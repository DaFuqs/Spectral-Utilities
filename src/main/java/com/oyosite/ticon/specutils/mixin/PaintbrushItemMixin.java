package com.oyosite.ticon.specutils.mixin;

import com.oyosite.ticon.specutils.block.*;
import com.oyosite.ticon.specutils.data_components.*;
import de.dafuqs.spectrum.api.energy.*;
import de.dafuqs.spectrum.items.magic_items.*;
import net.minecraft.core.*;
import net.minecraft.network.chat.*;
import net.minecraft.world.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.*;
import net.minecraft.world.level.block.entity.*;
import org.jetbrains.annotations.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.*;

@Mixin(PaintbrushItem.class)
public class PaintbrushItemMixin {

	@Inject(at = @At("HEAD"), method = "useOn(Lnet/minecraft/world/item/context/UseOnContext;)Lnet/minecraft/world/InteractionResult;", cancellable = true)
	public void useOn(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
		ItemStack handStack = context.getItemInHand();
		BlockPos pos = context.getClickedPos();
		BlockEntity be = context.getLevel().getBlockEntity(pos);
		if(be instanceof InkStorageBlockEntity<?>) {
			setTarget(handStack, pos);
			cir.setReturnValue(InteractionResult.sidedSuccess(context.getLevel().isClientSide));
		} else if (be instanceof LinkableBlockEntity linkableBlockEntity) {
			@Nullable BlockPos targetPos = getTarget(handStack);
			if (targetPos != null && linkableBlockEntity.setTargetPos(targetPos)) {
				cir.setReturnValue(InteractionResult.sidedSuccess(context.getLevel().isClientSide));
			}
		}
	}

	@Inject(at = @At("HEAD"), method = "appendHoverText(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/Item$TooltipContext;Ljava/util/List;Lnet/minecraft/world/item/TooltipFlag;)V", cancellable = true)
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag type, CallbackInfo ci) {
		BlockPos pos = getTarget(stack);
		if (pos != null) {
			tooltip.add(Component.translatable("item.specutils.binding_tooltip.linked_to", pos.getX(), pos.getY(), pos.getZ()));
		}
	}


	@Unique
	private @Nullable BlockPos getTarget(net.minecraft.world.item.ItemStack stack) {
		return stack.get(SpectralUtilitiesDataComponents.LINKED_POSITION);
	}

	@Unique
	private void setTarget(ItemStack stack, @Nullable BlockPos pos) {
		if (pos == null) {
			stack.remove(SpectralUtilitiesDataComponents.LINKED_POSITION);
		} else {
			stack.set(SpectralUtilitiesDataComponents.LINKED_POSITION, pos);
		}
	}

}
