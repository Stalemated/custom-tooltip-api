 package com.stalemated.customtooltips.mixin.client;

import com.stalemated.customtooltips.core.CurrentTooltip;
import com.stalemated.customtooltips.core.TooltipPalette;
import com.stalemated.customtooltips.core.background.TooltipBackgroundPainter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import org.spongepowered.asm.mixin.Mixin;
//? if fabric {
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
//?} else {
/*import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
*///?}

@Mixin(TooltipRenderUtil.class)
public abstract class TooltipRenderUtilMixin {

    //? if fabric {
    @WrapMethod(method = "renderTooltipBackground(Lnet/minecraft/client/gui/GuiGraphics;IIIII)V")
    private static void customtooltips$paintBackground(GuiGraphics graphics, int x, int y, int width, int height, int z, Operation<Void> original) {
        boolean painted = TooltipBackgroundPainter.paint(graphics, CurrentTooltip.look(), x, y, width, height, z, TooltipPalette.VANILLA);
        if (!painted) original.call(graphics, x, y, width, height, z);
    }
    //?} else {
    /*@Inject(method = "renderTooltipBackground(Lnet/minecraft/client/gui/GuiGraphics;IIIIIIIII)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/TooltipRenderUtil;renderHorizontalLine(Lnet/minecraft/client/gui/GuiGraphics;IIIII)V", ordinal = 0),
            cancellable = true)
    private static void customtooltips$paintBackground(GuiGraphics graphics, int x, int y, int width, int height, int z,
                                                       int backgroundTop, int backgroundBottom, int borderTop, int borderBottom, CallbackInfo ci) {
        TooltipPalette eventColors = new TooltipPalette(backgroundTop, backgroundBottom, borderTop, borderBottom);
        if (TooltipBackgroundPainter.paint(graphics, CurrentTooltip.look(), x, y, width, height, z, eventColors)) ci.cancel();
    }
    *///?}
}
