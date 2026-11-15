package com.stalemated.customtooltips.core.background.strategies;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.core.TooltipBackgroundManager;
import com.stalemated.customtooltips.core.background.BackgroundRenderStrategy;
import net.minecraft.client.gui.GuiGraphics;

public class SolidBackgroundStrategy implements BackgroundRenderStrategy {
    @Override
    public void render(GuiGraphics context, int x, int y, int width, int height, int z, int defaultColor, TooltipEntry entry) {
        int color = TooltipBackgroundManager.getBackgroundColorStart(defaultColor);
        context.fill(x, y, x + width, y + height, z, color);
    }
}