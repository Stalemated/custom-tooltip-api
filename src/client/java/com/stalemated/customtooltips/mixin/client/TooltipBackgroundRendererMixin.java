package com.stalemated.customtooltips.mixin.client;

import com.stalemated.customtooltips.core.TooltipBackgroundManager;
import com.stalemated.customtooltips.core.background.BackgroundRenderStrategy;
import com.stalemated.customtooltips.core.background.BackgroundStrategyFactory;
import com.stalemated.customtooltips.TooltipEntry;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipBackgroundRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TooltipBackgroundRenderer.class)
public abstract class TooltipBackgroundRendererMixin {
    @Redirect(method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/tooltip/TooltipBackgroundRenderer;renderRectangle(Lnet/minecraft/client/gui/DrawContext;IIIIII)V"))
    private static void customTooltips$renderCustomBackground(DrawContext context, int x, int y, int width, int height, int z, int color) {
        TooltipEntry entry = TooltipBackgroundManager.getCurrentEntry();

        if (entry != null && entry.hasCustomBackground()) {
            BackgroundRenderStrategy strategy = BackgroundStrategyFactory.getStrategy(entry.backgroundType);
            strategy.render(context, x, y, width, height, z, color, entry);
        } else {
            context.fill(x, y, x + width, y + height, z, TooltipBackgroundManager.scaleBackgroundAlpha(color));
        }
    }

    @ModifyArg(method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/tooltip/TooltipBackgroundRenderer;renderHorizontalLine(Lnet/minecraft/client/gui/DrawContext;IIIII)V"), index = 5)
    private static int customTooltips$renderHorizontalLine(int color) {
        return TooltipBackgroundManager.scaleBorderAlpha(color);
    }

    @ModifyArg(method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/tooltip/TooltipBackgroundRenderer;renderVerticalLine(Lnet/minecraft/client/gui/DrawContext;IIIII)V"), index = 5)
    private static int customTooltips$renderVerticalLineStart(int color) {
        return TooltipBackgroundManager.scaleBorderAlpha(color);
    }

    @ModifyArg(method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/tooltip/TooltipBackgroundRenderer;renderBorder(Lnet/minecraft/client/gui/DrawContext;IIIIIII)V"), index = 6)
    private static int customTooltips$renderBorderStartColor(int color) {
        return TooltipBackgroundManager.getBorderColorStart(color);
    }

    @ModifyArg(method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/tooltip/TooltipBackgroundRenderer;renderBorder(Lnet/minecraft/client/gui/DrawContext;IIIIIII)V"), index = 7)
    private static int customTooltips$renderBorderEndColor(int color) {
        return TooltipBackgroundManager.getBorderColorEnd(color);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private static void customTooltips$clearStateAfterRender(DrawContext context, int x, int y, int width, int height, int z, CallbackInfo ci) {
        TooltipBackgroundManager.clearState();
    }
}
