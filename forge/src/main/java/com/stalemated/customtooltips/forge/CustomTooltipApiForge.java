package com.stalemated.customtooltips.forge;

import com.stalemated.customtooltips.CustomTooltipApiClient;
import com.stalemated.customtooltips.gui.TooltipListScreen;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CustomTooltipApiClient.MOD_ID)
public class CustomTooltipApiForge {
    public CustomTooltipApiForge() {
        EventBuses.registerModEventBus(CustomTooltipApiClient.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        CustomTooltipApiClient.init();
        
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((client, parent) -> new TooltipListScreen(parent)));
    }
}
