package com.stalemated.customtooltips.fabric;

import com.stalemated.customtooltips.CustomTooltipApiClient;
import com.stalemated.customtooltips.gui.screen.TooltipEditScreen;
import com.stalemated.customtooltips.registry.KeybindRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Environment(EnvType.CLIENT)
public class CustomTooltipApiFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CustomTooltipApiClient.init();

        KeyBindingHelper.registerKeyBinding(KeybindRegistry.openConfigKeybind);
        KeyBindingHelper.registerKeyBinding(KeybindRegistry.holdKeyKeybind);

        ClientTickEvents.END_CLIENT_TICK.register(CustomTooltipApiClient::onClientTick);

        ItemTooltipCallback.EVENT.register((stack, context, lines) -> CustomTooltipApiClient.onItemTooltip(stack, lines));

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new IdentifiableResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return new Identifier(CustomTooltipApiClient.MOD_ID, "resources");
            }

            @Override
            public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
                return synchronizer.whenPrepared(null).thenRun(CustomTooltipApiClient::onResourceReload);
            }
        });

        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> ScreenEvents.afterRender(screen).register(TooltipEditScreen::renderPreview));
    }
}
