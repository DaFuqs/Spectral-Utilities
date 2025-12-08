package com.oyosite.ticon.specutils.config;
;
import com.oyosite.ticon.specutils.SpectralUtilities;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = SpectralUtilities.MOD_ID)
public class CommonConfig implements ConfigData {
    
    public boolean jadeVinesDropJadeJellyWhenRevived = true;
    
}