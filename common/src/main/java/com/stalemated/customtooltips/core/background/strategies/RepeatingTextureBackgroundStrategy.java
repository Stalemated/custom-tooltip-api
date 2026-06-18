package com.stalemated.customtooltips.core.background.strategies;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.core.background.BackgroundRenderStrategy;
import com.stalemated.customtooltips.core.background.helper.DefaultBackgroundHelper;
import com.stalemated.customtooltips.core.background.helper.BlendHelper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class RepeatingTextureBackgroundStrategy implements BackgroundRenderStrategy {
    @Override
    public void render(DrawContext context, int x, int y, int width, int height, int z, int defaultColor, TooltipEntry entry) {
        if (DefaultBackgroundHelper.renderDefaultBackground(context, x, y, width, height, z, defaultColor, entry) == 1) {
            return;
        }

        Identifier texture = Identifier.of(entry.backgroundTexture);

        BlendHelper.enableBlend(context, z, defaultColor);

        int texW = 64;
        int texH = 64;

        for (int i = 0; i < width; i += texW) {
            for (int j = 0; j < height; j += texH) {
                int drawWidth = Math.min(texW, width - i);
                int drawHeight = Math.min(texH, height - j);
                context.drawTexture(texture, x + i, y + j, 0, 0, drawWidth, drawHeight, texW, texH);
            }
        }

        BlendHelper.disableBlend(context);
    }
}
