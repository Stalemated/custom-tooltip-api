package com.stalemated.customtooltips.mixin.client.compat.legendarytooltips;

import com.stalemated.customtooltips.core.CurrentTooltip;
import com.stalemated.customtooltips.core.TooltipColors;
import com.stalemated.lib.mixin.RequiresMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Pseudo
@RequiresMod("legendarytooltips")
@Mixin(targets = "com.anthonyhilyard.legendarytooltips.tooltip.TooltipDecor")
public class LegendaryTooltipsBorderMixin {
    @ModifyVariable(method = "setCurrentTooltipBorderStart", at = @At("HEAD"), argsOnly = true)
    private static int customtooltips$borderStart(int color) {
        return TooltipColors.borderStart(CurrentTooltip.look(), color);
    }
}
