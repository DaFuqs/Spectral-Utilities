package com.oyosite.ticon.specutils

import com.oyosite.ticon.specutils.block.BlockRegistry
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment

@Environment(EnvType.CLIENT)
class SpectralUtilitiesClient : ClientModInitializer {
    override fun onInitializeClient() {
        BlockRegistry.registerClient()
    }
}