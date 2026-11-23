package com.stalemated.customtooltips.core.background;

import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
//? if >=1.21.6 {
/*import com.stalemated.customtooltips.mixin.client.GuiGraphicsInvoker;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.ARGB;
*///?} elif >=1.21.2 {
/*import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ARGB;
import org.joml.Matrix4f;
*///?} else {
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;
//?}

final class BackgroundTextureDrawer {
    private BackgroundTextureDrawer() {
    }

    //? if >=1.21.6 {
    /*static void draw(GuiGraphics graphics, ResourceLocation texture, float opacity, List<TexturedQuad> quads) {
        GuiGraphicsInvoker blitter = (GuiGraphicsInvoker) graphics;
        int color = ARGB.white(opacity);
        for (TexturedQuad quad : quads)
            blitter.customtooltips$innerBlit(RenderPipelines.GUI_TEXTURED, texture, quad.x(), quad.right(), quad.y(), quad.bottom(), quad.minU(), quad.maxU(), quad.minV(), quad.maxV(), color);
    }
    *///?} elif >=1.21.2 {
    /*static void draw(GuiGraphics graphics, ResourceLocation texture, float opacity, List<TexturedQuad> quads) {
        Matrix4f matrix = graphics.pose().last().pose();
        int color = ARGB.white(opacity);
        graphics.drawSpecial(buffers -> {
            VertexConsumer consumer = buffers.getBuffer(RenderType.guiTextured(texture));
            for (TexturedQuad quad : quads) {
                consumer.addVertex(matrix, quad.x(), quad.y(), 0).setUv(quad.minU(), quad.minV()).setColor(color);
                consumer.addVertex(matrix, quad.x(), quad.bottom(), 0).setUv(quad.minU(), quad.maxV()).setColor(color);
                consumer.addVertex(matrix, quad.right(), quad.bottom(), 0).setUv(quad.maxU(), quad.maxV()).setColor(color);
                consumer.addVertex(matrix, quad.right(), quad.y(), 0).setUv(quad.maxU(), quad.minV()).setColor(color);
            }
        });
    }
    *///?} else {
    static void draw(GuiGraphics graphics, ResourceLocation texture, float opacity, List<TexturedQuad> quads) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, opacity);
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        upload(graphics.pose().last().pose(), quads);

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
    }
    //?}

    //? if >=1.21 <1.21.2 {
    /*private static void upload(Matrix4f matrix, List<TexturedQuad> quads) {
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        for (TexturedQuad quad : quads) {
            buffer.addVertex(matrix, quad.x(), quad.y(), 0).setUv(quad.minU(), quad.minV());
            buffer.addVertex(matrix, quad.x(), quad.bottom(), 0).setUv(quad.minU(), quad.maxV());
            buffer.addVertex(matrix, quad.right(), quad.bottom(), 0).setUv(quad.maxU(), quad.maxV());
            buffer.addVertex(matrix, quad.right(), quad.y(), 0).setUv(quad.maxU(), quad.minV());
        }
        BufferUploader.drawWithShader(buffer.buildOrThrow());
    }
    *///?} elif <1.21 {
    private static void upload(Matrix4f matrix, List<TexturedQuad> quads) {
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        for (TexturedQuad quad : quads) {
            buffer.vertex(matrix, quad.x(), quad.y(), 0).uv(quad.minU(), quad.minV()).endVertex();
            buffer.vertex(matrix, quad.x(), quad.bottom(), 0).uv(quad.minU(), quad.maxV()).endVertex();
            buffer.vertex(matrix, quad.right(), quad.bottom(), 0).uv(quad.maxU(), quad.maxV()).endVertex();
            buffer.vertex(matrix, quad.right(), quad.y(), 0).uv(quad.maxU(), quad.minV()).endVertex();
        }
        BufferUploader.drawWithShader(buffer.end());
    }
    //?}
}
