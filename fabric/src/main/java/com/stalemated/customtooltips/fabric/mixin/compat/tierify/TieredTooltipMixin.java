package com.stalemated.customtooltips.fabric.mixin.compat.tierify;

import com.stalemated.customtooltips.ConfigManager;
import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.config.TooltipConfig;
import com.stalemated.customtooltips.core.TooltipBackgroundManager;
import com.stalemated.customtooltips.core.background.BackgroundRenderStrategy;
import com.stalemated.customtooltips.core.background.BackgroundStrategyFactory;
import com.stalemated.customtooltips.core.dimensions.TitleOverflowMode;
import com.stalemated.customtooltips.core.dimensions.TooltipDimensionManager;
import com.stalemated.customtooltips.core.dimensions.components.ScrollingTitleTooltipComponent;
import com.stalemated.customtooltips.fabric.compat.TierifyLegendaryBridge;
import net.fabricmc.loader.api.FabricLoader;
import draylar.tiered.api.BorderTemplate;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Pseudo
@Mixin(targets = "elocindev.tierify.util.TieredTooltip")
public abstract class TieredTooltipMixin {

    @Shadow
    private static void renderBorder(DrawContext context, int x, int y, int width, int height, int z, int startColor, int endColor) {
        throw new AssertionError();
    }

    @Shadow
    private static void renderHorizontalLine(DrawContext context, int x, int y, int width, int z, int color) {
        throw new AssertionError();
    }

    @Shadow
    private static void renderVerticalLine(DrawContext context, int x, int y, int height, int z, int color) {
        throw new AssertionError();
    }

    @Inject(method = "renderTieredTooltipFromComponents", at = @At("HEAD"))
    private static void customtooltips$captureContext(DrawContext context, TextRenderer textRenderer, List<TooltipComponent> components, int x, int y, TooltipPositioner positioner, BorderTemplate borderTemplate, CallbackInfo ci) {
        TooltipDimensionManager.setState(context, textRenderer);
        TooltipDimensionManager.isCurrentTooltipItemTooltip = true;
    }

    @ModifyVariable(method = "renderTieredTooltipFromComponents", at = @At("HEAD"), index = 2, argsOnly = true)
    private static List<TooltipComponent> customtooltips$applyDimensions(List<TooltipComponent> components) {
        List<TooltipComponent> processedList = components;
        TooltipConfig config = ConfigManager.getConfig();

        if (config.custom_tooltip_dimensions) {
            if (config.title_overflow_mode == TitleOverflowMode.SCROLL) ScrollingTitleTooltipComponent.isTierifyTooltip = true;
            processedList = TooltipDimensionManager.enforceHeightLimit(components);
        }

        if (FabricLoader.getInstance().isModLoaded("legendarytooltips")) {
            processedList = TierifyLegendaryBridge.wrapComponents(processedList);
        }

        return processedList;
    }

    @Inject(method = "renderTooltipBackground", at = @At("HEAD"), cancellable = true)
    private static void customtooltips$overrideTierifyBackground(DrawContext context, int x, int y, int width, int height, int z, int backgroundColor, int colorStart, int colorEnd, CallbackInfo ci) {
        if (FabricLoader.getInstance().isModLoaded("legendarytooltips")) {
            TierifyLegendaryBridge.setTooltipPosition(x, y, width);
        }
        
        TooltipEntry entry = TooltipBackgroundManager.getCurrentEntry();
        if (entry != null && entry.hasCustomBackground()) {
            int i = x - 3;
            int j = y - 3;
            int k = width + 6;
            int l = height + 6;

            int defaultBgColor = TooltipEntry.DEFAULT_BACKGROUND_COLORS.get(0);
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

    @ModifyArg(method = "renderTooltipBackground", at = @At(value = "INVOKE", target = "Lelocindev/tierify/util/TieredTooltip;renderRectangle(Lnet/minecraft/client/gui/DrawContext;IIIIII)V"), index = 6)
    private static int customtooltips$modifyTierifyBgColor(int color) {
        return TooltipBackgroundManager.scaleBackgroundAlpha(color);
    }

    @ModifyArg(method = "renderTooltipBackground", at = @At(value = "INVOKE", target = "Lelocindev/tierify/util/TieredTooltip;renderHorizontalLine(Lnet/minecraft/client/gui/DrawContext;IIIII)V"), index = 5)
    private static int customtooltips$modifyTierifyBgOutlineH(int color) {
        return TooltipBackgroundManager.scaleBorderAlpha(color);
    }

    @ModifyArg(method = "renderTooltipBackground", at = @At(value = "INVOKE", target = "Lelocindev/tierify/util/TieredTooltip;renderVerticalLine(Lnet/minecraft/client/gui/DrawContext;IIIII)V"), index = 5)
    private static int customtooltips$modifyTierifyBgOutlineV(int color) {
        return TooltipBackgroundManager.scaleBorderAlpha(color);
    }

    @ModifyArg(method = "renderTooltipBackground", at = @At(value = "INVOKE", target = "Lelocindev/tierify/util/TieredTooltip;renderBorder(Lnet/minecraft/client/gui/DrawContext;IIIIIII)V"), index = 6)
    private static int customtooltips$modifyTierifyBorderStart(int colorStart) {
        return TooltipBackgroundManager.getBorderColorStart(colorStart);
    }

    @ModifyArg(method = "renderTooltipBackground", at = @At(value = "INVOKE", target = "Lelocindev/tierify/util/TieredTooltip;renderBorder(Lnet/minecraft/client/gui/DrawContext;IIIIIII)V"), index = 7)
    private static int customtooltips$modifyTierifyBorderEnd(int colorEnd) {
        return TooltipBackgroundManager.getBorderColorEnd(colorEnd);
    }

    @Inject(method = "renderTieredTooltipFromComponents", at = @At("TAIL"))
    private static void customtooltips$clearContext(DrawContext context, TextRenderer textRenderer, List<TooltipComponent> components, int x, int y, TooltipPositioner positioner, BorderTemplate borderTemplate, CallbackInfo ci) {
        if (FabricLoader.getInstance().isModLoaded("legendarytooltips")) {
            TierifyLegendaryBridge.drawSeparator(context, components);
        }
        if (ConfigManager.getConfig().title_overflow_mode == TitleOverflowMode.SCROLL) ScrollingTitleTooltipComponent.isTierifyTooltip = false;

        
        TooltipBackgroundManager.clearState();
        TooltipDimensionManager.clearState();
    }
}
