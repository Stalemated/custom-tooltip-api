package com.stalemated.customtooltips.forge;

import com.stalemated.customtooltips.CustomTooltipApiClient;
import com.stalemated.customtooltips.gui.TooltipListScreen;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(CustomTooltipApiClient.MOD_ID)
public class CustomTooltipApiForge {
    public CustomTooltipApiForge() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus(CustomTooltipApiClient.MOD_ID, modBus);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            initClient();
        }
    }
    
    private void initClient() {
        CustomTooltipApiClient.init();

        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((client, parent) -> new TooltipListScreen(parent)));
    }
}
