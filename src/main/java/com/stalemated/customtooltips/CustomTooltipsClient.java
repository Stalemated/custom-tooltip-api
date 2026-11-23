package com.stalemated.customtooltips;

import com.stalemated.customtooltips.config.CustomTooltipsConfigRegistry;
import com.stalemated.customtooltips.core.IconAligner;
import com.stalemated.customtooltips.core.TooltipProcessor;
import com.stalemated.customtooltips.core.background.TooltipBackgroundAtlas;
import com.stalemated.customtooltips.gui.screen.TooltipListScreen;
import com.stalemated.customtooltips.registry.KeybindRegistry;
import com.stalemated.customtooltips.resource.AssetChoices;
import com.stalemated.customtooltips.resource.ConfigFolderPack;
import com.stalemated.lib.client.SLibConfigScreens;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemStack;

import java.util.List;

//? if fabric {
import com.stalemated.lib.util.ResourceIds;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
//? if <1.21.2 {
import net.minecraft.util.profiling.ProfilerFiller;
//?}
import org.jetbrains.annotations.NotNull;
//?} elif forge {
/*import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
*///?} elif neoforge {
/*import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
*///?}
//? if neoforge && >=1.21.4 {
/*import com.stalemated.lib.util.ResourceIds;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
*///?} elif neoforge {
/*import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
*///?}
//? if neoforge && >=1.20.5 {
/*import net.neoforged.neoforge.client.event.ClientTickEvent;
*///?} elif neoforge {
/*import net.neoforged.neoforge.event.TickEvent;
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
        ConfigFolderPack.register();
        CustomTooltipsConfigRegistry.register();
        KeybindRegistry.register();
        SLibConfigScreens.register(CustomTooltips.MOD_ID, () -> TooltipListScreen::new);
        registerLoaderEvents();
    }

    //? if fabric {
    private static void registerLoaderEvents() {
        ClientTickEvents.END_CLIENT_TICK.register(CustomTooltipsClient::onClientTick);
        //? if >=1.20.5 {
        /*ItemTooltipCallback.EVENT.register((stack, context, flag, lines) -> onItemTooltip(stack, lines));
        *///?} else {
        ItemTooltipCallback.EVENT.register((stack, context, lines) -> onItemTooltip(stack, lines));
        //?}
        ResourceManagerHelper resources = ResourceManagerHelper.get(PackType.CLIENT_RESOURCES);
        resources.registerReloadListener(new IdentifiableResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId() {
                return ResourceIds.of(CustomTooltips.MOD_ID, "tooltip_backgrounds");
            }

            //? if >=1.21.9 {
            /*@Override
            public @NotNull CompletableFuture<Void> reload(PreparableReloadListener.SharedState state, Executor backgroundExecutor, PreparableReloadListener.PreparationBarrier barrier, Executor gameExecutor) {
                return TooltipBackgroundAtlas.reload(state, backgroundExecutor, barrier, gameExecutor);
            }
            *///?} elif >=1.21.2 {
            /*@Override
            public @NotNull CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier barrier, ResourceManager manager, Executor backgroundExecutor, Executor gameExecutor) {
                return TooltipBackgroundAtlas.reload(barrier, manager, backgroundExecutor, gameExecutor);
            }
            *///?} else {
            @Override
            public @NotNull CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier barrier, ResourceManager manager, ProfilerFiller preparationProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
                return TooltipBackgroundAtlas.reload(barrier, manager, preparationProfiler, reloadProfiler, backgroundExecutor, gameExecutor);
            }
            //?}
        });
        resources.registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId() {
                return ResourceIds.of(CustomTooltips.MOD_ID, "resources");
            }

            @Override
            public void onResourceManagerReload(ResourceManager manager) {
                onResourceReload(manager);
            }
        });
    }
    //?} elif forge {
    /*@SuppressWarnings("removal")
    private static void registerLoaderEvents() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener((RegisterClientReloadListenersEvent event) -> {
            event.registerReloadListener(TooltipBackgroundAtlas::reload);
            event.registerReloadListener((ResourceManagerReloadListener) CustomTooltipsClient::onResourceReload);
        });
        MinecraftForge.EVENT_BUS.addListener((TickEvent.ClientTickEvent event) -> {
            if (event.phase == TickEvent.Phase.END) onClientTick(Minecraft.getInstance());
        });
        MinecraftForge.EVENT_BUS.addListener((ItemTooltipEvent event) -> onItemTooltip(event.getItemStack(), event.getToolTip()));
    }
    *///?} elif neoforge {
    /*private static void registerLoaderEvents() {
        registerReloadListeners();
        registerClientTick();
        NeoForge.EVENT_BUS.addListener((ItemTooltipEvent event) -> onItemTooltip(event.getItemStack(), event.getToolTip()));
    }
    *///?}

    //? if neoforge && >=1.21.4 {
    /*private static void registerReloadListeners() {
        ModLoadingContext.get().getActiveContainer().getEventBus().addListener((AddClientReloadListenersEvent event) -> {
            event.addListener(ResourceIds.of(CustomTooltips.MOD_ID, "tooltip_backgrounds"), TooltipBackgroundAtlas::reload);
            event.addListener(ResourceIds.of(CustomTooltips.MOD_ID, "resources"), (ResourceManagerReloadListener) CustomTooltipsClient::onResourceReload);
        });
    }
    *///?} elif neoforge {
    /*private static void registerReloadListeners() {
        ModLoadingContext.get().getActiveContainer().getEventBus().addListener((RegisterClientReloadListenersEvent event) -> {
            event.registerReloadListener(TooltipBackgroundAtlas::reload);
            event.registerReloadListener((ResourceManagerReloadListener) CustomTooltipsClient::onResourceReload);
        });
    }
    *///?}

    //? if neoforge && >=1.20.5 {
    /*private static void registerClientTick() {
        NeoForge.EVENT_BUS.addListener((ClientTickEvent.Post event) -> onClientTick(Minecraft.getInstance()));
    }
    *///?} elif neoforge {
    /*private static void registerClientTick() {
        NeoForge.EVENT_BUS.addListener((TickEvent.ClientTickEvent event) -> {
            if (event.phase == TickEvent.Phase.END) onClientTick(Minecraft.getInstance());
        });
    }
    *///?}

    private static void onClientTick(Minecraft client) {
        while (OPEN_CONFIG.consumeClick()) {
            //? if >=26.2 {
            /*client.gui.setScreen(new TooltipListScreen(client.gui.screen()));
            *///?} else {
            client.setScreen(new TooltipListScreen(client.screen));
            //?}
        }
    }

    private static void onResourceReload(ResourceManager manager) {
        IconAligner.clearCache();
        CustomTooltipsConfigRegistry.EXTERNAL_BACKGROUNDS.reload();
        AssetChoices.reload(manager);
    }

    private static void onItemTooltip(ItemStack stack, List<Component> lines) {
        if (stack.isEmpty()) return;
        TooltipProcessor.processTooltipLines(stack, lines);
    }
}
