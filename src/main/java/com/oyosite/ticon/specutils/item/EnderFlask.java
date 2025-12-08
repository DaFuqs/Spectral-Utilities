package com.oyosite.ticon.specutils.item
;

import com.oyosite.ticon.specutils.component.*;
import com.oyosite.ticon.specutils.ink.*;
import de.dafuqs.spectrum.api.energy.*;
import de.dafuqs.spectrum.api.energy.color.*;
import de.dafuqs.spectrum.api.energy.storage.*;
import de.dafuqs.spectrum.items.energy.*;
import net.fabricmc.api.*;
import net.minecraft.network.chat.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.scores.*;

import java.util.*;

public class EnderFlask extends InkFlaskItem {
    
    public static final InkStorage DUMMY_ENERGY_STORAGE = new SingleInkStorage(0);
    
    protected InkColor inkColor;
    
    public EnderFlask(Properties settings, InkColor inkColor) {
        super(settings, StaticEnderInkStorageComponent.inkCapacity);
        this.inkColor = inkColor;
    }
    
    @Override
    public SingleInkStorage getEnergyStorage(ItemStack itemStack) {
        val owner = itemStack?.owner?:return DUMMY_ENERGY_STORAGE
        return ScoreboardComponentEntrypoint.ENDER_FLASK[scoreboard?:return DUMMY_ENERGY_STORAGE][owner, color.dyeColor]
    }
    
    @Override
    public void setEnergyStorage(ItemStack itemStack, InkStorage storage) {
        val owner = itemStack?.owner?:return
        var colorLock = storage as? ColorLockedInkStorage
        if(colorLock==null)colorLock = ColorLockedInkStorage(storage?.maxPerColor?:0, color, storage?.getEnergy(color)?:0)
        ScoreboardComponentEntrypoint.ENDER_FLASK[scoreboard?:return][owner, color.dyeColor] = colorLock
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        Scoreboard scoreboard = player.getScoreboard();
        ItemStack stack = player.getItemInHand(usedHand);
        
        stack.owner = player.uuid;
        stack.ownerName = player.name;
        
        return super.use(level, player, usedHand);
    }
    
    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        
        
        scoreboard?: (entity as? PlayerEntity)?.fetchScoreboard()
    }
    
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        super.appendHoverText(stack, context, tooltip, type);
        
        if(stack.owner==null){
            tooltip.add(Component.translatable("item.specutils.ender_flask.tooltip.unlinked_0"));
            tooltip.add(Component.translatable("item.specutils.ender_flask.tooltip.unlinked_1"));
            return;
        }
        tooltip.add(Component.translatable("item.specutils.ender_flask.tooltip.owner", stack.ownerName));
        
        if(!(getEnergyStorage(stack) instanceof ColorLockedInkStorage)) {
            return;
        }
        
        super.appendHoverText(stack, context, tooltip, type);
        tooltip.removeLast();
    }
    
    private fun PlayerEntity.fetchScoreboard(){
        Companion.scoreboard = this.scoreboard
    }
    
    public class companion {
        Scoreboard scoreboard = null;
        
        UUID owner = null;
        Text ownerName =
        
        var ItemStack.owner: UUID?
            get() = getSubNbt("ender_flask_data")?.takeIf { it.containsUuid("owner") }?.getUuid("owner")
            set(value) = getOrCreateSubNbt("ender_flask_data").let{dat -> value?.let { dat.putUuid("owner", it) }?: dat.remove("owner") }

        var ItemStack.ownerName: Text
            get() = getSubNbt("ender_flask_data")?.takeIf { it.contains("owner_name") }?.getString("owner_name")?.let(Text.Serializer::fromJson)?:Component.Text.translatable("item.specutils.ender_flask.tooltip.unknown_player")
            set(value) { if(value.content!=TextContent.EMPTY)getOrCreateSubNbt("ender_flask_data").putString("owner_name", Text.Serializer.toJson(value)) else getOrCreateSubNbt("ender_flask_data").remove("owner_name") }

    }
    
}