package com.stalemated.customtooltips.mixin.client;

import com.stalemated.customtooltips.config.ConfigManager;
import com.stalemated.customtooltips.config.TooltipConfig;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TextFieldWidget.class)
public abstract class TextFieldWidgetMixin {

    @Unique
    private long custom_tooltip_api$lastClickTime = 0L;

    @Redirect(method = "onClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/TextFieldWidget;setCursor(I)V"))
    private void customtooltips$onSetCursorFromClick(TextFieldWidget instance, int cursor) {
        long time = System.currentTimeMillis();
        int timeBetweenClicksMs = 250;
        
        TooltipConfig config = ConfigManager.getConfig();

        if (config.enable_double_click_selection && time - this.custom_tooltip_api$lastClickTime < timeBetweenClicksMs) {
            instance.setSelectionStart(0);
            instance.setSelectionEnd(instance.getText().length());
        } else {
            instance.setCursor(cursor);
        }

        this.custom_tooltip_api$lastClickTime = time;
    }
}