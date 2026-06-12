package com.stalemated.customtooltips.core.dimensions;

import com.stalemated.customtooltips.ConfigManager;
import com.stalemated.customtooltips.compat.LegendaryTooltipsCompat;
import com.stalemated.customtooltips.config.TooltipConfig;
import com.stalemated.customtooltips.core.dimensions.overflow.TitleOverflowStrategyFactory;
import com.stalemated.customtooltips.core.dimensions.components.ScrollableTooltipComponent;
import com.stalemated.customtooltips.core.dimensions.util.TooltipTextUtil;
import com.stalemated.customtooltips.util.MathUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.*;

import java.util.ArrayList;
import java.util.List;

public class TooltipDimensionManager {

    public static DrawContext currentContext = null;
    public static TextRenderer currentTextRenderer = null;
    public static ItemStack currentStack = null;
    private static final TooltipConfig config = ConfigManager.getConfig();
    private static final int MIN_TOOLTIP_HEIGHT = 32;
    public static final int MIN_TOOLTIP_WIDTH = 64;
    public static final int TOOLTIP_PADDING_X = 8;
    private static final int TOOLTIP_PADDING_Y = 4;

    public static boolean nextTooltipIsItem = false;
    public static boolean isCurrentTooltipItemTooltip = false;
    public static String expectedTitleString = "";
    public static List<TooltipComponent> titleComponentList = new ArrayList<>();
    public static List<TooltipComponent> bodyComponentList = new ArrayList<>();

    private static final DimensionCache widthCache = new DimensionCache(TOOLTIP_PADDING_X, MIN_TOOLTIP_WIDTH);
    private static final DimensionCache heightCache = new DimensionCache(TOOLTIP_PADDING_Y, MIN_TOOLTIP_HEIGHT);

    private static class DimensionCache {
        private final int padding;
        private int lastWindowSize = -1;
        private int lastConfigPercent = -1;
        private int cachedSize = -1;
        private final int minSideLength;

        DimensionCache(int padding, int minSideLength) {
            this.padding = padding;
            this.minSideLength = minSideLength;
        }

        int get(int currentWindowSize, int currentConfigPercent) {
            if (currentWindowSize != lastWindowSize || currentConfigPercent != lastConfigPercent) {
                this.lastWindowSize = currentWindowSize;
                this.lastConfigPercent = currentConfigPercent;

                int maxAllowedSize = currentWindowSize - padding;
                int safePercent = MathUtils.clamp(currentConfigPercent, 1, 100);
                this.cachedSize = MathUtils.clamp(maxAllowedSize * safePercent / 100, minSideLength, maxAllowedSize);
            }
            return this.cachedSize;
        }
    }

    public static List<Text> enforceWidthLimit(List<Text> text) {
        isCurrentTooltipItemTooltip = nextTooltipIsItem;
        nextTooltipIsItem = false;

        if (!config.custom_tooltip_dimensions || !isCurrentTooltipItemTooltip || text.isEmpty()) {
            expectedTitleString = "";
            return text;
        }

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int maxTitleWidth = getScaledTooltipWidth() - LegendaryTooltipsCompat.getItemModelComponentWidth(currentStack);

        List<Text> processed = TitleOverflowStrategyFactory.getStrategy().processTextPhase(text, textRenderer, maxTitleWidth);
        expectedTitleString = "";
        return processed;
    }

    private static int calculateTotalHeight(List<TooltipComponent> components) {
        if (components.isEmpty()) return 0;
        int totalHeight = components.size() == 1 ? -2 : 0;

        for (int i = 0; i < components.size(); i++) {
            totalHeight += components.get(i).getHeight() + (i == 0 && components.size() > 1 ? 2 : 0);
        }
        return totalHeight;
    }

    public static List<TooltipComponent> enforceHeightLimit(List<TooltipComponent> components) {
        if (components.isEmpty()) return components;

        int splitIndex = getSplitIndex(components);
        int scaledTooltipWidth = getScaledTooltipWidth();
        int scaledTooltipHeight = getScaledTooltipHeight();
        int componentWidth = LegendaryTooltipsCompat.getItemModelComponentWidth(currentStack);
        int titleMaxWidth = scaledTooltipWidth - componentWidth;

        List<TooltipComponent> pinned = new ArrayList<>(components.subList(0, splitIndex));
        List<TooltipComponent> scrollableContent = new ArrayList<>(components.subList(splitIndex, components.size()));
        titleComponentList = pinned;
        bodyComponentList = scrollableContent;

        if (currentTextRenderer != null) {
            pinned = TitleOverflowStrategyFactory.getStrategy().processComponentPhase(pinned, currentTextRenderer, titleMaxWidth);

            // Scrollable content is always wrapped, but never indented
            scrollableContent = TooltipTextUtil.wrapComponents(scrollableContent, scaledTooltipWidth, currentTextRenderer, false);
        }

        List<TooltipComponent> combined = new ArrayList<>();
        combined.addAll(pinned);
        combined.addAll(scrollableContent);
        
        int totalHeight = calculateTotalHeight(combined);

        if (totalHeight > scaledTooltipHeight && currentTextRenderer != null) {
            if (scrollableContent.isEmpty()) {
                return combined;
            }

            int pinnedHeight = 0;
            for (int i = 0; i < pinned.size(); i++) {
                pinnedHeight += pinned.get(i).getHeight() + (i == 0 && pinned.size() > 1 ? 2 : 0);
            }
            int availableHeight = Math.max(scaledTooltipHeight - pinnedHeight, MIN_TOOLTIP_HEIGHT);

            pinned.add(new ScrollableTooltipComponent(scrollableContent, pinned, availableHeight, scaledTooltipWidth, currentTextRenderer));
            return pinned;
        }
        TooltipScrollManager.updateMaxScroll(0);

        return combined;
    }

    // Compat
    public static int getSplitIndex(List<TooltipComponent> components) {
        int splitIndex = LegendaryTooltipsCompat.getSplitIndex(components);
        if (splitIndex != 1) return splitIndex;

        // Vanilla Forge logic
        if (expectedTitleString != null && !expectedTitleString.isEmpty()) {
            StringBuilder accumulated = new StringBuilder();
            for (int i = 0; i < components.size(); i++) {
                accumulated.append(TooltipTextUtil.getComponentString(components.get(i)).replace(" ", ""));
                if (accumulated.length() >= expectedTitleString.length()) {
                    splitIndex = i + 1;
                    break;
                }
            }
        }

        return Math.min(splitIndex, components.size());
    }

    public static void setCurrentStack(ItemStack itemStack) {
        currentStack = itemStack;
    }

    public static ItemStack getCurrentStack() {
        return currentStack;
    }

    public static void setState(DrawContext context, TextRenderer textRenderer) {
        currentContext = context;
        currentTextRenderer = textRenderer;
    }

    public static void clearState() {
        currentStack = null;
        currentContext = null;
        currentTextRenderer = null;
    }

    public static int getScaledTooltipHeight() {
        return heightCache.get(MinecraftClient.getInstance().getWindow().getScaledHeight(), config.max_height_percentage);
    }

    public static int getScaledTooltipWidth() {
        return widthCache.get(MinecraftClient.getInstance().getWindow().getScaledWidth(), config.max_width_percentage);
    }
}