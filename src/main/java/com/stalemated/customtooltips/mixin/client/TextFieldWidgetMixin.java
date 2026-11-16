package com.stalemated.customtooltips.mixin.client;

import com.stalemated.customtooltips.config.CustomTooltipsConfigRegistry;
import com.stalemated.customtooltips.config.TooltipConfig;
import net.minecraft.client.gui.components.EditBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EditBox.class)
public abstract class TextFieldWidgetMixin {

    @Unique
    private long custom_tooltip_api$lastClickTime = 0L;

    @Redirect(method = "onClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/EditBox;moveCursorTo(I)V"))
    private void customtooltips$onSetCursorFromClick(EditBox instance, int cursor) {
        long time = System.currentTimeMillis();
        int timeBetweenClicksMs = 250;
        
        TooltipConfig config = CustomTooltipsConfigRegistry.CONFIG.get();

        if (config.enable_double_click_selection && time - this.custom_tooltip_api$lastClickTime < timeBetweenClicksMs) {
            instance.setCursorPosition(0);
            instance.setHighlightPos(instance.getValue().length());
        } else {
            instance.moveCursorTo(cursor);
        }

        this.custom_tooltip_api$lastClickTime = time;
    }
}