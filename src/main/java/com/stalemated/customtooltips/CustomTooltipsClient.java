package com.stalemated.customtooltips;

import com.stalemated.customtooltips.compat.CompatManager;
import com.stalemated.customtooltips.compat.legendarytooltips.LegendaryTooltipsCompat;
import com.stalemated.customtooltips.config.ConfigManager;
import com.stalemated.customtooltips.core.IconAligner;
import com.stalemated.customtooltips.core.TooltipProcessor;
import com.stalemated.customtooltips.gui.screen.TooltipEditScreen;
import com.stalemated.customtooltips.gui.screen.TooltipListScreen;
import com.stalemated.customtooltips.registry.KeybindRegistry;
import com.stalemated.customtooltips.util.CustomBackgroundManager;
import com.stalemated.customtooltips.util.ResourcepackManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

//? if fabric {
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
//?} elif forge {
/*import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
*///?}

import static com.stalemated.customtooltips.registry.KeybindRegistry.openConfigKeybind;

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
        ConfigManager.register();
        KeybindRegistry.register();
        CompatManager.registerColorProvider(new LegendaryTooltipsCompat());
        registerLoaderEvents();
    }

    //? if fabric {
    private static void registerLoaderEvents() {
        ClientTickEvents.END_CLIENT_TICK.register(CustomTooltipsClient::onClientTick);
        ItemTooltipCallback.EVENT.register((stack, context, lines) -> onItemTooltip(stack, lines));
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new IdentifiableResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId() {
                return new ResourceLocation(CustomTooltips.MOD_ID, "resources");
            }

            @Override
            public @NotNull CompletableFuture<Void> reload(PreparationBarrier synchronizer, ResourceManager manager, ProfilerFiller prepareProfiler, ProfilerFiller applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
                return synchronizer.wait(null).thenRun(CustomTooltipsClient::onResourceReload);
            }
        });
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> ScreenEvents.afterRender(screen).register(TooltipEditScreen::renderPreview));
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
        MinecraftForge.EVENT_BUS.addListener((ScreenEvent.Render.Post event) ->
                TooltipEditScreen.renderPreview(event.getScreen(), event.getGuiGraphics(), event.getMouseX(), event.getMouseY(), event.getPartialTick()));
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((client, parent) -> new TooltipListScreen(parent)));
    }
    *///?}

    private static void onClientTick(Minecraft client) {
        while (openConfigKeybind.consumeClick()) {
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
