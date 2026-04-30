package com.stalemated.customtooltips.mixin.client;

import com.stalemated.customtooltips.ConfigManager;
import com.stalemated.customtooltips.config.TooltipConfig;
import dev.isxander.yacl3.gui.controllers.string.StringControllerElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StringControllerElement.class)
public abstract class StringControllerElementMixin {

    @Shadow(remap = false) 
    protected abstract boolean doSelectAll();

    @Unique
    private long lastClickedTime = 0L;

    @Inject(method = "mouseClicked", at = @At("RETURN"))
    private void customtooltips$onYaclMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {

        if (cir.getReturnValueZ()) {
            long time = System.currentTimeMillis();
            int timeBetweenClicksMs = 250;

            TooltipConfig config = ConfigManager.getConfig();

            if (config != null && config.enable_double_click_selection && (time - this.lastClickedTime) < timeBetweenClicksMs) {
                this.doSelectAll();
            }

            this.lastClickedTime = time;
        }
    }
}