package com.oyosite.ticon.specutils.component
;
import com.oyosite.ticon.specutils.*;
import net.minecraft.resources.*;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;

public class ScoreboardComponentEntrypoint extends ScoreboardComponentInitializer {

    public static String ENDER_FLASK = ComponentRegistry.getOrCreate(SpectralUtilities.id("ender_flask"), StaticEnderInkStorageComponent::class)

    public void registerScoreboardComponentFactories(registry: ScoreboardComponentFactoryRegistry) {
        registry.registerScoreboardComponent(ENDER_FLASK, ::StaticEnderInkStorageComponent)
    }

}