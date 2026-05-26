package com.stalemated.customtooltips.core.background.strategies;

import com.mojang.blaze3d.systems.RenderSystem;
import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.core.TooltipBackgroundManager;
import com.stalemated.customtooltips.core.background.BackgroundRenderStrategy;
import com.stalemated.customtooltips.core.background.helper.EnableBlendHelper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class NineSliceTextureBackgroundStrategy implements BackgroundRenderStrategy {
    @Override
    public void render(DrawContext context, int x, int y, int width, int height, int z, int defaultColor, TooltipEntry entry) {
        if (entry.backgroundTexture == null || entry.backgroundTexture.isEmpty()) {
            context.fill(x, y, x + width, y + height, z, TooltipBackgroundManager.getBackgroundColorStart(defaultColor));
            return;
        }

        Identifier texture = new Identifier(entry.backgroundTexture);

        int corner = 8;
        int texW = 64;
        int texH = 64;

        EnableBlendHelper.enableBlend(context, z, defaultColor);

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

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        context.getMatrices().pop();
    }
}