package com.stalemated.customtooltips.core;

import com.stalemated.customtooltips.ConfigManager;
import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.gui.TooltipListScreen;
import com.stalemated.customtooltips.util.ColorUtils;
import com.stalemated.customtooltips.util.ToastManager;
import net.minecraft.client.gui.screen.Screen;

import java.util.ArrayList;

public class TooltipEntryUpdater {

    public static void updateAndSave(TooltipEntry entry, String[] boundColors, boolean isNew, Screen parent) {
        entry.colors = new ArrayList<>();
        boolean hasError = false;

        String finalColor1 = boundColors[0].trim();
        String finalColor2 = boundColors[1].trim();

        if (ColorUtils.isInvalidColorCode(finalColor1)) { hasError = true; finalColor1 = "white"; }
        if (ColorUtils.isInvalidColorCode(finalColor2)) { hasError = true; finalColor2 = "white"; }

        entry.colors.add(finalColor1);
        entry.colors.add(finalColor2);

        if (hasError) ToastManager.showInvalidColorToast();

        if (isNew) {
            ConfigManager.getConfig().entries.add(entry);
        } else {
            entry.invalidateCaches();
        }

        ConfigManager.save();

        if (parent instanceof TooltipListScreen listScreen) {
            listScreen.listWidget.updateEntries(listScreen.searchBox.getText());
        }
    }
}