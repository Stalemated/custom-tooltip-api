package com.stalemated.customtooltips.core.background;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.core.TooltipColors;
import com.stalemated.customtooltips.core.TooltipLook;
import com.stalemated.customtooltips.core.TooltipPalette;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

public final class TooltipBackgroundPainter {
    private static final int PADDING = 3;

    private TooltipBackgroundPainter() {
    }

    public static boolean paint(GuiGraphics graphics, TooltipLook look, int x, int y, int width, int height, int z, TooltipPalette base) {
        if (look.isVanilla()) return false;

        TooltipPalette palette = TooltipColors.palette(look, base);
        int left = x - PADDING;
        int top = y - PADDING;
        int right = x + width + PADDING;
        int bottom = y + height + PADDING;

        graphics.fill(left, top - 1, right, top, z, palette.backgroundTop());
        graphics.fill(left, bottom, right, bottom + 1, z, palette.backgroundBottom());
        paintBackground(graphics, look, left, top, right - left, bottom - top, z, palette);
        graphics.fillGradient(left - 1, top, left, bottom, z, palette.backgroundTop(), palette.backgroundBottom());
        graphics.fillGradient(right, top, right + 1, bottom, z, palette.backgroundTop(), palette.backgroundBottom());

        graphics.fillGradient(left, top + 1, left + 1, bottom - 1, z, palette.borderTop(), palette.borderBottom());
        graphics.fillGradient(right - 1, top + 1, right, bottom - 1, z, palette.borderTop(), palette.borderBottom());
        graphics.fill(left, top, right, top + 1, z, palette.borderTop());
        graphics.fill(left, bottom - 1, right, bottom, z, palette.borderBottom());
        return true;
    }

    private static void paintBackground(GuiGraphics graphics, TooltipLook look, int x, int y, int width, int height, int z, TooltipPalette palette) {
        TooltipEntry background = look.background();
        ResourceLocation texture = background != null ? background.runtimeCache().backgroundTexture() : null;
        if (texture == null) {
            graphics.fillGradient(x, y, x + width, y + height, z, palette.backgroundTop(), palette.backgroundBottom());
            return;
        }

        TextureAtlasSprite sprite = TooltipBackgroundAtlas.sprite(background.runtimeCache().backgroundSprite());
        boolean inAtlas = !isMissing(sprite);
        UvRect uv = inAtlas ? UvRect.of(sprite) : UvRect.FULL;

        List<TexturedQuad> quads = switch (background.backgroundType) {
            case SIMPLE_TEXTURE -> TextureQuads.simple(uv, x, y, width, height);
            case TEXTURE -> TextureQuads.nineSlice(uv, x, y, width, height, background.backgroundScale);
            case REPEATING_TEXTURE -> TextureQuads.repeating(uv, x, y, width, height, background.backgroundScale);
            case SOLID, GRADIENT -> List.of();
        };
        if (quads.isEmpty()) return;

        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, z);
        BackgroundTextureDrawer.draw(graphics, inAtlas ? TooltipBackgroundAtlas.LOCATION : texture, look.backgroundOpacity(), quads);
        graphics.pose().popPose();
    }

    @SuppressWarnings("resource")
    private static boolean isMissing(TextureAtlasSprite sprite) {
        return sprite == null || sprite.contents().name().getPath().equals("missingno");
    }
}
