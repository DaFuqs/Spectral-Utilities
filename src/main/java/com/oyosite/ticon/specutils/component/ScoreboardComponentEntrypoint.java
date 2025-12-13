package com.oyosite.ticon.specutils.component;

import com.oyosite.ticon.specutils.*;
import org.ladysnake.cca.api.v3.component.*;
import org.ladysnake.cca.api.v3.scoreboard.*;

public class ScoreboardComponentEntrypoint implements ScoreboardComponentInitializer {

	public static final ComponentKey<StaticEnderInkStorageComponent> ENDER_FLASK =
			ComponentRegistry.getOrCreate(SpectralUtilities.id("ender_flask"), StaticEnderInkStorageComponent.class);

	@Override
	public void registerScoreboardComponentFactories(ScoreboardComponentFactoryRegistry registry) {
		registry.registerScoreboardComponent(ENDER_FLASK, StaticEnderInkStorageComponent::new);

	}
}