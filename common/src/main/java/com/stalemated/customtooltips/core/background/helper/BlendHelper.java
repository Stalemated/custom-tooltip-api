package com.stalemated.customtooltips.core.background.helper;

import com.mojang.blaze3d.systems.RenderSystem;
import com.stalemated.customtooltips.core.TooltipBackgroundManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

public class BlendHelper {
    public static void enableBlend(GuiGraphics context, int z, int defaultColor) {
        context.pose().pushPose();
        context.pose().translate(0, 0, z);

        int alphaInt = (TooltipBackgroundManager.scaleBackgroundAlpha(defaultColor) >> 24) & 0xFF;
        float alpha = alphaInt / 255.0F;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
    }

    public static void disableBlend(GuiGraphics context) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
        context.pose().popPose();
    }

    public static void setupAtlasRendering(GuiGraphics context, int z, int defaultColor) {
        enableBlend(context, z, defaultColor);
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
    }

    public static void setupStandaloneRendering(GuiGraphics context, ResourceLocation texture, int z, int defaultColor) {
        enableBlend(context, z, defaultColor);
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
    }
}
