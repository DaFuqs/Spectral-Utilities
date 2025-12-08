package com.oyosite.ticon.specutils.item;

import com.oyosite.ticon.specutils.block.*;
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

public class BindingTool extends Item {
    
    public BindingTool(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return super.useOn(context);
        
        BlockPos pos = context.getClickedPos();
        BlockEntity be = context.getLevel().getBlockEntity(pos);
        if(be instanceof LinkableBlockEntity linkableBlockEntity) {
            ItemStack usedStack = context.getItemInHand();
            
            @Nullable BlockPos targetPos = getTarget(usedStack);
            if(targetPos == null) return InteractionResult.FAIL;
            if(!linkableBlockEntity.canBind(targetPos)) return InteractionResult.FAIL;
            
            it.targetPos = targetPos;
            return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
        }
        
        setTarget(context.getItemInHand(), pos);
        
        return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if(player.isCrouching()) {
            setTarget(stack, null);
            return InteractionResultHolder.success(stack);
        }
        return InteractionResultHolder.pass(stack);
    }
    
    @Override
    public void appendHoverText(net.minecraft.world.item.ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        
        BlockPos pos = getTarget(stack);
        if(pos == null) {
            tooltipComponents.add(Component.translatable("item.specutils.binding_tool.tooltip.unlinked"));
        }
        else tooltipComponents.add(Component.translatable("item.specutils.binding_tool.tooltip.linked_to", pos.getX(), pos.getY(), pos.getZ()));
    }
    
    private @Nullable BlockPos getTarget(net.minecraft.world.item.ItemStack stack) {
        get() = getSubNbt("target_data")?.run{BlockPos(getInt("x"),getInt("y"),getInt("z"))}
    }
    
    private BlockPos setTarget(net.minecraft.world.item.ItemStack stack, @Nullable BlockPos pos) {
        set(value) {value?.apply{getOrCreateSubNbt("target_data").run{putInt("x", x);putInt("y",y);putInt("z",z)}}?:removeSubNbt("target_data")}
    }
}