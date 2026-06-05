package com.stalemated.customtooltips.mixin.client;

import com.stalemated.customtooltips.ConfigManager;
import com.stalemated.customtooltips.core.TooltipBackgroundManager;
import com.stalemated.customtooltips.core.dimensions.TooltipDimensionManager;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DrawContext.class)
public abstract class DrawContextMixin {

    @Shadow
    protected abstract void drawTooltip(TextRenderer textRenderer, List<TooltipComponent> components, int x, int y, TooltipPositioner positioner);

    @Inject(method = "drawItemTooltip", at = @At("HEAD"))
    private void customtooltips$captureTooltipOpacity(TextRenderer textRenderer, ItemStack stack, int x, int y, CallbackInfo ci) {
        TooltipBackgroundManager.setMixinTooltipOpacity(stack);
    }

    @Inject(method = "drawItemTooltip", at = @At("RETURN"))
    private void customtooltips$resetTooltipOpacity(TextRenderer textRenderer, ItemStack stack, int x, int y, CallbackInfo ci) {
        TooltipBackgroundManager.clearState();
    }

    @Inject(method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;)V", at = @At("HEAD"))
    private void customtooltips$captureDrawContext(TextRenderer textRenderer, List<TooltipComponent> components, int x, int y, TooltipPositioner positioner, CallbackInfo ci) {
        TooltipDimensionManager.setState((DrawContext) (Object) this, textRenderer);
    }

    @ModifyVariable(method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;Ljava/util/Optional;II)V", at = @At("HEAD"), argsOnly = true, index = 2)
    private List<Text> customtooltips$applyDimensionsWidth(List<Text> text) {
        return TooltipDimensionManager.enforceWidthLimit(text);
    }

    @ModifyVariable(method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;)V", at = @At("HEAD"), argsOnly = true, index = 2)
    private List<TooltipComponent> customtooltips$applyDimensionsHeight(List<TooltipComponent> components) {
        if (ConfigManager.getConfig().custom_tooltip_dimensions && TooltipDimensionManager.isCurrentTooltipItemTooltip) {
            return TooltipDimensionManager.enforceHeightLimit(components);
        }
        return components;
    }

    @Inject(method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;)V", at = @At("TAIL"))
    private void customTooltips$clearStateAfterRender(CallbackInfo ci) {
        TooltipBackgroundManager.clearState();
        TooltipDimensionManager.clearState();
        TooltipDimensionManager.isCurrentTooltipItemTooltip = false;
    }
}