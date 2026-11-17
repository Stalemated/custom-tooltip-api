package com.stalemated.customtooltips.core;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.lib.util.color.VanillaTooltipColors;
import com.stalemated.lib.util.math.MathUtils;

import static com.stalemated.lib.util.color.VanillaTooltipColors.BACKGROUND_OPACITY;

public final class TooltipColors {

    private TooltipColors() {
    }

    public static int scaleAlpha(int color, int opacity) {
        int originalAlpha = (color >> 24) & 0xFF;
        int newAlpha = MathUtils.clamp((int) (originalAlpha * (opacity / (float) BACKGROUND_OPACITY)), 0, 255);
        return (color & 0x00FFFFFF) | (newAlpha << 24);
    }

    public static int backgroundStart(TooltipEntry entry, int vanillaColor) {
        int color = hasCustomBackground(entry) ? entry.runtimeCache().backgroundColorStart() : vanillaColor;
        return scaleAlpha(color, backgroundOpacity(entry));
    }

    public static int backgroundEnd(TooltipEntry entry, int vanillaColor) {
        int color = hasCustomBackground(entry) ? entry.runtimeCache().backgroundColorEnd() : vanillaColor;
        return scaleAlpha(color, backgroundOpacity(entry));
    }

    public static int borderStart(TooltipEntry entry, int vanillaColor) {
        int color = hasCustomBorder(entry) ? entry.runtimeCache().borderColorStart() : vanillaColor;
        return scaleAlpha(color, borderOpacity(entry));
    }

    public static int borderEnd(TooltipEntry entry, int vanillaColor) {
        int color = hasCustomBorder(entry) ? entry.runtimeCache().borderColorEnd() : vanillaColor;
        return scaleAlpha(color, borderOpacity(entry));
    }

    public static int outline(TooltipEntry entry, int vanillaColor) {
        return scaleAlpha(vanillaColor, borderOpacity(entry));
    }

    public static int tierifyOutline(TooltipEntry entry, int tierColor) {
        return hasCustomBackground(entry) ? VanillaTooltipColors.BACKGROUND : outline(entry, tierColor);
    }

    public static int backgroundAlpha(TooltipEntry entry, int vanillaColor) {
        return (scaleAlpha(vanillaColor, backgroundOpacity(entry)) >> 24) & 0xFF;
    }

    private static boolean hasCustomBackground(TooltipEntry entry) {
        return entry != null && entry.hasCustomBackground();
    }

    private static boolean hasCustomBorder(TooltipEntry entry) {
        return entry != null && entry.hasCustomBorder();
    }

    private static int backgroundOpacity(TooltipEntry entry) {
        return entry != null ? entry.backgroundOpacity : BACKGROUND_OPACITY;
    }

    private static int borderOpacity(TooltipEntry entry) {
        return entry != null ? entry.borderOpacity : BACKGROUND_OPACITY;
    }
}
