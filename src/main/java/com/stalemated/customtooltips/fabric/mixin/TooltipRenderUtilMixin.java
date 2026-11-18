package com.stalemated.customtooltips.fabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.stalemated.customtooltips.core.CurrentTooltip;
import com.stalemated.customtooltips.core.TooltipColors;
import com.stalemated.customtooltips.core.background.TooltipBackgroundPainter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(TooltipRenderUtil.class)
public abstract class TooltipRenderUtilMixin {
    @WrapOperation(method = "renderTooltipBackground",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/TooltipRenderUtil;renderRectangle(Lnet/minecraft/client/gui/GuiGraphics;IIIIII)V"))
    private static void customtooltips$paintBackground(GuiGraphics graphics, int x, int y, int width, int height, int z, int color, Operation<Void> original) {
        boolean painted = TooltipBackgroundPainter.paint(graphics, CurrentTooltip.look(), x, y, width, height, z, color);
        if (!painted) original.call(graphics, x, y, width, height, z, color);
    }

    @ModifyArg(method = "renderTooltipBackground",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/TooltipRenderUtil;renderHorizontalLine(Lnet/minecraft/client/gui/GuiGraphics;IIIII)V"), index = 5)
    private static int customtooltips$horizontalOutline(int color) {
        return TooltipColors.outline(CurrentTooltip.look(), color);
    }

    @ModifyArg(method = "renderTooltipBackground",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/TooltipRenderUtil;renderVerticalLine(Lnet/minecraft/client/gui/GuiGraphics;IIIII)V"), index = 5)
    private static int customtooltips$verticalOutline(int color) {
        return TooltipColors.outline(CurrentTooltip.look(), color);
    }

    @ModifyArg(method = "renderTooltipBackground",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/TooltipRenderUtil;renderFrameGradient(Lnet/minecraft/client/gui/GuiGraphics;IIIIIII)V"), index = 6)
    private static int customtooltips$borderStart(int color) {
        return TooltipColors.borderStart(CurrentTooltip.look(), color);
    }

    @ModifyArg(method = "renderTooltipBackground",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/TooltipRenderUtil;renderFrameGradient(Lnet/minecraft/client/gui/GuiGraphics;IIIIIII)V"), index = 7)
    private static int customtooltips$borderEnd(int color) {
        return TooltipColors.borderEnd(CurrentTooltip.look(), color);
    }
}
