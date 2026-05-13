package com.stalemated.customtooltips.core.background;

import com.stalemated.customtooltips.TooltipEntry;
import net.minecraft.client.gui.DrawContext;

public interface BackgroundRenderStrategy {
    void render(DrawContext context, int x, int y, int width, int height, int z, int defaultColor, TooltipEntry entry);
}