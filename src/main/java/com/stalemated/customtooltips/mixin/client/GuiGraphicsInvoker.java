package com.stalemated.customtooltips.mixin.client;

//? if >=26.1 {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
*///?} else {
import net.minecraft.client.gui.GuiGraphics;
//?}
import org.spongepowered.asm.mixin.Mixin;
//? if >=26.3 {
/*import com.mojang.renderpearl.api.pipeline.RenderPipeline;
*///?} elif >=1.21.6 {
/*import com.mojang.blaze3d.pipeline.RenderPipeline;
*///?}
//? if >=1.21.6 {
/*import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.gen.Invoker;
*///?}

//? if >=26.1 {
/*@Mixin(GuiGraphicsExtractor.class)
*///?} else {
@Mixin(GuiGraphics.class)
//?}
public interface GuiGraphicsInvoker {
    //? if >=1.21.6 {
    /*@Invoker("innerBlit")
    void customtooltips$innerBlit(RenderPipeline pipeline, ResourceLocation texture, int x0, int x1, int y0, int y1, float u0, float u1, float v0, float v1, int color);
    *///?}
}
