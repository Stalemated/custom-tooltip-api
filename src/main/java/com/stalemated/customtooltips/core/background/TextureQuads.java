package com.stalemated.customtooltips.core.background;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Matrix4f;

public final class TextureQuads {
    private static final int GRID_SIZE = 64;
    private static final int CORNER_SIZE = 8;

    private TextureQuads() {
    }

    public static void simple(GuiGraphics graphics, UvRect uv, int x, int y, int width, int height) {
        Matrix4f matrix = graphics.pose().last().pose();
        BufferBuilder buffer = beginQuads();

        drawQuad(buffer, matrix, x, y, width, height, uv.minU(), uv.minV(), uv.maxU(), uv.maxV());

        upload(buffer);
    }

    public static void nineSlice(GuiGraphics graphics, UvRect uv, int x, int y, int width, int height, int scale) {
        float minU = uv.minU();
        float maxU = uv.maxU();
        float minV = uv.minV();
        float maxV = uv.maxV();

        float scaleFactor = scale / 100.0f;
        int scaledCorner = Math.max(1, (int) (CORNER_SIZE * scaleFactor));

        // Ensure corner does not exceed half the width/height to avoid rendering glitches
        scaledCorner = Math.min(scaledCorner, width / 2);
        scaledCorner = Math.min(scaledCorner, height / 2);

        float spanU = maxU - minU;
        float spanV = maxV - minV;

        float cornerU = (spanU / GRID_SIZE) * CORNER_SIZE;
        float cornerV = (spanV / GRID_SIZE) * CORNER_SIZE;

        Matrix4f matrix = graphics.pose().last().pose();
        BufferBuilder buffer = beginQuads();

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

        upload(buffer);
    }

    public static void repeating(GuiGraphics graphics, UvRect uv, int x, int y, int width, int height, int scale) {
        float minU = uv.minU();
        float minV = uv.minV();

        float scaleFactor = scale / 100.0f;
        int scaledTexW = Math.max(1, (int) (GRID_SIZE * scaleFactor));
        int scaledTexH = Math.max(1, (int) (GRID_SIZE * scaleFactor));

        float spanU = uv.maxU() - minU;
        float spanV = uv.maxV() - minV;

        Matrix4f matrix = graphics.pose().last().pose();
        BufferBuilder buffer = beginQuads();

        for (int i = 0; i < width; i += scaledTexW) {
            for (int j = 0; j < height; j += scaledTexH) {
                int drawWidth = Math.min(scaledTexW, width - i);
                int drawHeight = Math.min(scaledTexH, height - j);

                float partialMaxU = minU + spanU * ((float) drawWidth / scaledTexW);
                float partialMaxV = minV + spanV * ((float) drawHeight / scaledTexH);

                drawQuad(buffer, matrix, x + i, y + j, drawWidth, drawHeight, minU, minV, partialMaxU, partialMaxV);
            }
        }

        upload(buffer);
    }

    //? if >=1.21 {
    /*private static BufferBuilder beginQuads() {
        return Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
    }

    private static void upload(BufferBuilder buffer) {
        BufferUploader.drawWithShader(buffer.buildOrThrow());
    }

    private static void drawQuad(BufferBuilder buffer, Matrix4f matrix, int x, int y, int width, int height, float minU, float minV, float maxU, float maxV) {
        buffer.addVertex(matrix, x, y, 0).setUv(minU, minV);
        buffer.addVertex(matrix, x, y + height, 0).setUv(minU, maxV);
        buffer.addVertex(matrix, x + width, y + height, 0).setUv(maxU, maxV);
        buffer.addVertex(matrix, x + width, y, 0).setUv(maxU, minV);
    }
    *///?} else {
    private static BufferBuilder beginQuads() {
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        return buffer;
    }

    private static void upload(BufferBuilder buffer) {
        BufferUploader.drawWithShader(buffer.end());
    }

    private static void drawQuad(BufferBuilder buffer, Matrix4f matrix, int x, int y, int width, int height, float minU, float minV, float maxU, float maxV) {
        buffer.vertex(matrix, x, y, 0).uv(minU, minV).endVertex();
        buffer.vertex(matrix, x, y + height, 0).uv(minU, maxV).endVertex();
        buffer.vertex(matrix, x + width, y + height, 0).uv(maxU, maxV).endVertex();
        buffer.vertex(matrix, x + width, y, 0).uv(maxU, minV).endVertex();
    }
    //?}
}
