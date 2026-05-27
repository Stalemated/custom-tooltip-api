package com.stalemated.customtooltips.forge;

import com.stalemated.customtooltips.CustomTooltipApiClient;
import com.stalemated.customtooltips.gui.TooltipListScreen;
import com.stalemated.customtooltips.registry.KeybindRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(CustomTooltipApiClient.MOD_ID)
public class CustomTooltipApiForge {
    public CustomTooltipApiForge() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

            modBus.addListener(this::onRegisterKeyMappings);
            modBus.addListener(this::onRegisterReloadListeners);

            MinecraftForge.EVENT_BUS.addListener(this::onClientTick);
            MinecraftForge.EVENT_BUS.addListener(this::onItemTooltip);

            initClient();
        }
    }
    
    private void initClient() {
        CustomTooltipApiClient.init();

        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((client, parent) -> new TooltipListScreen(parent)));
    }

    private void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(KeybindRegistry.openConfigKeybind);
        event.register(KeybindRegistry.holdKeyKeybind);
    }

    private void onRegisterReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener((SynchronousResourceReloader) manager -> CustomTooltipApiClient.onResourceReload());
    }

    private void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            CustomTooltipApiClient.onClientTick(MinecraftClient.getInstance());
        }
    }

    private void onItemTooltip(ItemTooltipEvent event) {
        CustomTooltipApiClient.onItemTooltip(event.getItemStack(), event.getToolTip());
    }
}
