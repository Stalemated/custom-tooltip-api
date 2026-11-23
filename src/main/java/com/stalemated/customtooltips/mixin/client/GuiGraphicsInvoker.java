package com.stalemated.customtooltips.mixin.client;

import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
//? if >=1.21.6 {
/*import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.gen.Invoker;
*///?}

@Mixin(GuiGraphics.class)
public interface GuiGraphicsInvoker {
    //? if >=1.21.6 {
    /*@Invoker("innerBlit")
    void customtooltips$innerBlit(RenderPipeline pipeline, ResourceLocation texture, int x0, int x1, int y0, int y1, float u0, float u1, float v0, float v1, int color);
    *///?}
}
