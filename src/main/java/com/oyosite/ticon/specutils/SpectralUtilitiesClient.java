package com.oyosite.ticon.specutils;

import com.oyosite.ticon.specutils.block.*;
import net.fabricmc.api.*;

@Environment(EnvType.CLIENT)
public class SpectralUtilitiesClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		BlockRegistry.registerClient();
	}
}