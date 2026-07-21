package com.stalemated.customtooltips.mixin.client.compat.legendarytooltips;

import com.anthonyhilyard.legendarytooltips.tooltip.TooltipDecor;
import com.stalemated.customtooltips.compat.CompatManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Pseudo
@Mixin(TooltipDecor.class)
public class LegendaryTooltipsBorderMixin {
    @ModifyVariable(method = "setCurrentTooltipBorderStart", at = @At("HEAD"), argsOnly = true, remap = false)
    private static int modifyBorderStart(int color) {
        return CompatManager.getBorderColorOverride(color);
    }
}
