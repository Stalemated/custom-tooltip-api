package com.stalemated.customtooltips.mixin.client;

import com.stalemated.customtooltips.core.TooltipOpacity;
import net.minecraft.client.gui.tooltip.TooltipBackgroundRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(TooltipBackgroundRenderer.class)
public abstract class TooltipBackgroundRendererMixin {
    @ModifyArg(method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/tooltip/TooltipBackgroundRenderer;renderRectangle(Lnet/minecraft/client/gui/DrawContext;IIIIII)V"), index = 6)
    private static int customTooltips$renderRectangle(int color) {
        return TooltipOpacity.scaleAlpha(color);
    }

    @ModifyArg(method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/tooltip/TooltipBackgroundRenderer;renderHorizontalLine(Lnet/minecraft/client/gui/DrawContext;IIIII)V"), index = 5)
    private static int customTooltips$renderHorizontalLine(int color) {
        return TooltipOpacity.scaleAlpha(color);
    }

    @ModifyArg(method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/tooltip/TooltipBackgroundRenderer;renderVerticalLine(Lnet/minecraft/client/gui/DrawContext;IIIII)V"), index = 5)
    private static int customTooltips$renderVerticalLineStart(int color) {
        return TooltipOpacity.scaleAlpha(color);
    }

    @ModifyArg(method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/tooltip/TooltipBackgroundRenderer;renderBorder(Lnet/minecraft/client/gui/DrawContext;IIIIIII)V"), index = 6)
    private static int customTooltips$renderBorderStartColor(int color) {
        return TooltipOpacity.getBorderColorStart(color);
    }

    @ModifyArg(method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/tooltip/TooltipBackgroundRenderer;renderBorder(Lnet/minecraft/client/gui/DrawContext;IIIIIII)V"), index = 7)
    private static int customTooltips$renderBorderEndColor(int color) {
        return TooltipOpacity.getBorderColorEnd(color);
    }
}
