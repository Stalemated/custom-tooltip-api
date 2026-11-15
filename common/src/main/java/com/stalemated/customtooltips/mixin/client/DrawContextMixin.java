package com.stalemated.customtooltips.mixin.client;

import com.stalemated.customtooltips.core.TooltipBackgroundManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.world.item.ItemStack;

@Mixin(GuiGraphics.class)
public abstract class DrawContextMixin {

    @Shadow
    protected abstract void renderTooltipInternal(Font textRenderer, List<ClientTooltipComponent> components, int x, int y, ClientTooltipPositioner positioner);

    @Inject(method = "renderTooltip(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V", at = @At("HEAD"))
    private void customtooltips$captureTooltipOpacity(Font textRenderer, ItemStack stack, int x, int y, CallbackInfo ci) {
        TooltipBackgroundManager.setMixinTooltipOpacity(stack);
    }

    @Inject(method = "renderTooltip(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V", at = @At("RETURN"))
    private void customtooltips$resetTooltipOpacity(Font textRenderer, ItemStack stack, int x, int y, CallbackInfo ci) {
        TooltipBackgroundManager.clearState();
    }

    @Inject(method = "renderTooltipInternal(Lnet/minecraft/client/gui/Font;Ljava/util/List;IILnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;)V", at = @At("TAIL"))
    private void customTooltips$clearStateAfterRender(CallbackInfo ci) {
        TooltipBackgroundManager.clearState();
    }
}