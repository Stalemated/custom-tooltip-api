package com.stalemated.customtooltips.core;

import com.stalemated.customtooltips.ConfigManager;
import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.util.MathUtils;
import net.minecraft.item.ItemStack;

public class TooltipBackgroundManager {
    private static int currentOpacity = -1;
    private static TooltipEntry currentEntry = null;

    public static void setMixinTooltipOpacity(ItemStack stack) {
        int targetOpacity = -1;
        TooltipEntry targetEntry = null;

        for (TooltipEntry entry : ConfigManager.getConfig().entries) {
            if (ConfigManager.getConfig().disabled_entries.contains(entry.getIdentifier())) continue;

            if (entry.matches(stack) && entry.areItemConditionsMet(stack)) {
                if (entry.require_keybind && !TooltipProcessor.isHoldKeyPressed()) continue;
                targetOpacity = entry.opacity;
                targetEntry = entry;
                break;
            }
        }

        currentOpacity = targetOpacity;
        currentEntry = targetEntry;
    }

    public static void setCurrentOpacity(int opacity) {
        currentOpacity = opacity;
    }

    public static void setCurrentEntry(TooltipEntry entry) {
        currentEntry = entry;
    }

    public static TooltipEntry getCurrentEntry() {
        return currentEntry;
    }

    public static int getCurrentOpacity() {
        return currentOpacity;
    }

    public static void clearState() {
        currentOpacity = -1;
        currentEntry = null;
    }

    public static int scaleAlpha(int color) {
        int value = TooltipBackgroundManager.getCurrentOpacity() != -1
                ? TooltipBackgroundManager.getCurrentOpacity()
                : TooltipEntry.DEFAULT_OPACITY;

        int originalAlpha = (color >> 24) & 0xFF;
        int newAlpha = MathUtils.clamp((int) (originalAlpha * (value / 240.0f)), 0, 255);

        return (color & 0x00FFFFFF) | (newAlpha << 24);
    }

    public static int getBorderColorStart(int originalColor) {
        if (currentEntry != null && currentEntry.hasCustomBorder()) {
            return scaleAlpha(currentEntry.getParsedBorderColorStart());
        }
        return scaleAlpha(originalColor);
    }

    public static int getBorderColorEnd(int originalColor) {
        if (currentEntry != null && currentEntry.hasCustomBorder()) {
            return scaleAlpha(currentEntry.getParsedBorderColorEnd());
        }
        return scaleAlpha(originalColor);
    }

    public static int getBackgroundColorStart(int originalColor) {
        if (currentEntry != null && currentEntry.hasCustomBackground()) {
            return scaleAlpha(currentEntry.getParsedBackgroundColorStart());
        }
        return scaleAlpha(originalColor);
    }

    public static int getBackgroundColorEnd(int originalColor) {
        if (currentEntry != null && currentEntry.hasCustomBackground()) {
            return scaleAlpha(currentEntry.getParsedBackgroundColorEnd());
        }
        return scaleAlpha(originalColor);
    }
}