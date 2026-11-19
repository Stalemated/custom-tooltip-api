package com.stalemated.customtooltips.core;

import com.stalemated.lib.util.color.VanillaTooltipColors;

public record TooltipPalette(int backgroundTop, int backgroundBottom, int borderTop, int borderBottom) {
    public static final TooltipPalette VANILLA = new TooltipPalette(
            VanillaTooltipColors.BACKGROUND, VanillaTooltipColors.BACKGROUND,
            VanillaTooltipColors.BORDER_TOP, VanillaTooltipColors.BORDER_BOTTOM
    );
}
