package com.stalemated.customtooltips.mixin.client;

import com.stalemated.customtooltips.config.CustomTooltipsConfigRegistry;
import com.stalemated.customtooltips.config.TooltipConfig;
import com.stalemated.lib.mixin.RequiresMod;
import dev.isxander.yacl3.gui.controllers.string.StringControllerElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@RequiresMod("yet_another_config_lib_v3")
@Mixin(StringControllerElement.class)
public abstract class StringControllerElementMixin {

    @Shadow
    protected abstract boolean doSelectAll();

    @Unique
    private long custom_tooltip_api$lastClickedTime = 0L;

    @Inject(method = "mouseClicked", at = @At("RETURN"))
    private void customtooltips$onYaclMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {

        if (cir.getReturnValueZ()) {
            long time = System.currentTimeMillis();
            int timeBetweenClicksMs = 250;

            TooltipConfig config = CustomTooltipsConfigRegistry.CONFIG.get();

            if (config.enable_double_click_selection && (time - this.custom_tooltip_api$lastClickedTime) < timeBetweenClicksMs) {
                this.doSelectAll();
            }

            this.custom_tooltip_api$lastClickedTime = time;
        }
    }
}