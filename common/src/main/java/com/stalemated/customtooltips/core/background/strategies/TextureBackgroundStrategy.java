package com.stalemated.customtooltips.core.background.strategies;

import com.mojang.blaze3d.systems.RenderSystem;
import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.core.TooltipBackgroundManager;
import com.stalemated.customtooltips.core.background.BackgroundRenderStrategy;
import com.stalemated.customtooltips.core.background.helper.EnableBlendHelper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class TextureBackgroundStrategy implements BackgroundRenderStrategy {
    @Override
    public void render(DrawContext context, int x, int y, int width, int height, int z, int defaultColor, TooltipEntry entry) {
        if (entry.backgroundTexture == null || entry.backgroundTexture.isEmpty()) {
            context.fill(x, y, x + width, y + height, z, TooltipBackgroundManager.getBackgroundColorStart(defaultColor));
            return;
        }

        Identifier texture = Identifier.of(entry.backgroundTexture);

        EnableBlendHelper.enableBlend(context, z, defaultColor);

        context.drawTexture(texture, x, y, width, height, 0.0F, 0.0F, 64, 64, 64, 64);

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        context.getMatrices().pop();
    }
}