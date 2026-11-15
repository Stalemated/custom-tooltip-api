package com.stalemated.customtooltips.fabric.mixin;

import com.stalemated.customtooltips.core.TooltipBackgroundManager;
import com.stalemated.customtooltips.core.background.BackgroundRenderStrategy;
import com.stalemated.customtooltips.core.background.BackgroundStrategyFactory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import com.stalemated.customtooltips.TooltipEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TooltipRenderUtil.class)
public abstract class TooltipBackgroundRendererMixin {
    @Redirect(method = "renderTooltipBackground",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/TooltipRenderUtil;renderRectangle(Lnet/minecraft/client/gui/GuiGraphics;IIIIII)V"))
    private static void customTooltips$renderCustomBackground(GuiGraphics context, int x, int y, int width, int height, int z, int color) {
        TooltipEntry entry = TooltipBackgroundManager.getCurrentEntry();

        if (entry != null && entry.hasCustomBackground()) {
            BackgroundRenderStrategy strategy = BackgroundStrategyFactory.getStrategy(entry.backgroundType);
            strategy.render(context, x, y, width, height, z, color, entry);
        } else {
            context.fill(x, y, x + width, y + height, z, TooltipBackgroundManager.scaleBackgroundAlpha(color));
        }
    }

    @ModifyArg(method = "renderTooltipBackground",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/TooltipRenderUtil;renderHorizontalLine(Lnet/minecraft/client/gui/GuiGraphics;IIIII)V"), index = 5)
    private static int customTooltips$renderHorizontalLine(int color) {
        return TooltipBackgroundManager.scaleBorderAlpha(color);
    }

    @ModifyArg(method = "renderTooltipBackground",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/TooltipRenderUtil;renderVerticalLine(Lnet/minecraft/client/gui/GuiGraphics;IIIII)V"), index = 5)
    private static int customTooltips$renderVerticalLineStart(int color) {
        return TooltipBackgroundManager.scaleBorderAlpha(color);
    }

    @ModifyArg(method = "renderTooltipBackground",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/TooltipRenderUtil;renderFrameGradient(Lnet/minecraft/client/gui/GuiGraphics;IIIIIII)V"), index = 6)
    private static int customTooltips$renderBorderStartColor(int color) {
        return TooltipBackgroundManager.getBorderColorStart(color);
    }

    @ModifyArg(method = "renderTooltipBackground",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/TooltipRenderUtil;renderFrameGradient(Lnet/minecraft/client/gui/GuiGraphics;IIIIIII)V"), index = 7)
    private static int customTooltips$renderBorderEndColor(int color) {
        return TooltipBackgroundManager.getBorderColorEnd(color);
    }
}
