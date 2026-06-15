package com.stalemated.customtooltips.core;

import com.stalemated.customtooltips.ConfigManager;
import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.lib.util.math.MathUtils;
import net.minecraft.item.ItemStack;

import static com.stalemated.lib.util.color.ColorUtils.DEFAULT_OPACITY;

public class TooltipBackgroundManager {
    private static int currentBackgroundOpacity = -1;
    private static int currentBorderOpacity = -1;
    private static TooltipEntry currentEntry = null;

    public static void setMixinTooltipOpacity(ItemStack stack) {
        int targetBackgroundOpacity = -1;
        int targetBorderOpacity = -1;
        TooltipEntry targetEntry = null;

        for (TooltipEntry entry : ConfigManager.getConfig().entries) {
            if (ConfigManager.getConfig().disabled_entries.contains(entry.getIdentifier())) continue;

            if (entry.matches(stack) && entry.areItemConditionsMet(stack)) {
                if (entry.require_keybind && !TooltipProcessor.isHoldKeyPressed()) continue;
                targetBackgroundOpacity = entry.backgroundOpacity;
                targetBorderOpacity = entry.borderOpacity;
                targetEntry = entry;
                break;
            }
        }

        currentBorderOpacity = targetBorderOpacity;
        currentBackgroundOpacity = targetBackgroundOpacity;
        currentEntry = targetEntry;
    }

    public static void setCurrentBackgroundOpacity(int opacity) {
        currentBackgroundOpacity = opacity;
    }

    public static void setCurrentEntry(TooltipEntry entry) {
        currentEntry = entry;
    }

    public static TooltipEntry getCurrentEntry() {
        return currentEntry;
    }

    public static int getCurrentBackgroundOpacity() {
        return currentBackgroundOpacity;
    }

    public static void setCurrentBorderOpacity(int borderOpacity) {
        currentBorderOpacity = borderOpacity;
    }

    public static int getCurrentBorderOpacity() {
        return currentBorderOpacity;
    }

    public static void clearState() {
        currentBackgroundOpacity = -1;
        currentBorderOpacity = -1;
        currentEntry = null;
    }

    public static int scaleBackgroundAlpha(int color) {
        int value = TooltipBackgroundManager.getCurrentBackgroundOpacity() != -1
                ? TooltipBackgroundManager.getCurrentBackgroundOpacity()
                : DEFAULT_OPACITY;

        int originalAlpha = (color >> 24) & 0xFF;
        int newAlpha = MathUtils.clamp((int) (originalAlpha * (value / 240.0f)), 0, 255);

        return (color & 0x00FFFFFF) | (newAlpha << 24);
    }

    public static int scaleBorderAlpha(int color) {
        int value = TooltipBackgroundManager.getCurrentBorderOpacity() != -1
                ? TooltipBackgroundManager.getCurrentBorderOpacity()
                : DEFAULT_OPACITY;

        int originalAlpha = (color >> 24) & 0xFF;
        int newAlpha = MathUtils.clamp((int) (originalAlpha * (value / 240.0f)), 0, 255);

        return (color & 0x00FFFFFF) | (newAlpha << 24);
    }

    public static int getBorderColorStart(int originalColor) {
        if (currentEntry != null && currentEntry.hasCustomBorder()) {
            return scaleBorderAlpha(currentEntry.getParsedBorderColorStart());
        }
        return scaleBorderAlpha(originalColor);
    }

    public static int getBorderColorEnd(int originalColor) {
        if (currentEntry != null && currentEntry.hasCustomBorder()) {
            return scaleBorderAlpha(currentEntry.getParsedBorderColorEnd());
        }
        return scaleBorderAlpha(originalColor);
    }

    public static int getBackgroundColorStart(int originalColor) {
        if (currentEntry != null && currentEntry.hasCustomBackground()) {
            return scaleBackgroundAlpha(currentEntry.getParsedBackgroundColorStart());
        }
        return scaleBackgroundAlpha(originalColor);
    }

    public static int getBackgroundColorEnd(int originalColor) {
        if (currentEntry != null && currentEntry.hasCustomBackground()) {
            return scaleBackgroundAlpha(currentEntry.getParsedBackgroundColorEnd());
        }
        return scaleBackgroundAlpha(originalColor);
    }
}