package com.stalemated.customtooltips.core.background.helper;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.Sprite;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

public class AtlasRenderHelper {
    private static final int textureWidth = 64;
    private static final int textureHeight = 64;
    private static final int corner = 8;

    public static Sprite getSprite(String backgroundTexture) {
        Identifier spriteId = Identifier.of(backgroundTexture.replace(".png", "").replace("textures/", ""));
        return MinecraftClient.getInstance().getBakedModelManager().getAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).getSprite(spriteId);
    }

    public static boolean isMissingSprite(Sprite sprite) {
        return sprite == null || sprite.getContents().getId().getPath().equals("missingno");
    }

    public static Identifier getRawTextureId(String backgroundTexture) {
        return new Identifier(backgroundTexture);
    }

    public static void drawNineSlice(DrawContext context, Sprite sprite, int x, int y, int width, int height, int scale) {
        float minU = sprite.getMinU();
        float maxU = sprite.getMaxU();
        float minV = sprite.getMinV();
        float maxV = sprite.getMaxV();

        drawNineSliceHelper(context, x, y, width, height, minU, maxU, minV, maxV, scale);
    }

    public static void drawNineSliceStandalone(DrawContext context, int x, int y, int width, int height, int scale) {
        float minU = 0.0f;
        float maxU = 1.0f;
        float minV = 0.0f;
        float maxV = 1.0f;

        drawNineSliceHelper(context, x, y, width, height, minU, maxU, minV, maxV, scale);
    }

    private static void drawNineSliceHelper(DrawContext context, int x, int y, int width, int height, float minU, float maxU, float minV, float maxV, int scale) {
        float scaleFactor = scale / 100.0f;
        int scaledCorner = Math.max(1, (int)(corner * scaleFactor));
        
        // Ensure corner does not exceed half the width/height to avoid rendering glitches
        scaledCorner = Math.min(scaledCorner, width / 2);
        scaledCorner = Math.min(scaledCorner, height / 2);

        float spanU = maxU - minU;
        float spanV = maxV - minV;

        float cornerU = (spanU / textureWidth) * corner;
        float cornerV = (spanV / textureHeight) * corner;

        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

        // Top Left
        drawQuad(buffer, matrix, x, y, scaledCorner, scaledCorner, minU, minV, minU + cornerU, minV + cornerV);
        // Top Right
        drawQuad(buffer, matrix, x + width - scaledCorner, y, scaledCorner, scaledCorner, maxU - cornerU, minV, maxU, minV + cornerV);
        // Bottom Left
        drawQuad(buffer, matrix, x, y + height - scaledCorner, scaledCorner, scaledCorner, minU, maxV - cornerV, minU + cornerU, maxV);
        // Bottom Right
        drawQuad(buffer, matrix, x + width - scaledCorner, y + height - scaledCorner, scaledCorner, scaledCorner, maxU - cornerU, maxV - cornerV, maxU, maxV);

        // Top Border
        drawQuad(buffer, matrix, x + scaledCorner, y, width - scaledCorner * 2, scaledCorner, minU + cornerU, minV, maxU - cornerU, minV + cornerV);
        // Bottom Border
        drawQuad(buffer, matrix, x + scaledCorner, y + height - scaledCorner, width - scaledCorner * 2, scaledCorner, minU + cornerU, maxV - cornerV, maxU - cornerU, maxV);
        // Left Border
        drawQuad(buffer, matrix, x, y + scaledCorner, scaledCorner, height - scaledCorner * 2, minU, minV + cornerV, minU + cornerU, maxV - cornerV);
        // Right Border
        drawQuad(buffer, matrix, x + width - scaledCorner, y + scaledCorner, scaledCorner, height - scaledCorner * 2, maxU - cornerU, minV + cornerV, maxU, maxV - cornerV);

        // Center
        drawQuad(buffer, matrix, x + scaledCorner, y + scaledCorner, width - scaledCorner * 2, height - scaledCorner * 2, minU + cornerU, minV + cornerV, maxU - cornerU, maxV - cornerV);

        BufferRenderer.drawWithGlobalProgram(buffer.end());
    }

    public static void drawSimple(DrawContext context, Sprite sprite, int x, int y, int width, int height) {
        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

        drawQuad(buffer, matrix, x, y, width, height, sprite.getMinU(), sprite.getMinV(), sprite.getMaxU(), sprite.getMaxV());

        BufferRenderer.drawWithGlobalProgram(buffer.end());
    }

    public static void drawSimpleStandalone(DrawContext context, int x, int y, int width, int height) {
        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

        drawQuad(buffer, matrix, x, y, width, height, 0.0f, 0.0f, 1.0f, 1.0f);

        BufferRenderer.drawWithGlobalProgram(buffer.end());
    }

    public static void drawRepeating(DrawContext context, Sprite sprite, int x, int y, int width, int height, int scale) {
        float minU = sprite.getMinU();
        float maxU = sprite.getMaxU();
        float minV = sprite.getMinV();
        float maxV = sprite.getMaxV();

        drawRepeatingHelper(context, x, y, width, height, minU, minV, maxU, maxV, scale);
    }

    public static void drawRepeatingStandalone(DrawContext context, int x, int y, int width, int height, int scale) {
        float minU = 0.0f;
        float maxU = 1.0f;
        float minV = 0.0f;
        float maxV = 1.0f;

        drawRepeatingHelper(context, x, y, width, height, minU, minV, maxU, maxV, scale);
    }

    private static void drawRepeatingHelper(DrawContext context, int x, int y, int width, int height, float minU, float minV,  float maxU, float maxV, int scale) {
        float scaleFactor = scale / 100.0f;
        int scaledTexW = Math.max(1, (int)(textureWidth * scaleFactor));
        int scaledTexH = Math.max(1, (int)(textureHeight * scaleFactor));

        float spanU = maxU - minU;
        float spanV = maxV - minV;

        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

        for (int i = 0; i < width; i += scaledTexW) {
            for (int j = 0; j < height; j += scaledTexH) {
                int drawWidth = Math.min(scaledTexW, width - i);
                int drawHeight = Math.min(scaledTexH, height - j);

                float partialMaxU = minU + spanU * ((float) drawWidth / scaledTexW);
                float partialMaxV = minV + spanV * ((float) drawHeight / scaledTexH);

                drawQuad(buffer, matrix, x + i, y + j, drawWidth, drawHeight, minU, minV, partialMaxU, partialMaxV);
            }
        }

        BufferRenderer.drawWithGlobalProgram(buffer.end());
    }

    private static void drawQuad(BufferBuilder buffer, Matrix4f matrix, int x, int y, int width, int height, float minU, float minV, float maxU, float maxV) {
        buffer.vertex(matrix, x, y, 0).texture(minU, minV);
        buffer.vertex(matrix, x, y + height, 0).texture(minU, maxV);
        buffer.vertex(matrix, x + width, y + height, 0).texture(maxU, maxV);
        buffer.vertex(matrix, x + width, y, 0).texture(maxU, minV);
    }
}
