package com.stalemated.customtooltips.mixin.client;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.core.TooltipOpacity;
import net.minecraft.client.gui.tooltip.TooltipBackgroundRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(TooltipBackgroundRenderer.class)
public abstract class TooltipBackgroundRendererMixin {
    @ModifyArg(method = "render(Lnet/minecraft/client/gui/DrawContext;IIIII)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/tooltip/TooltipBackgroundRenderer;renderRectangle(Lnet/minecraft/client/gui/DrawContext;IIIIII)V"), index = 6)
    private static int customTooltips$renderRectangle(int color) {
        int value = TooltipOpacity.getCurrentOpacity() != -1
                ? TooltipOpacity.getCurrentOpacity()
                : TooltipEntry.DEFAULT_OPACITY;

        int originalAlpha = (color >> 24) & 0xFF;
        int newAlpha = Math.min(255, (int) (originalAlpha * (value / 240.0f)));

        return (color & 0x00FFFFFF) | (newAlpha << 24);
    }
}
