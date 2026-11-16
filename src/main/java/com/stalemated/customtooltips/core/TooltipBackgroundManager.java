package com.stalemated.customtooltips.core;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.lib.util.input.KeyBindingUtil;
import com.stalemated.lib.util.math.MathUtils;
import java.util.List;
import net.minecraft.world.item.ItemStack;

import static com.stalemated.customtooltips.registry.KeybindRegistry.holdKeyKeybind;
import static com.stalemated.lib.util.color.VanillaTooltipColors.BACKGROUND_OPACITY;

public class TooltipBackgroundManager {
    private static int currentBackgroundOpacity = -1;
    private static int currentBorderOpacity = -1;
    private static TooltipEntry currentEntry = null;

    public static void setMixinTooltipOpacity(ItemStack stack) {
        List<TooltipEntry> shown = TooltipRegistry.activeEntries().match(stack, KeyBindingUtil.isKeyDownInGui(holdKeyKeybind)).shown();
        if (shown.isEmpty()) {
            clearState();
            return;
        }

        TooltipEntry entry = shown.get(0);
        currentBorderOpacity = entry.borderOpacity;
        currentBackgroundOpacity = entry.backgroundOpacity;
        currentEntry = entry;
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
                : BACKGROUND_OPACITY;

        int originalAlpha = (color >> 24) & 0xFF;
        int newAlpha = MathUtils.clamp((int) (originalAlpha * (value / 240.0f)), 0, 255);

        return (color & 0x00FFFFFF) | (newAlpha << 24);
    }

    public static int scaleBorderAlpha(int color) {
        int value = TooltipBackgroundManager.getCurrentBorderOpacity() != -1
                ? TooltipBackgroundManager.getCurrentBorderOpacity()
                : BACKGROUND_OPACITY;

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