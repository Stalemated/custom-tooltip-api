package com.stalemated.customtooltips.core.background;

import java.util.ArrayList;
import java.util.List;

public final class TextureQuads {
    private static final int GRID_SIZE = 64;
    private static final int CORNER_SIZE = 8;

    private TextureQuads() {
    }

    public static List<TexturedQuad> simple(UvRect uv, int x, int y, int width, int height) {
        return List.of(new TexturedQuad(x, y, width, height, uv.minU(), uv.minV(), uv.maxU(), uv.maxV()));
    }

    public static List<TexturedQuad> nineSlice(UvRect uv, int x, int y, int width, int height, int scale) {
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

        return List.of(
                // Top Left
                new TexturedQuad(x, y, scaledCorner, scaledCorner, minU, minV, minU + cornerU, minV + cornerV),
                // Top Right
                new TexturedQuad(x + width - scaledCorner, y, scaledCorner, scaledCorner, maxU - cornerU, minV, maxU, minV + cornerV),
                // Bottom Left
                new TexturedQuad(x, y + height - scaledCorner, scaledCorner, scaledCorner, minU, maxV - cornerV, minU + cornerU, maxV),
                // Bottom Right
                new TexturedQuad(x + width - scaledCorner, y + height - scaledCorner, scaledCorner, scaledCorner, maxU - cornerU, maxV - cornerV, maxU, maxV),

                // Top Border
                new TexturedQuad(x + scaledCorner, y, width - scaledCorner * 2, scaledCorner, minU + cornerU, minV, maxU - cornerU, minV + cornerV),
                // Bottom Border
                new TexturedQuad(x + scaledCorner, y + height - scaledCorner, width - scaledCorner * 2, scaledCorner, minU + cornerU, maxV - cornerV, maxU - cornerU, maxV),
                // Left Border
                new TexturedQuad(x, y + scaledCorner, scaledCorner, height - scaledCorner * 2, minU, minV + cornerV, minU + cornerU, maxV - cornerV),
                // Right Border
                new TexturedQuad(x + width - scaledCorner, y + scaledCorner, scaledCorner, height - scaledCorner * 2, maxU - cornerU, minV + cornerV, maxU, maxV - cornerV),

                // Center
                new TexturedQuad(x + scaledCorner, y + scaledCorner, width - scaledCorner * 2, height - scaledCorner * 2, minU + cornerU, minV + cornerV, maxU - cornerU, maxV - cornerV)
        );
    }

    public static List<TexturedQuad> repeating(UvRect uv, int x, int y, int width, int height, int scale) {
        float minU = uv.minU();
        float minV = uv.minV();

        float scaleFactor = scale / 100.0f;
        int scaledTexW = Math.max(1, (int) (GRID_SIZE * scaleFactor));
        int scaledTexH = Math.max(1, (int) (GRID_SIZE * scaleFactor));

        float spanU = uv.maxU() - minU;
        float spanV = uv.maxV() - minV;

        List<TexturedQuad> quads = new ArrayList<>();
        for (int i = 0; i < width; i += scaledTexW) {
            for (int j = 0; j < height; j += scaledTexH) {
                int drawWidth = Math.min(scaledTexW, width - i);
                int drawHeight = Math.min(scaledTexH, height - j);

                float partialMaxU = minU + spanU * ((float) drawWidth / scaledTexW);
                float partialMaxV = minV + spanV * ((float) drawHeight / scaledTexH);

                quads.add(new TexturedQuad(x + i, y + j, drawWidth, drawHeight, minU, minV, partialMaxU, partialMaxV));
            }
        }
        return quads;
    }
}
