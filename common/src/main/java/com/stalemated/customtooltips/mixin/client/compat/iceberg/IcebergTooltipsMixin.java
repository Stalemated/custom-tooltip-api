package com.stalemated.customtooltips.mixin.client.compat.iceberg;

import com.stalemated.customtooltips.core.dimensions.overflow.strategies.ScrollingTitleTooltipComponent;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Pseudo
@Mixin(targets = "com.anthonyhilyard.iceberg.util.Tooltips")
public class IcebergTooltipsMixin {

    @Inject(method = "centerTitle(Ljava/util/List;Lnet/minecraft/client/font/TextRenderer;I)Ljava/util/List;", at = @At("HEAD"), cancellable = true)
    private static void onCenterTitle(List<TooltipComponent> components, TextRenderer font, int width, CallbackInfoReturnable<List<TooltipComponent>> cir) {
        for (TooltipComponent comp : components) {
            if (comp instanceof ScrollingTitleTooltipComponent) {
                cir.setReturnValue(new ArrayList<>(components));
                return;
            }
        }
    }

    @Inject(method = "centerTitle(Ljava/util/List;Lnet/minecraft/client/font/TextRenderer;II)Ljava/util/List;", at = @At("HEAD"), cancellable = true)
    private static void onCenterTitleWithLines(List<TooltipComponent> components, TextRenderer font, int width, int titleLines, CallbackInfoReturnable<List<TooltipComponent>> cir) {
        for (TooltipComponent comp : components) {
            if (comp instanceof ScrollingTitleTooltipComponent) {
                cir.setReturnValue(new ArrayList<>(components));
                return;
            }
        }
    }
}
