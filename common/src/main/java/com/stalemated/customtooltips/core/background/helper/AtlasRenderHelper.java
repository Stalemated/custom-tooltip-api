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

    public static Sprite getSprite(String backgroundTexture) {
        Identifier spriteId = Identifier.of(backgroundTexture.replace(".png", "").replace("textures/", ""));
        return MinecraftClient.getInstance().getBakedModelManager().getAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).getSprite(spriteId);
    }

    public static void drawNineSlice(DrawContext context, Sprite sprite, int x, int y, int width, int height) {
        int corner = 8;

        float minU = sprite.getMinU();
        float maxU = sprite.getMaxU();
        float minV = sprite.getMinV();
        float maxV = sprite.getMaxV();

        float spanU = maxU - minU;
        float spanV = maxV - minV;

        float cornerU = (spanU / textureWidth) * corner;
        float cornerV = (spanV / textureHeight) * corner;

        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

        // Top Left
        drawQuad(buffer, matrix, x, y, corner, corner, minU, minV, minU + cornerU, minV + cornerV);
        // Top Right
        drawQuad(buffer, matrix, x + width - corner, y, corner, corner, maxU - cornerU, minV, maxU, minV + cornerV);
        // Bottom Left
        drawQuad(buffer, matrix, x, y + height - corner, corner, corner, minU, maxV - cornerV, minU + cornerU, maxV);
        // Bottom Right
        drawQuad(buffer, matrix, x + width - corner, y + height - corner, corner, corner, maxU - cornerU, maxV - cornerV, maxU, maxV);

        // Top Border
        drawQuad(buffer, matrix, x + corner, y, width - corner * 2, corner, minU + cornerU, minV, maxU - cornerU, minV + cornerV);
        // Bottom Border
        drawQuad(buffer, matrix, x + corner, y + height - corner, width - corner * 2, corner, minU + cornerU, maxV - cornerV, maxU - cornerU, maxV);
        // Left Border
        drawQuad(buffer, matrix, x, y + corner, corner, height - corner * 2, minU, minV + cornerV, minU + cornerU, maxV - cornerV);
        // Right Border
        drawQuad(buffer, matrix, x + width - corner, y + corner, corner, height - corner * 2, maxU - cornerU, minV + cornerV, maxU, maxV - cornerV);

        // Center
        drawQuad(buffer, matrix, x + corner, y + corner, width - corner * 2, height - corner * 2, minU + cornerU, minV + cornerV, maxU - cornerU, maxV - cornerV);

        BufferRenderer.drawWithGlobalProgram(buffer.end());
    }

    public static void drawSimple(DrawContext context, Sprite sprite, int x, int y, int width, int height) {
        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

        drawQuad(buffer, matrix, x, y, width, height, sprite.getMinU(), sprite.getMinV(), sprite.getMaxU(), sprite.getMaxV());

        BufferRenderer.drawWithGlobalProgram(buffer.end());
    }

    public static void drawRepeating(DrawContext context, Sprite sprite, int x, int y, int width, int height) {
        float minU = sprite.getMinU();
        float maxU = sprite.getMaxU();
        float minV = sprite.getMinV();
        float maxV = sprite.getMaxV();

        float spanU = maxU - minU;
        float spanV = maxV - minV;

        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

        for (int i = 0; i < width; i += textureWidth) {
            for (int j = 0; j < height; j += textureHeight) {
                int drawWidth = Math.min(textureWidth, width - i);
                int drawHeight = Math.min(textureHeight, height - j);

                float partialMaxU = minU + (spanU / textureWidth) * drawWidth;
                float partialMaxV = minV + (spanV / textureHeight) * drawHeight;

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
