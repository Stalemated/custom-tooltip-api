package com.stalemated.customtooltips.neoforge;

import com.stalemated.customtooltips.CustomTooltipApiClient;
import com.stalemated.customtooltips.gui.TooltipEditScreen;
import com.stalemated.customtooltips.gui.TooltipListScreen;
import com.stalemated.customtooltips.registry.KeybindRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.SynchronousResourceReloader;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(CustomTooltipApiClient.MOD_ID)
public class CustomTooltipApiNeoForge {
    public CustomTooltipApiNeoForge(IEventBus modBus, ModContainer modContainer) {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            modBus.addListener(this::onRegisterKeyMappings);
            modBus.addListener(this::onRegisterReloadListeners);

            NeoForge.EVENT_BUS.addListener(this::onClientTick);
            NeoForge.EVENT_BUS.addListener(this::onItemTooltip);
            NeoForge.EVENT_BUS.addListener(this::onScreenRenderPost);

            initClient(modContainer);
        }
    }
    
    private void initClient(ModContainer modContainer) {
        CustomTooltipApiClient.init();

        modContainer.registerExtensionPoint(IConfigScreenFactory.class,
                (client, parent) -> new TooltipListScreen(parent));
    }

    private void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(KeybindRegistry.openConfigKeybind);
        event.register(KeybindRegistry.holdKeyKeybind);
    }

    private void onRegisterReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener((SynchronousResourceReloader) manager -> CustomTooltipApiClient.onResourceReload());
    }

    private void onClientTick(ClientTickEvent.Post event) {
        CustomTooltipApiClient.onClientTick(MinecraftClient.getInstance());
    }

    private void onItemTooltip(ItemTooltipEvent event) {
        CustomTooltipApiClient.onItemTooltip(event.getItemStack(), event.getToolTip());
    }

    private void onScreenRenderPost(ScreenEvent.Render.Post event) {
        TooltipEditScreen.renderPreview(event.getScreen(), event.getGuiGraphics(), event.getMouseX(), event.getMouseY(), event.getPartialTick());
    }
}
