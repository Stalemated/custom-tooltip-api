package com.stalemated.customtooltips.fabric.mixin.compat.tierify;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.core.TooltipBackgroundManager;
import com.stalemated.customtooltips.core.background.BackgroundRenderStrategy;
import com.stalemated.customtooltips.core.background.BackgroundStrategyFactory;
import com.stalemated.lib.mixin.RequiresMod;
import com.stalemated.lib.util.color.VanillaTooltipColors;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@RequiresMod("tiered")
@Mixin(targets = "elocindev.tierify.util.TieredTooltip")
public abstract class TieredTooltipMixin {

    @Shadow
    private static void renderBorder(GuiGraphics context, int x, int y, int width, int height, int z, int startColor, int endColor) {
        throw new AssertionError();
    }

    @Shadow
    private static void renderHorizontalLine(GuiGraphics context, int x, int y, int width, int z, int color) {
        throw new AssertionError();
    }

    @Shadow
    private static void renderVerticalLine(GuiGraphics context, int x, int y, int height, int z, int color) {
        throw new AssertionError();
    }

    @Inject(method = "renderTooltipBackground", at = @At("HEAD"), cancellable = true)
    private static void customtooltips$overrideTierifyBackground(GuiGraphics context, int x, int y, int width, int height, int z, int backgroundColor, int colorStart, int colorEnd, CallbackInfo ci) {
        
        TooltipEntry entry = TooltipBackgroundManager.getCurrentEntry();
        if (entry != null && entry.hasCustomBackground()) {
            int i = x - 3;
            int j = y - 3;
            int k = width + 6;
            int l = height + 6;

            int defaultBgColor = VanillaTooltipColors.BACKGROUND;
            BackgroundRenderStrategy strategy = BackgroundStrategyFactory.getStrategy(entry.backgroundType);

            renderHorizontalLine(context, i, j - 1, k, z, defaultBgColor);
            renderHorizontalLine(context, i, j + l, k, z, defaultBgColor);
            strategy.render(context, i, j, k, l, z, backgroundColor, entry);
            renderVerticalLine(context, i - 1, j, l, z, defaultBgColor);
            renderVerticalLine(context, i + k, j, l, z, defaultBgColor);

            int borderStart = TooltipBackgroundManager.getBorderColorStart(colorStart);
            int borderEnd = TooltipBackgroundManager.getBorderColorEnd(colorEnd);
            renderBorder(context, i, j + 1, k, l, z, borderStart, borderEnd);
            
            ci.cancel();
        }
    }

    @ModifyArg(method = "renderTooltipBackground", at = @At(value = "INVOKE", target = "Lelocindev/tierify/util/TieredTooltip;renderRectangle(Lnet/minecraft/client/gui/GuiGraphics;IIIIII)V"), index = 6)
    private static int customtooltips$modifyTierifyBgColor(int color) {
        return TooltipBackgroundManager.scaleBackgroundAlpha(color);
    }

    @ModifyArg(method = "renderTooltipBackground", at = @At(value = "INVOKE", target = "Lelocindev/tierify/util/TieredTooltip;renderHorizontalLine(Lnet/minecraft/client/gui/GuiGraphics;IIIII)V"), index = 5)
    private static int customtooltips$modifyTierifyBgOutlineH(int color) {
        return TooltipBackgroundManager.scaleBorderAlpha(color);
    }

    @ModifyArg(method = "renderTooltipBackground", at = @At(value = "INVOKE", target = "Lelocindev/tierify/util/TieredTooltip;renderVerticalLine(Lnet/minecraft/client/gui/GuiGraphics;IIIII)V"), index = 5)
    private static int customtooltips$modifyTierifyBgOutlineV(int color) {
        return TooltipBackgroundManager.scaleBorderAlpha(color);
    }

    @ModifyArg(method = "renderTooltipBackground", at = @At(value = "INVOKE", target = "Lelocindev/tierify/util/TieredTooltip;renderBorder(Lnet/minecraft/client/gui/GuiGraphics;IIIIIII)V"), index = 6)
    private static int customtooltips$modifyTierifyBorderStart(int colorStart) {
        return TooltipBackgroundManager.getBorderColorStart(colorStart);
    }

    @ModifyArg(method = "renderTooltipBackground", at = @At(value = "INVOKE", target = "Lelocindev/tierify/util/TieredTooltip;renderBorder(Lnet/minecraft/client/gui/GuiGraphics;IIIIIII)V"), index = 7)
    private static int customtooltips$modifyTierifyBorderEnd(int colorEnd) {
        return TooltipBackgroundManager.getBorderColorEnd(colorEnd);
    }

    @Inject(method = "renderTieredTooltipFromComponents", at = @At("TAIL"))
    private static void customtooltips$clearContext(CallbackInfo ci) {
        TooltipBackgroundManager.clearState();
    }
}
