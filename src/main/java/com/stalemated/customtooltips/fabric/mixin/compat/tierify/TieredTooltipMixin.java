package com.stalemated.customtooltips.fabric.mixin.compat.tierify;

import com.stalemated.lib.mixin.RequiresMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
//? if <1.21.6 {
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.stalemated.customtooltips.core.CurrentTooltip;
import com.stalemated.customtooltips.core.TooltipPalette;
import com.stalemated.customtooltips.core.background.TooltipBackgroundPainter;
import net.minecraft.client.gui.GuiGraphics;
//?}

@Pseudo
@RequiresMod("tiered")
@Mixin(targets = "elocindev.tierify.util.TieredTooltip")
public abstract class TieredTooltipMixin {

    //? if <1.21.6 {
    @WrapMethod(method = "renderTooltipBackground(Lnet/minecraft/client/gui/GuiGraphics;IIIIIIII)V")
    private static void customtooltips$paintBackground(
            GuiGraphics context, int x, int y, int width, int height, int z,
            int backgroundColor, int colorStart, int colorEnd, Operation<Void> original
    ) {
        TooltipPalette tierColors = new TooltipPalette(backgroundColor, backgroundColor, colorStart, colorEnd);
        boolean painted = TooltipBackgroundPainter.paintAtDepth(context, CurrentTooltip.look(), x, y, width, height, z, tierColors);
        if (!painted) original.call(context, x, y, width, height, z, backgroundColor, colorStart, colorEnd);
    }
    //?}
}
