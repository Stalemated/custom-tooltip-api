package com.stalemated.customtooltips.fabric.mixin.compat.tierify;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.stalemated.customtooltips.core.CurrentTooltip;
import com.stalemated.customtooltips.core.TooltipColors;
import com.stalemated.customtooltips.core.background.TooltipBackgroundPainter;
import com.stalemated.lib.mixin.RequiresMod;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@RequiresMod("tiered")
@Mixin(targets = "elocindev.tierify.util.TieredTooltip")
public abstract class TieredTooltipMixin {

    @WrapOperation(method = "renderTooltipBackground", at = @At(value = "INVOKE", target = "Lelocindev/tierify/util/TieredTooltip;renderRectangle(Lnet/minecraft/client/gui/GuiGraphics;IIIIII)V"))
    private static void customtooltips$paintBackground(GuiGraphics context, int x, int y, int width, int height, int z, int color, Operation<Void> original) {
        boolean painted = TooltipBackgroundPainter.paint(context, CurrentTooltip.entry(), x, y, width, height, z, color);
        if (!painted) original.call(context, x, y, width, height, z, color);
    }

    @ModifyArg(method = "renderTooltipBackground", at = @At(value = "INVOKE", target = "Lelocindev/tierify/util/TieredTooltip;renderHorizontalLine(Lnet/minecraft/client/gui/GuiGraphics;IIIII)V"), index = 5)
    private static int customtooltips$horizontalOutline(int color) {
        return TooltipColors.tierifyOutline(CurrentTooltip.entry(), color);
    }

    @ModifyArg(method = "renderTooltipBackground", at = @At(value = "INVOKE", target = "Lelocindev/tierify/util/TieredTooltip;renderVerticalLine(Lnet/minecraft/client/gui/GuiGraphics;IIIII)V"), index = 5)
    private static int customtooltips$verticalOutline(int color) {
        return TooltipColors.tierifyOutline(CurrentTooltip.entry(), color);
    }

    @ModifyArg(method = "renderTooltipBackground", at = @At(value = "INVOKE", target = "Lelocindev/tierify/util/TieredTooltip;renderBorder(Lnet/minecraft/client/gui/GuiGraphics;IIIIIII)V"), index = 6)
    private static int customtooltips$borderStart(int color) {
        return TooltipColors.borderStart(CurrentTooltip.entry(), color);
    }

    @ModifyArg(method = "renderTooltipBackground", at = @At(value = "INVOKE", target = "Lelocindev/tierify/util/TieredTooltip;renderBorder(Lnet/minecraft/client/gui/GuiGraphics;IIIIIII)V"), index = 7)
    private static int customtooltips$borderEnd(int color) {
        return TooltipColors.borderEnd(CurrentTooltip.entry(), color);
    }

    @Inject(method = "renderTieredTooltipFromComponents", at = @At("TAIL"))
    private static void customtooltips$clearContext(CallbackInfo ci) {
        CurrentTooltip.clear();
    }
}
