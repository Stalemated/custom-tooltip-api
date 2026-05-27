package com.stalemated.customtooltips.mixin.client;

import com.stalemated.customtooltips.core.TooltipBackgroundManager;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrawContext.class)
public abstract class DrawContextMixin {

    @Inject(method = "drawItemTooltip", at = @At("HEAD"))
    private void customtooltips$captureTooltipOpacity(TextRenderer textRenderer, ItemStack stack, int x, int y, CallbackInfo ci) {
        TooltipBackgroundManager.setMixinTooltipOpacity(stack);
    }

    @Inject(method = "drawItemTooltip", at = @At("RETURN"))
    private void customtooltips$resetTooltipOpacity(TextRenderer textRenderer, ItemStack stack, int x, int y, CallbackInfo ci) {
        TooltipBackgroundManager.clearState();
    }

    @Inject(method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;)V", at = @At("TAIL"))
    private void customTooltips$clearStateAfterRender(CallbackInfo ci) {
        TooltipBackgroundManager.clearState();
    }
}