package com.stalemated.customtooltips.fabric;

import com.stalemated.customtooltips.CustomTooltipApiClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class CustomTooltipApiFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CustomTooltipApiClient.init();
    }
}
