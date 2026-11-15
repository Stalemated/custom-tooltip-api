package com.stalemated.customtooltips.core.background.helper;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import org.joml.Matrix4f;

public class AtlasRenderHelper {
    private static final int textureWidth = 64;
    private static final int textureHeight = 64;
    private static final int corner = 8;

    public static TextureAtlasSprite getSprite(String backgroundTexture) {
        ResourceLocation spriteId = new ResourceLocation(backgroundTexture.replace(".png", "").replace("textures/", ""));
        return Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS).getSprite(spriteId);
    }

    public static boolean isMissingSprite(TextureAtlasSprite sprite) {
        return sprite == null || sprite.contents().name().getPath().equals("missingno");
    }

    public static ResourceLocation getRawTextureId(String backgroundTexture) {
        return new ResourceLocation(backgroundTexture);
    }

    public static void drawNineSlice(GuiGraphics context, TextureAtlasSprite sprite, int x, int y, int width, int height, int scale) {
        float minU = sprite.getU0();
        float maxU = sprite.getU1();
        float minV = sprite.getV0();
        float maxV = sprite.getV1();

        drawNineSliceHelper(context, x, y, width, height, minU, maxU, minV, maxV, scale);
    }

    public static void drawNineSliceStandalone(GuiGraphics context, int x, int y, int width, int height, int scale) {
        float minU = 0.0f;
        float maxU = 1.0f;
        float minV = 0.0f;
        float maxV = 1.0f;

        drawNineSliceHelper(context, x, y, width, height, minU, maxU, minV, maxV, scale);
    }

    private static void drawNineSliceHelper(GuiGraphics context, int x, int y, int width, int height, float minU, float maxU, float minV, float maxV, int scale) {
        float scaleFactor = scale / 100.0f;
        int scaledCorner = Math.max(1, (int)(corner * scaleFactor));
        
        // Ensure corner does not exceed half the width/height to avoid rendering glitches
        scaledCorner = Math.min(scaledCorner, width / 2);
        scaledCorner = Math.min(scaledCorner, height / 2);

        float spanU = maxU - minU;
        float spanV = maxV - minV;

        float cornerU = (spanU / textureWidth) * corner;
        float cornerV = (spanV / textureHeight) * corner;

        Matrix4f matrix = context.pose().last().pose();
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

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

        BufferUploader.drawWithShader(buffer.end());
    }

    public static void drawSimple(GuiGraphics context, TextureAtlasSprite sprite, int x, int y, int width, int height) {
        Matrix4f matrix = context.pose().last().pose();
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        drawQuad(buffer, matrix, x, y, width, height, sprite.getU0(), sprite.getV0(), sprite.getU1(), sprite.getV1());

        BufferUploader.drawWithShader(buffer.end());
    }

    public static void drawSimpleStandalone(GuiGraphics context, int x, int y, int width, int height) {
        Matrix4f matrix = context.pose().last().pose();
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        drawQuad(buffer, matrix, x, y, width, height, 0.0f, 0.0f, 1.0f, 1.0f);

        BufferUploader.drawWithShader(buffer.end());
    }

    public static void drawRepeating(GuiGraphics context, TextureAtlasSprite sprite, int x, int y, int width, int height, int scale) {
        float minU = sprite.getU0();
        float maxU = sprite.getU1();
        float minV = sprite.getV0();
        float maxV = sprite.getV1();

        drawRepeatingHelper(context, x, y, width, height, minU, minV, maxU, maxV, scale);
    }

    public static void drawRepeatingStandalone(GuiGraphics context, int x, int y, int width, int height, int scale) {
        float minU = 0.0f;
        float maxU = 1.0f;
        float minV = 0.0f;
        float maxV = 1.0f;

        drawRepeatingHelper(context, x, y, width, height, minU, minV, maxU, maxV, scale);
    }

    private static void drawRepeatingHelper(GuiGraphics context, int x, int y, int width, int height, float minU, float minV,  float maxU, float maxV, int scale) {
        float scaleFactor = scale / 100.0f;
        int scaledTexW = Math.max(1, (int)(textureWidth * scaleFactor));
        int scaledTexH = Math.max(1, (int)(textureHeight * scaleFactor));

        float spanU = maxU - minU;
        float spanV = maxV - minV;

        Matrix4f matrix = context.pose().last().pose();
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        for (int i = 0; i < width; i += scaledTexW) {
            for (int j = 0; j < height; j += scaledTexH) {
                int drawWidth = Math.min(scaledTexW, width - i);
                int drawHeight = Math.min(scaledTexH, height - j);

                float partialMaxU = minU + spanU * ((float) drawWidth / scaledTexW);
                float partialMaxV = minV + spanV * ((float) drawHeight / scaledTexH);

                drawQuad(buffer, matrix, x + i, y + j, drawWidth, drawHeight, minU, minV, partialMaxU, partialMaxV);
            }
        }

        BufferUploader.drawWithShader(buffer.end());
    }

    private static void drawQuad(BufferBuilder buffer, Matrix4f matrix, int x, int y, int width, int height, float minU, float minV, float maxU, float maxV) {
        buffer.vertex(matrix, x, y, 0).uv(minU, minV).endVertex();
        buffer.vertex(matrix, x, y + height, 0).uv(minU, maxV).endVertex();
        buffer.vertex(matrix, x + width, y + height, 0).uv(maxU, maxV).endVertex();
        buffer.vertex(matrix, x + width, y, 0).uv(maxU, minV).endVertex();
    }
}
