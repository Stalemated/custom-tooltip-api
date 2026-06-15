package com.stalemated.customtooltips.core;

import com.stalemated.customtooltips.ConfigManager;
import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.gui.screen.TooltipListScreen;
import com.stalemated.customtooltips.util.ToastManager;
import com.stalemated.lib.util.color.ColorUtils;
import net.minecraft.client.gui.screen.Screen;

import java.util.ArrayList;

public class TooltipEntryUpdater {

    public static void updateAndSave(TooltipEntry entry, String[] boundColors, String[] boundBorderColors, String[] boundBackgroundColors, boolean isNew, Screen parent) {
        entry.colors = new ArrayList<>();
        entry.borderColors = new ArrayList<>();
        entry.backgroundColors = new ArrayList<>();

        boolean hasError = false;

        String finalColor1 = boundColors[0].trim();
        String finalColor2 = boundColors[1].trim();

        String finalBorderColor1 = boundBorderColors[0].trim();
        String finalBorderColor2 = boundBorderColors[1].trim();

        String finalBackgroundColor1 = boundBackgroundColors[0].trim();
        String finalBackgroundColor2 = boundBackgroundColors[1].trim();

        if (ColorUtils.isInvalidColorCode(finalColor1)) { hasError = true; finalColor1 = "white"; }
        if (ColorUtils.isInvalidColorCode(finalColor2)) { hasError = true; finalColor2 = "white"; }
        if (ColorUtils.isInvalidARGBColor(finalBorderColor1, 0)) { hasError = true; finalBorderColor1 = "#505000FF"; }
        if (ColorUtils.isInvalidARGBColor(finalBorderColor2, 1)) { hasError = true; finalBorderColor1 = "#5028007F"; }
        if (ColorUtils.isInvalidARGBColor(finalBackgroundColor1, 0)) {hasError = true; finalBackgroundColor1 = "#F0100010"; }
        if (ColorUtils.isInvalidARGBColor(finalBackgroundColor2, 1)) {hasError = true; finalBackgroundColor1 = "#F0100010"; }

        entry.colors.add(finalColor1);
        entry.colors.add(finalColor2);

        entry.borderColors.add(finalBorderColor1);
        entry.borderColors.add(finalBorderColor2);

        entry.backgroundColors.add(finalBackgroundColor1);
        entry.backgroundColors.add(finalBackgroundColor2);

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