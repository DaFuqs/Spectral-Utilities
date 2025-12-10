package com.oyosite.ticon.specutils.item;

import com.oyosite.ticon.specutils.component.*;
import com.oyosite.ticon.specutils.ink.*;
import de.dafuqs.spectrum.api.energy.*;
import de.dafuqs.spectrum.api.energy.color.*;
import de.dafuqs.spectrum.api.energy.storage.*;
import de.dafuqs.spectrum.items.energy.*;
import net.minecraft.core.component.*;
import net.minecraft.network.chat.*;
import net.minecraft.server.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.*;
import net.minecraft.world.level.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class EnderFlask extends InkFlaskItem {
    
    public static final SingleInkStorage DUMMY_ENERGY_STORAGE = new SingleInkStorage(0);
    public static ServerScoreboard scoreboard;
    
    protected InkColor inkColor;
    
    public EnderFlask(Properties settings, InkColor inkColor) {
        super(settings, StaticEnderInkStorageComponent.CAPACITY);
        this.inkColor = inkColor;
    }
    
    @Override
    public SingleInkStorage getEnergyStorage(ItemStack itemStack) {
        Optional<StaticEnderInkStorageComponent> storage = ScoreboardComponentEntrypoint.ENDER_FLASK.maybeGet(getOwner(itemStack));
        if(storage.isEmpty()) {
            return DUMMY_ENERGY_STORAGE;
        }
        return storage.get().get(getOwner(itemStack).id().get(), inkColor.getDyeColor().get());
    }
    
    @Override
    public void setEnergyStorage(ItemStack itemStack, InkStorage storage) {
        @Nullable ResolvableProfile owner = getOwner(itemStack);
        if(owner == null) {
            return;
        }
        ColorLockedInkStorage s = (ColorLockedInkStorage) storage;
        ScoreboardComponentEntrypoint.ENDER_FLASK.get(scoreboard).set(owner.id().get(), inkColor.getDyeColor().get(), s);
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        setOwner(stack, player);
        
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
    
    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        
        if(scoreboard != null && entity instanceof Player player) {
            player.getScoreboard();
        }
    }
    
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        super.appendHoverText(stack, context, tooltip, type);

        @Nullable ResolvableProfile profile = getOwner(stack);
        if(profile == null){
            tooltip.add(Component.translatable("item.specutils.ender_flask.tooltip.unlinked_0"));
            tooltip.add(Component.translatable("item.specutils.ender_flask.tooltip.unlinked_1"));
            return;
        } else {
            tooltip.add(Component.translatable("item.specutils.ender_flask.tooltip.owner", profile.name()));
        }
        
        if(!(getEnergyStorage(stack) instanceof ColorLockedInkStorage)) {
            return;
        }
        
        super.appendHoverText(stack, context, tooltip, type);
        tooltip.removeLast();
    }

    public static @Nullable ResolvableProfile getOwner(ItemStack stack) {
        return stack.get(DataComponents.PROFILE);
    }

    private static void setOwner(ItemStack stack, Player player) {
        setOwner(stack, new ResolvableProfile(player.getGameProfile()));
    }

    public static void setOwner(ItemStack stack, ResolvableProfile profile) {
        stack.set(DataComponents.PROFILE, profile);
    }

}