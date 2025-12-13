package com.oyosite.ticon.specutils.ink
		;

import de.dafuqs.spectrum.api.energy.*;
import de.dafuqs.spectrum.api.energy.color.*;
import de.dafuqs.spectrum.api.energy.storage.*;
import de.dafuqs.spectrum.helpers.*;
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
		tooltip.add(Component.translatable("item.spectrum.ink_storage.stores_up_to_ink_per_type", new Object[]{Support.getShortenedNumberString(this.maxEnergy)}));
		if (this.storedEnergy > 0L) {
			InkStorage.addInkStoreBulletTooltip(tooltip, this.storedColor, this.storedEnergy);
		}
	}

}