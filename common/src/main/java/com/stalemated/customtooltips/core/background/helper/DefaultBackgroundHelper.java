package com.stalemated.customtooltips.core.background.helper;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.core.TooltipBackgroundManager;
import net.minecraft.client.gui.DrawContext;

public class DefaultBackgroundHelper {
    public static int renderDefaultBackground(DrawContext context, int x, int y, int width, int height, int z, int defaultColor, TooltipEntry entry) {
        if (entry.backgroundTexture == null || entry.backgroundTexture.isEmpty()) {
            context.fill(x, y, x + width, y + height, z, TooltipBackgroundManager.getBackgroundColorStart(defaultColor));
            return 1;
        }
        return 0;
    }
}
