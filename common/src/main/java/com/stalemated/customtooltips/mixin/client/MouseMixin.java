package com.stalemated.customtooltips.mixin.client;

import com.stalemated.customtooltips.ConfigManager;
import com.stalemated.customtooltips.core.dimensions.TooltipScrollManager;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {
    @Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
    private void customtooltips$onMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        if (ConfigManager.getConfig().custom_tooltip_dimensions) {
            if (TooltipScrollManager.scroll(vertical) && ConfigManager.getConfig().enable_container_scrolling) {
                ci.cancel();
            }
        }
    }
}