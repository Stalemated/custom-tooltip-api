package com.stalemated.customtooltips.fabric.mixin.compat.tierify;

import com.stalemated.customtooltips.ConfigManager;
import com.stalemated.customtooltips.core.dimensions.TooltipDimensionManager;
import draylar.tiered.api.BorderTemplate;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import elocindev.tierify.util.TieredTooltip;

import java.util.List;

@Pseudo
@Mixin(value = TieredTooltip.class, remap = false)
public class TieredTooltipMixin {

    @Inject(method = "renderTieredTooltipFromComponents", at = @At("HEAD"))
    private static void customtooltips$captureContext(DrawContext context, TextRenderer textRenderer, List<TooltipComponent> components, int x, int y, TooltipPositioner positioner, BorderTemplate borderTemplate, CallbackInfo ci) {
        TooltipDimensionManager.setState(context, textRenderer);
        TooltipDimensionManager.isCurrentTooltipItemTooltip = true;
    }

    @ModifyVariable(method = "renderTieredTooltipFromComponents", at = @At("HEAD"), index = 2, argsOnly = true)
    private static List<TooltipComponent> customtooltips$applyDimensions(List<TooltipComponent> components) {
        if (ConfigManager.getConfig().custom_tooltip_dimensions) {
            return TooltipDimensionManager.enforceHeightLimit(components);
        }
        return components;
    }

    @Inject(method = "renderTieredTooltipFromComponents", at = @At("TAIL"))
    private static void customtooltips$clearContext(CallbackInfo ci) {
        TooltipDimensionManager.clearState();
        TooltipDimensionManager.isCurrentTooltipItemTooltip = false;
    }
}
