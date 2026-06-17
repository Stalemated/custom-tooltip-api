package com.stalemated.customtooltips.core.background.strategies;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.core.background.BackgroundRenderStrategy;
import com.stalemated.customtooltips.core.background.helper.DefaultBackgroundHelper;
import com.stalemated.customtooltips.core.background.helper.BlendHelper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class NineSliceTextureBackgroundStrategy implements BackgroundRenderStrategy {
    @Override
    public void render(DrawContext context, int x, int y, int width, int height, int z, int defaultColor, TooltipEntry entry) {
        if (DefaultBackgroundHelper.renderDefaultBackground(context, x, y, width, height, z, defaultColor, entry) == 1) {
            return;
        }

        Identifier texture = new Identifier(entry.backgroundTexture);

        int corner = 8;
        int texW = 64;
        int texH = 64;

        BlendHelper.enableBlend(context, z, defaultColor);

        // Corners
        context.drawTexture(texture, x, y, corner, corner, 0, 0, corner, corner, texW, texH); // TL
        context.drawTexture(texture, x + width - corner, y, corner, corner, texW - corner, 0, corner, corner, texW, texH); // TR
        context.drawTexture(texture, x, y + height - corner, corner, corner, 0, texH - corner, corner, corner, texW, texH); // BL
        context.drawTexture(texture, x + width - corner, y + height - corner, corner, corner, texW - corner, texH - corner, corner, corner, texW, texH); // BR

        // Borders
        context.drawTexture(texture, x + corner, y, width - corner * 2, corner, corner, 0, texW - corner * 2, corner, texW, texH); // T
        context.drawTexture(texture, x + corner, y + height - corner, width - corner * 2, corner, corner, texH - corner, texW - corner * 2, corner, texW, texH); // B
        context.drawTexture(texture, x, y + corner, corner, height - corner * 2, 0, corner, corner, texH - corner * 2, texW, texH); // L
        context.drawTexture(texture, x + width - corner, y + corner, corner, height - corner * 2, texW - corner, corner, corner, texH - corner * 2, texW, texH); // R

        // Center
        context.drawTexture(texture, x + corner, y + corner, width - corner * 2, height - corner * 2, corner, corner, texW - corner * 2, texH - corner * 2, texW, texH);

        BlendHelper.disableBlend(context);
    }
}