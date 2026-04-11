package com.stalemated.customtooltips;

import com.stalemated.customtooltips.config.TooltipConfig;
import com.stalemated.customtooltips.api.CustomTooltipApi;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class CustomTooltipApiClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ConfigManager.register();

		ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
			if (stack.isEmpty()) return;

			TooltipConfig config = ConfigManager.getConfig();

			List<TooltipEntry> allEntries = new ArrayList<>();
			if (config != null && config.entries != null) {
				allEntries.addAll(config.entries);
			}

			if (CustomTooltipApi.getApiEntries() != null) {
				allEntries.addAll(CustomTooltipApi.getApiEntries());
			}

			if (allEntries.isEmpty()) return;

			boolean shiftPressed = Screen.hasShiftDown();

			for (TooltipEntry entry : allEntries) {
				if (entry == null) continue;

				if (entry.matches(stack)) {

					if (entry.require_shift && !shiftPressed) {
						continue;
					}

					if (entry.position == TooltipEntry.TooltipPosition.REPLACE_NAME) {
						if (!lines.isEmpty()) {
							lines.set(0, entry.getTextComponents().get(0));

							int insertIndex = 1;
							insertIndex += entry.getLineOffset(lines.size());

							List<Text> components = entry.getTextComponents();
							for (int i = 1; i < components.size(); i++) {
								lines.add(insertIndex, components.get(i));
								insertIndex++;
							}
						}
					} else if (entry.position == TooltipEntry.TooltipPosition.REPLACE_ALL) {
						lines.clear();
						lines.addAll(entry.getTextComponents());

					} else if (entry.position == TooltipEntry.TooltipPosition.APPEND) {
						if (!lines.isEmpty()) {
							int insertIndex = entry.getLineOffset(lines.size());

							if (insertIndex >= 0 && insertIndex < lines.size()) {
								Text currentLine = lines.get(insertIndex);
								MutableText newLine = currentLine.copy();

								for (Text component : entry.getTextComponents()) {
									newLine.append(Text.literal(" ")).append(component);
								}

								lines.set(insertIndex, newLine);
							}
						}
					} else if (entry.position == TooltipEntry.TooltipPosition.PREPEND) {
						if (!lines.isEmpty()) {
							int insertIndex = entry.getLineOffset(lines.size());

							if (insertIndex >= 0 && insertIndex < lines.size()) {
								Text currentLine = lines.get(insertIndex);
								MutableText newLine = Text.empty();

								for (Text component : entry.getTextComponents()) {
									newLine.append(component).append(Text.literal(" "));
								}

								newLine.append(currentLine);
								lines.set(insertIndex, newLine);
							}
						}
					} else {
						int insertIndex = lines.size();

						if (!lines.isEmpty()) {
							insertIndex = TooltipEntry.TooltipPosition.TOP == entry.position ? 1 : insertIndex;
							insertIndex += entry.getLineOffset(lines.size());
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
			}
		});
	}
}