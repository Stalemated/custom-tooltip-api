package com.stalemated.customtooltips.core.background;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.core.TooltipColors;
import com.stalemated.customtooltips.core.TooltipLook;
import com.stalemated.customtooltips.core.TooltipPalette;
import java.util.List;
//? if >=26.1 {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
*///?} else {
import net.minecraft.client.gui.GuiGraphics;
//?}
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

public final class TooltipBackgroundPainter {
    private static final int PADDING = 3;

    private TooltipBackgroundPainter() {
    }

    //? if <1.21.6 {
    public static boolean paintAtDepth(GuiGraphics graphics, TooltipLook look, int x, int y, int width, int height, int z, TooltipPalette base) {
        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, z);
        boolean painted = paint(graphics, look, x, y, width, height, base);
        graphics.pose().popPose();
        return painted;
    }
    //?}

    //? if >=26.1 {
    /*public static boolean paint(GuiGraphicsExtractor graphics, TooltipLook look, int x, int y, int width, int height, TooltipPalette base) {
    *///?} else {
    public static boolean paint(GuiGraphics graphics, TooltipLook look, int x, int y, int width, int height, TooltipPalette base) {
    //?}
        if (look.isVanilla()) return false;

        TooltipPalette palette = TooltipColors.palette(look, base);
        int left = x - PADDING;
        int top = y - PADDING;
        int right = x + width + PADDING;
        int bottom = y + height + PADDING;

        graphics.fill(left, top - 1, right, top, palette.backgroundTop());
        graphics.fill(left, bottom, right, bottom + 1, palette.backgroundBottom());
        paintBackground(graphics, look, left, top, right - left, bottom - top, palette);
        graphics.fillGradient(left - 1, top, left, bottom, palette.backgroundTop(), palette.backgroundBottom());
        graphics.fillGradient(right, top, right + 1, bottom, palette.backgroundTop(), palette.backgroundBottom());

        graphics.fillGradient(left, top + 1, left + 1, bottom - 1, palette.borderTop(), palette.borderBottom());
        graphics.fillGradient(right - 1, top + 1, right, bottom - 1, palette.borderTop(), palette.borderBottom());
        graphics.fill(left, top, right, top + 1, palette.borderTop());
        graphics.fill(left, bottom - 1, right, bottom, palette.borderBottom());
        return true;
    }

    //? if >=26.1 {
    /*private static void paintBackground(GuiGraphicsExtractor graphics, TooltipLook look, int x, int y, int width, int height, TooltipPalette palette) {
    *///?} else {
    private static void paintBackground(GuiGraphics graphics, TooltipLook look, int x, int y, int width, int height, TooltipPalette palette) {
    //?}
        TooltipEntry background = look.background();
        ResourceLocation texture = background != null ? background.runtimeCache().backgroundTexture() : null;
        if (texture == null) {
            graphics.fillGradient(x, y, x + width, y + height, palette.backgroundTop(), palette.backgroundBottom());
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

        BackgroundTextureDrawer.draw(graphics, inAtlas ? TooltipBackgroundAtlas.LOCATION : texture, look.backgroundOpacity(), quads);
    }

    @SuppressWarnings("resource")
    private static boolean isMissing(TextureAtlasSprite sprite) {
        return sprite == null || sprite.contents().name().getPath().equals("missingno");
    }
}
