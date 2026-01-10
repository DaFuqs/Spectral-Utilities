package com.oyosite.ticon.specutils.config

import com.oyosite.ticon.specutils.SpectralUtilities
import me.shedaniel.autoconfig.ConfigData
import me.shedaniel.autoconfig.annotation.Config

@Config(name = SpectralUtilities.MOD_ID)
class CommonConfig : ConfigData {
    @JvmField
    var jadeVinesDropJadeJellyWhenRevived: Boolean = true
    @JvmField
    var AuxiliaryInkSupplierRange: Int = 3
}