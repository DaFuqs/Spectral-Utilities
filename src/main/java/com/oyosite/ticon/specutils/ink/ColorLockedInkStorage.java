package com.oyosite.ticon.specutils.ink
;
import de.dafuqs.spectrum.api.energy.color.InkColor;
import de.dafuqs.spectrum.api.energy.storage.SingleInkStorage;
import de.dafuqs.spectrum.helpers.Support;
import net.minecraft.network.chat.*;

import java.util.*;

public class ColorLockedInkStorage extends SingleInkStorage {
    
    public ColorLockedInkStorage(long capacity, InkColor color, long amount) {
        super(capacity, color, amount);
    }
    
    @Override
    public boolean accepts(InkColor color) {
        return this.storedColor == color;
    }
    
    @Override
    public long addEnergy(InkColor color, long amount) {
        return super.addEnergy(color, amount);
    }
    
    @Override
    public void setEnergy(Map<InkColor, Long> colors, long total) {
        super.setEnergy(Map.of(this.storedColor, colors.get(this.storedColor)), total);
    }
    
    @Override
    public void addTooltip(List<Component> tooltip) {
        tooltip.add(Component.translatable("item.specutils.ender_flask.tooltip", Support.getShortenedNumberString(this.maxEnergy), this.storedColor.getColoredInkName()));
        tooltip.add(Component.translatable("spectrum.tooltip.ink_powered.bullet." + storedColor.toString().toLowerCase(Locale.ROOT), Support.getShortenedNumberString(this.storedEnergy)));
    }

}