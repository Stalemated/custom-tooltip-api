package com.stalemated.customtooltips;

import com.stalemated.customtooltips.config.TooltipConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class CustomTooltipApiClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ConfigManager.register();

		ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
			if (stack.isEmpty()) return;

			TooltipConfig config = ConfigManager.getConfig();
			if (config == null || config.entries == null) return;

			boolean shiftPressed = Screen.hasShiftDown();

			for (TooltipEntry entry : config.entries) {
				if (entry.matches(stack)) {

					if (entry.require_shift && !shiftPressed) {
						continue;
					}

					int insertIndex = lines.size();

					if ((TooltipEntry.TooltipPosition.TOP == entry.position) && !lines.isEmpty()) {
						insertIndex = 1;
					}

					if (entry.empty_line_before) {
						lines.add(insertIndex, Text.empty());
						if (insertIndex < lines.size()) insertIndex++;
					}

					for (Text component : entry.getTextComponents()) {
						lines.add(insertIndex, component);
						if (insertIndex < lines.size()) insertIndex++;
					}
				}
			}
		});
	}
}