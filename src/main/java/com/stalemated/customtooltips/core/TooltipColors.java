package com.stalemated.customtooltips.core;

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

    public static int backgroundStart(TooltipLook look, int vanillaColor) {
        int color = look.background() != null ? look.background().runtimeCache().backgroundColorStart() : vanillaColor;
        return scaleAlpha(color, look.backgroundOpacity());
    }

    public static int backgroundEnd(TooltipLook look, int vanillaColor) {
        int color = look.background() != null ? look.background().runtimeCache().backgroundColorEnd() : vanillaColor;
        return scaleAlpha(color, look.backgroundOpacity());
    }

    public static int borderStart(TooltipLook look, int vanillaColor) {
        int color = look.border() != null ? look.border().runtimeCache().borderColorStart() : vanillaColor;
        return scaleAlpha(color, look.borderOpacity());
    }

    public static int borderEnd(TooltipLook look, int vanillaColor) {
        int color = look.border() != null ? look.border().runtimeCache().borderColorEnd() : vanillaColor;
        return scaleAlpha(color, look.borderOpacity());
    }

    public static int outline(TooltipLook look, int vanillaColor) {
        return scaleAlpha(vanillaColor, look.borderOpacity());
    }

    public static int tierifyOutline(TooltipLook look, int tierColor) {
        return look.changesBackground() ? VanillaTooltipColors.BACKGROUND : outline(look, tierColor);
    }

    public static int backgroundAlpha(TooltipLook look, int vanillaColor) {
        return (scaleAlpha(vanillaColor, look.backgroundOpacity()) >> 24) & 0xFF;
    }
}
