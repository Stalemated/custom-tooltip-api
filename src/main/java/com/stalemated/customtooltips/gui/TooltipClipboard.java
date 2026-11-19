package com.stalemated.customtooltips.gui;

import com.google.gson.JsonParseException;
import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.config.CustomTooltipsConfigRegistry;
import com.stalemated.lib.config.ConfigManager;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.client.Minecraft;

import static com.stalemated.customtooltips.CustomTooltips.LOGGER;

public final class TooltipClipboard {
    private TooltipClipboard() {
    }

    public static void copy(TooltipEntry entry) {
        Minecraft.getInstance().keyboardHandler.setClipboard(CustomTooltipsConfigRegistry.CONFIG.toJson(entry));
    }

    public static Optional<TooltipEntry> paste() {
        return read(Minecraft.getInstance().keyboardHandler.getClipboard(), CustomTooltipsConfigRegistry.CONFIG);
    }

    static Optional<TooltipEntry> read(String clipboard, ConfigManager<?> config) {
        TooltipEntry entry;
        try {
            entry = config.fromJson(clipboard, TooltipEntry.class);
        } catch (JsonParseException e) {
            LOGGER.warn("Clipboard does not hold a tooltip entry: {}", e.getMessage());
            return Optional.empty();
        }
        if (entry == null || entry.target == null) {
            LOGGER.warn("Clipboard does not hold a tooltip entry with a target");
            return Optional.empty();
        }

        entry.uuid = UUID.randomUUID();
        entry.apiEntry = false;
        entry.apiId = null;
        return Optional.of(entry);
    }
}
