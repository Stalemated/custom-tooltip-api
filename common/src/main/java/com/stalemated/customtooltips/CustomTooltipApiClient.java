package com.stalemated.customtooltips;

import com.stalemated.customtooltips.compat.CompatManager;
import com.stalemated.customtooltips.compat.legendarytooltips.LegendaryTooltipsCompat;
import com.stalemated.customtooltips.core.IconAligner;
import com.stalemated.customtooltips.core.TooltipProcessor;
import com.stalemated.customtooltips.gui.screen.TooltipListScreen;
import com.stalemated.customtooltips.registry.KeybindRegistry;
import com.stalemated.customtooltips.util.CustomBackgroundManager;
import com.stalemated.customtooltips.util.ResourcepackManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.stalemated.customtooltips.registry.KeybindRegistry.openConfigKeybind;

@Environment(EnvType.CLIENT)
public class CustomTooltipApiClient {

	public static final String MOD_ID = "customtooltips";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static void init() {
		ResourcepackManager.generateResourcePack();
		ConfigManager.register();
		KeybindRegistry.register();
		CompatManager.registerColorProvider(new LegendaryTooltipsCompat());
	}

	public static void onClientTick(MinecraftClient client) {
		while (openConfigKeybind.wasPressed()) {
			client.setScreen(new TooltipListScreen(client.currentScreen));
		}
	}

	public static void onResourceReload() {
		IconAligner.clearCache();
		CustomBackgroundManager.reloadExternalBackgrounds();
	}

	public static void onItemTooltip(ItemStack stack, List<Text> lines) {
		if (stack.isEmpty()) return;
		TooltipProcessor.processTooltipLines(stack, lines);
	}
}