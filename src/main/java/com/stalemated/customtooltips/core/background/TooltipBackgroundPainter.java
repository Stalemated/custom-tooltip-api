package com.stalemated.customtooltips.core.background;

import com.mojang.blaze3d.systems.RenderSystem;
import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.api.enums.BackgroundType;
import com.stalemated.customtooltips.core.TooltipColors;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

public final class TooltipBackgroundPainter {

    private TooltipBackgroundPainter() {
    }

    public static boolean paint(GuiGraphics graphics, TooltipEntry entry, int x, int y, int width, int height, int z, int vanillaColor) {
        if (entry == null || !entry.hasCustomBackground()) return false;

        switch (typeOf(entry)) {
            case SOLID -> fill(graphics, entry, x, y, width, height, z, vanillaColor);
            case GRADIENT -> graphics.fillGradient(x, y, x + width, y + height, z, TooltipColors.backgroundStart(entry, vanillaColor), TooltipColors.backgroundEnd(entry, vanillaColor));
            case SIMPLE_TEXTURE -> paintTexture(graphics, entry, x, y, width, height, z, vanillaColor,
                    uv -> TextureQuads.simple(graphics, uv, x, y, width, height));
            case TEXTURE -> paintTexture(graphics, entry, x, y, width, height, z, vanillaColor,
                    uv -> TextureQuads.nineSlice(graphics, uv, x, y, width, height, entry.backgroundScale));
            case REPEATING_TEXTURE -> paintTexture(graphics, entry, x, y, width, height, z, vanillaColor,
                    uv -> TextureQuads.repeating(graphics, uv, x, y, width, height, entry.backgroundScale));
        }
        return true;
    }

    private static BackgroundType typeOf(TooltipEntry entry) {
        return entry.backgroundType != null ? entry.backgroundType : BackgroundType.SOLID;
    }

    private static void fill(GuiGraphics graphics, TooltipEntry entry, int x, int y, int width, int height, int z, int vanillaColor) {
        graphics.fill(x, y, x + width, y + height, z, TooltipColors.backgroundStart(entry, vanillaColor));
    }

    private static void paintTexture(GuiGraphics graphics, TooltipEntry entry, int x, int y, int width, int height, int z, int vanillaColor, Consumer<UvRect> quads) {
        ResourceLocation backgroundTexture = entry.runtimeCache().backgroundTexture();
        if (backgroundTexture == null) {
            fill(graphics, entry, x, y, width, height, z, vanillaColor);
            return;
        }

        TextureAtlasSprite sprite = TooltipBackgroundAtlas.sprite(entry.runtimeCache().backgroundSprite());
        boolean inAtlas = !isMissing(sprite);
        ResourceLocation texture = inAtlas ? TooltipBackgroundAtlas.LOCATION : backgroundTexture;

        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, z);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, TooltipColors.backgroundAlpha(entry, vanillaColor) / 255.0F);
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        quads.accept(inAtlas ? UvRect.of(sprite) : UvRect.FULL);

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
        graphics.pose().popPose();
    }

    @SuppressWarnings("resource")
    private static boolean isMissing(TextureAtlasSprite sprite) {
        return sprite == null || sprite.contents().name().getPath().equals("missingno");
    }
}
