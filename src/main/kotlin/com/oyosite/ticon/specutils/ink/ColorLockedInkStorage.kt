package com.oyosite.ticon.specutils.ink

import de.dafuqs.spectrum.api.energy.InkStorage
import de.dafuqs.spectrum.api.energy.color.InkColor
import de.dafuqs.spectrum.api.energy.storage.SingleInkStorage
import de.dafuqs.spectrum.helpers.Support
import net.minecraft.network.chat.Component
import java.util.Map

class ColorLockedInkStorage(capacity: Long, color: InkColor?, amount: Long) :
    SingleInkStorage(capacity, color, amount) {
    override fun accepts(color: InkColor?): Boolean {
        return this.storedColor === color
    }

    override fun addEnergy(color: InkColor?, amount: Long): Long {
        return super.addEnergy(color, amount)
    }

    override fun setEnergy(colors: MutableMap<InkColor?, Long?>, total: Long) {
        super.setEnergy(Map.of<InkColor?, Long?>(this.storedColor, colors.get(this.storedColor)), total)
    }

    override fun addTooltip(tooltip: MutableList<Component?>) {
        tooltip.add(
            Component.translatable(
                "item.spectrum.ink_storage.stores_up_to_ink_per_type",
                *arrayOf<Any?>(Support.getShortenedNumberString(this.maxEnergy))
            )
        )
        if (this.storedEnergy > 0L) {
            InkStorage.addInkStoreBulletTooltip(tooltip, this.storedColor, this.storedEnergy)
        }
    }
}