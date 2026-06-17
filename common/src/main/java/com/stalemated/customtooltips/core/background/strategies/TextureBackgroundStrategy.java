package com.stalemated.customtooltips.core.background.strategies;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.core.background.BackgroundRenderStrategy;
import com.stalemated.customtooltips.core.background.helper.DefaultBackgroundHelper;
import com.stalemated.customtooltips.core.background.helper.BlendHelper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class TextureBackgroundStrategy implements BackgroundRenderStrategy {
    @Override
    public void render(DrawContext context, int x, int y, int width, int height, int z, int defaultColor, TooltipEntry entry) {
        if (DefaultBackgroundHelper.renderDefaultBackground(context, x, y, width, height, z, defaultColor, entry) == 1) {
            return;
        }

        Identifier texture = new Identifier(entry.backgroundTexture);

        BlendHelper.enableBlend(context, z, defaultColor);

        context.drawTexture(texture, x, y, width, height, 0.0F, 0.0F, 64, 64, 64, 64);

        BlendHelper.disableBlend(context);
    }
}