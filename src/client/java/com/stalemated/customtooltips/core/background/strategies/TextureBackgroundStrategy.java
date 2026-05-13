package com.stalemated.customtooltips.core.background.strategies;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.core.TooltipBackgroundManager;
import com.stalemated.customtooltips.core.background.BackgroundRenderStrategy;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class TextureBackgroundStrategy implements BackgroundRenderStrategy {
    @Override
    public void render(DrawContext context, int x, int y, int width, int height, int z, int defaultColor, TooltipEntry entry) {
        if (entry.backgroundTexture == null || entry.backgroundTexture.isEmpty()) {
            context.fill(x, y, x + width, y + height, z, TooltipBackgroundManager.getBackgroundColorStart(defaultColor));
            return;
        }

        Identifier texture = new Identifier(entry.backgroundTexture);

        context.getMatrices().push();
        context.getMatrices().translate(0, 0, z);

        context.drawTexture(texture, x, y, width, height, 0.0F, 0.0F, 64, 64, 64, 64);

        context.getMatrices().pop();
    }
}