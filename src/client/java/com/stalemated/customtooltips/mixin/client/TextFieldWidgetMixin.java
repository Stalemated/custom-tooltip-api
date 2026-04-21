package com.stalemated.customtooltips.mixin.client;

import com.stalemated.customtooltips.config.TooltipConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TextFieldWidget.class)
public abstract class TextFieldWidgetMixin {

    @Unique
    private long lastClickTime = 0L;

    @Redirect(method = "onClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/TextFieldWidget;setCursor(I)V"))
    private void customtooltips$onSetCursorFromClick(TextFieldWidget instance, int cursor) {
        long time = System.currentTimeMillis();
        
        TooltipConfig config = AutoConfig.getConfigHolder(TooltipConfig.class).getConfig();

        if (config.enableDoubleClickSelection && time - this.lastClickTime < 250) {
            instance.setSelectionStart(0);
            instance.setSelectionEnd(instance.getText().length());
        } else {
            instance.setCursor(cursor);
        }

        this.lastClickTime = time;
    }
}