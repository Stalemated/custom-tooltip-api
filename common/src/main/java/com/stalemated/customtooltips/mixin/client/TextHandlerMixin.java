package com.stalemated.customtooltips.mixin.client;

import com.stalemated.customtooltips.ConfigManager;
import com.stalemated.customtooltips.core.dimensions.TooltipDimensionManager;
import com.stalemated.customtooltips.util.TooltipTextUtil;
import net.minecraft.client.font.TextHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(TextHandler.class)
public abstract class TextHandlerMixin {

    @ModifyVariable(method = "wrapLines(Lnet/minecraft/text/StringVisitable;ILnet/minecraft/text/Style;)Ljava/util/List;", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private int customtooltips$disableAutoWrapVisitable(int maxWidth) {
        if (custom_tooltip_api$wrappingConditions()) {
            return Integer.MAX_VALUE;
        }
        return maxWidth;
    }

    @ModifyVariable(method = "wrapLines(Ljava/lang/String;ILnet/minecraft/text/Style;)Ljava/util/List;", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private int customtooltips$disableAutoWrapString(int maxWidth) {
        if (custom_tooltip_api$wrappingConditions()) {
            return Integer.MAX_VALUE;
        }
        return maxWidth;
    }

    @Unique
    private boolean custom_tooltip_api$wrappingConditions() {
        return TooltipDimensionManager.isCurrentTooltipItemTooltip && ConfigManager.getConfig().custom_tooltip_dimensions && !TooltipTextUtil.isHandlingCustomWrap;
    }
}
