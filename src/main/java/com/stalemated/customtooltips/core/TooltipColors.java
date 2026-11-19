package com.stalemated.customtooltips.core;

import com.stalemated.lib.util.math.MathUtils;

public final class TooltipColors {
    public static final float UNCHANGED_OPACITY = 1f;

    private TooltipColors() {
    }

    public static int scaleAlpha(int color, float opacity) {
        int originalAlpha = (color >> 24) & 0xFF;
        int newAlpha = MathUtils.clamp(Math.round(originalAlpha * opacity), 0, 255);
        return (color & 0x00FFFFFF) | (newAlpha << 24);
    }

    public static TooltipPalette palette(TooltipLook look, TooltipPalette base) {
        return new TooltipPalette(
                backgroundStart(look, base.backgroundTop()),
                backgroundEnd(look, base.backgroundBottom()),
                borderStart(look, base.borderTop()),
                borderEnd(look, base.borderBottom()));
    }

    public static int backgroundStart(TooltipLook look, int baseColor) {
        int color = look.background() != null ? look.background().runtimeCache().backgroundColorStart() : baseColor;
        return scaleAlpha(color, look.backgroundOpacity());
    }

    public static int backgroundEnd(TooltipLook look, int baseColor) {
        int color = look.background() != null ? look.background().runtimeCache().backgroundColorEnd() : baseColor;
        return scaleAlpha(color, look.backgroundOpacity());
    }

    public static int borderStart(TooltipLook look, int baseColor) {
        int color = look.border() != null ? look.border().runtimeCache().borderColorStart() : baseColor;
        return scaleAlpha(color, look.borderOpacity());
    }

    public static int borderEnd(TooltipLook look, int baseColor) {
        int color = look.border() != null ? look.border().runtimeCache().borderColorEnd() : baseColor;
        return scaleAlpha(color, look.borderOpacity());
    }
}
