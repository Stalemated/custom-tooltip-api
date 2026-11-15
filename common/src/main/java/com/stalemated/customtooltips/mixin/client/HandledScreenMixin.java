package com.stalemated.customtooltips.mixin.client;

import com.stalemated.customtooltips.core.TooltipBackgroundManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public abstract class HandledScreenMixin {

    @Shadow @Nullable protected Slot hoveredSlot;

    @Inject(method = "renderTooltip", at = @At("HEAD"))
    private void customtooltips$captureInventoryTooltipOpacity(GuiGraphics context, int x, int y, CallbackInfo ci) {
        if (this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
            ItemStack stack = this.hoveredSlot.getItem();
            TooltipBackgroundManager.setMixinTooltipOpacity(stack);
        }
    }

    @Inject(method = "renderTooltip", at = @At("RETURN"))
    private void customtooltips$resetInventoryTooltipOpacity(GuiGraphics context, int x, int y, CallbackInfo ci) {
        TooltipBackgroundManager.clearState();
    }
}