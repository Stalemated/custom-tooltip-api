package com.stalemated.customtooltips.core.background;

import com.mojang.blaze3d.systems.RenderSystem;
import com.stalemated.customtooltips.core.TooltipColors;
import com.stalemated.customtooltips.core.TooltipLook;
import com.stalemated.customtooltips.core.TooltipRuntimeCache;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

public final class TooltipBackgroundPainter {

    private TooltipBackgroundPainter() {
    }

    public static boolean paint(GuiGraphics graphics, TooltipLook look, int x, int y, int width, int height, int z, int vanillaColor) {
        if (!look.changesBackground()) return false;

        switch (look.backgroundType()) {
            case SOLID -> fill(graphics, look, x, y, width, height, z, vanillaColor);
            case GRADIENT -> graphics.fillGradient(x, y, x + width, y + height, z, TooltipColors.backgroundStart(look, vanillaColor), TooltipColors.backgroundEnd(look, vanillaColor));
            case SIMPLE_TEXTURE -> paintTexture(graphics, look, x, y, width, height, z, vanillaColor,
                    uv -> TextureQuads.simple(graphics, uv, x, y, width, height));
            case TEXTURE -> paintTexture(graphics, look, x, y, width, height, z, vanillaColor,
                    uv -> TextureQuads.nineSlice(graphics, uv, x, y, width, height, look.background().backgroundScale));
            case REPEATING_TEXTURE -> paintTexture(graphics, look, x, y, width, height, z, vanillaColor,
                    uv -> TextureQuads.repeating(graphics, uv, x, y, width, height, look.background().backgroundScale));
        }
        return true;
    }

    private static void fill(GuiGraphics graphics, TooltipLook look, int x, int y, int width, int height, int z, int vanillaColor) {
        graphics.fill(x, y, x + width, y + height, z, TooltipColors.backgroundStart(look, vanillaColor));
    }

    private static void paintTexture(GuiGraphics graphics, TooltipLook look, int x, int y, int width, int height, int z, int vanillaColor, Consumer<UvRect> quads) {
        TooltipRuntimeCache cache = look.background().runtimeCache();
        ResourceLocation backgroundTexture = cache.backgroundTexture();
        if (backgroundTexture == null) {
            fill(graphics, look, x, y, width, height, z, vanillaColor);
            return;
        }

        TextureAtlasSprite sprite = TooltipBackgroundAtlas.sprite(cache.backgroundSprite());
        boolean inAtlas = !isMissing(sprite);
        ResourceLocation texture = inAtlas ? TooltipBackgroundAtlas.LOCATION : backgroundTexture;

        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, z);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, TooltipColors.backgroundAlpha(look, vanillaColor) / 255.0F);
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
