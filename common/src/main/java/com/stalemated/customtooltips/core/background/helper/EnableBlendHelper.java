package com.stalemated.customtooltips.core.background.helper;

import com.mojang.blaze3d.systems.RenderSystem;
import com.stalemated.customtooltips.core.TooltipBackgroundManager;
import net.minecraft.client.gui.DrawContext;

public class EnableBlendHelper {
    public static void enableBlend(DrawContext context, int z, int defaultColor) {
        context.getMatrices().push();
        context.getMatrices().translate(0, 0, z);

        int alphaInt = (TooltipBackgroundManager.scaleBackgroundAlpha(defaultColor) >> 24) & 0xFF;
        float alpha = alphaInt / 255.0F;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
    }
}
