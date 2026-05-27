package com.stalemated.customtooltips;

import com.stalemated.customtooltips.core.IconAligner;
import com.stalemated.customtooltips.core.TooltipProcessor;
import com.stalemated.customtooltips.gui.TooltipListScreen;
import com.stalemated.customtooltips.registry.KeybindRegistry;
import com.stalemated.customtooltips.util.ResourcepackManager;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.event.events.client.ClientTooltipEvent;
import dev.architectury.registry.ReloadListenerRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.SynchronousResourceReloader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.stalemated.customtooltips.registry.KeybindRegistry.openConfigKeybind;

@Environment(EnvType.CLIENT)
public class CustomTooltipApiClient {

	public static final String MOD_ID = "customtooltips";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static void init() {
		ResourcepackManager.generateResourcePack();
		ConfigManager.register();
		KeybindRegistry.register();

		ClientTickEvent.CLIENT_POST.register(client -> {
			while (openConfigKeybind.wasPressed()) {
				client.setScreen(new TooltipListScreen(client.currentScreen));
			}
		});

		ReloadListenerRegistry.register(ResourceType.CLIENT_RESOURCES, (SynchronousResourceReloader) manager -> {
            IconAligner.clearCache();
            LOGGER.info("Icon aligner cache cleared due to resource pack reload.");
        });

		ClientTooltipEvent.ITEM.register((stack, lines, context) -> {
			if (stack.isEmpty()) return;

			TooltipProcessor.processTooltipLines(stack, lines);
		});
	}
}