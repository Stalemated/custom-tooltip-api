package com.stalemated.customtooltips.forge;

import com.stalemated.customtooltips.CustomTooltipApiClient;
import com.stalemated.customtooltips.gui.screen.TooltipEditScreen;
import com.stalemated.customtooltips.gui.screen.TooltipListScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(CustomTooltipApiClient.MOD_ID)
@SuppressWarnings("removal")
public class CustomTooltipApiForge {
    public CustomTooltipApiForge() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

            modBus.addListener(this::onRegisterReloadListeners);

            MinecraftForge.EVENT_BUS.addListener(this::onClientTick);
            MinecraftForge.EVENT_BUS.addListener(this::onItemTooltip);
            MinecraftForge.EVENT_BUS.addListener(this::onScreenRenderPost);

            initClient();
        }
    }
    
    private void initClient() {
        CustomTooltipApiClient.init();

        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((client, parent) -> new TooltipListScreen(parent)));
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

    private void onScreenRenderPost(ScreenEvent.Render.Post event) {
        TooltipEditScreen.renderPreview(event.getScreen(), event.getGuiGraphics(), event.getMouseX(), event.getMouseY(), event.getPartialTick());
    }
}
