package com.stalemated.customtooltips;

import com.stalemated.customtooltips.core.IconAligner;
import com.stalemated.customtooltips.core.TooltipProcessor;
import com.stalemated.customtooltips.gui.TooltipListScreen;
import com.stalemated.customtooltips.util.CustomFontManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class CustomTooltipApiClient implements ClientModInitializer {

	public static final String MOD_ID = "customtooltips";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static KeyBinding openConfigKeybind;

	@Override
	public void onInitializeClient() {
		CustomFontManager.loadAndGenerateFonts();
		ConfigManager.register();

		openConfigKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.customtooltips.open_config",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_UNKNOWN,
				"category.customtooltips.keys"
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (openConfigKeybind.wasPressed()) {
				client.setScreen(new TooltipListScreen(client.currentScreen));
			}
		});

		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			@Override
			public Identifier getFabricId() {
				return new Identifier(MOD_ID, "icon_cache_clearer");
			}

			@Override
			public void reload(ResourceManager manager) {
				IconAligner.clearCache();
				LOGGER.info("Icon aligner cache cleared due to resource pack reload.");
			}
		});

		ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
			if (stack.isEmpty()) return;

			TooltipProcessor.processTooltipLines(stack, lines);
		});
	}
}