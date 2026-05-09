package com.stalemated.customtooltips.mixin.client;

import com.stalemated.customtooltips.core.TooltipOpacity;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin {

    @Shadow @Nullable protected Slot focusedSlot;

    @Inject(method = "drawMouseoverTooltip", at = @At("HEAD"))
    private void customtooltips$captureInventoryTooltipOpacity(DrawContext context, int x, int y, CallbackInfo ci) {
        if (this.focusedSlot != null && this.focusedSlot.hasStack()) {
            ItemStack stack = this.focusedSlot.getStack();
            TooltipOpacity.setMixinTooltipOpacity(stack);
        }
    }

    @Inject(method = "drawMouseoverTooltip", at = @At("RETURN"))
    private void customtooltips$resetInventoryTooltipOpacity(DrawContext context, int x, int y, CallbackInfo ci) {
        TooltipOpacity.setCurrentOpacity(-1);
    }
}