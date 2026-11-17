package com.stalemated.customtooltips;

import com.stalemated.customtooltips.config.CustomTooltipsConfigRegistry;
import com.stalemated.customtooltips.core.IconAligner;
import com.stalemated.customtooltips.core.TooltipProcessor;
import com.stalemated.customtooltips.gui.screen.TooltipListScreen;
import com.stalemated.customtooltips.registry.KeybindRegistry;
import com.stalemated.customtooltips.util.CustomBackgroundManager;
import com.stalemated.customtooltips.util.ResourcepackManager;
import com.stalemated.lib.client.SLibConfigScreens;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

//? if fabric {
import com.stalemated.lib.util.ResourceIds;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
//?} elif forge {
/*import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
*///?}

import static com.stalemated.customtooltips.registry.KeybindRegistry.OPEN_CONFIG;

public final class CustomTooltipsClient //? if fabric
implements ClientModInitializer
{
    //? if fabric {
    @Override
    public void onInitializeClient() {
        init();
    }
    //?} else {
    /*private CustomTooltipsClient() {
    }
    *///?}

    static void init() {
        ResourcepackManager.generateResourcePack();
        CustomTooltipsConfigRegistry.register();
        KeybindRegistry.register();
        SLibConfigScreens.register(CustomTooltips.MOD_ID, () -> TooltipListScreen::new);
        registerLoaderEvents();
    }

    //? if fabric {
    private static void registerLoaderEvents() {
        ClientTickEvents.END_CLIENT_TICK.register(CustomTooltipsClient::onClientTick);
        ItemTooltipCallback.EVENT.register((stack, context, lines) -> onItemTooltip(stack, lines));
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId() {
                return ResourceIds.of(CustomTooltips.MOD_ID, "resources");
            }

            @Override
            public void onResourceManagerReload(ResourceManager manager) {
                onResourceReload();
            }
        });
    }
    //?} elif forge {
    /*@SuppressWarnings("removal")
    private static void registerLoaderEvents() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener((RegisterClientReloadListenersEvent event) ->
                event.registerReloadListener((ResourceManagerReloadListener) manager -> onResourceReload()));
        MinecraftForge.EVENT_BUS.addListener((TickEvent.ClientTickEvent event) -> {
            if (event.phase == TickEvent.Phase.END) onClientTick(Minecraft.getInstance());
        });
        MinecraftForge.EVENT_BUS.addListener((ItemTooltipEvent event) -> onItemTooltip(event.getItemStack(), event.getToolTip()));
    }
    *///?}

    private static void onClientTick(Minecraft client) {
        while (OPEN_CONFIG.consumeClick()) {
            client.setScreen(new TooltipListScreen(client.screen));
        }
    }

    private static void onResourceReload() {
        IconAligner.clearCache();
        CustomBackgroundManager.reloadExternalBackgrounds();
    }

    private static void onItemTooltip(ItemStack stack, List<Component> lines) {
        if (stack.isEmpty()) return;
        TooltipProcessor.processTooltipLines(stack, lines);
    }
}
