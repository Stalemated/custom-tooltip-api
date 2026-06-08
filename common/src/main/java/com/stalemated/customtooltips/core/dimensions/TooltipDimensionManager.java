package com.stalemated.customtooltips.core.dimensions;

import com.stalemated.customtooltips.ConfigManager;
import com.stalemated.customtooltips.config.TooltipConfig;
import com.stalemated.customtooltips.core.dimensions.overflow.TitleOverflowStrategyFactory;
import com.stalemated.customtooltips.util.TooltipTextUtil;
import com.stalemated.customtooltips.util.MathUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class TooltipDimensionManager {

    public static DrawContext currentContext = null;
    public static TextRenderer currentTextRenderer = null;
    private static final TooltipConfig config = ConfigManager.getConfig();
    private static final int MIN_TOOLTIP_HEIGHT = 24;
    private static final int MIN_TOOLTIP_WIDTH = 48;
    private static final int TOOLTIP_PADDING_X = 16;
    private static final int TOOLTIP_PADDING_Y = 4;

    public static boolean nextTooltipIsItem = false;
    public static boolean isCurrentTooltipItemTooltip = false;
    public static String expectedTitleString = "";
    public static List<TooltipComponent> componentList;

    private static final DimensionCache widthCache = new DimensionCache(TOOLTIP_PADDING_X, MIN_TOOLTIP_WIDTH);
    private static final DimensionCache heightCache = new DimensionCache(TOOLTIP_PADDING_Y, MIN_TOOLTIP_HEIGHT);

    // Legendary Tooltips compat
    private static final Class<?> ITEM_MODEL_COMPONENT_CLASS;
    private static final Class<?> PADDING_COMPONENT_CLASS;
    private static final Class<?> TITLE_BREAK_COMPONENT_CLASS;
    static {
        Class<?> itemModelClass = null;
        Class<?> paddingClass = null;
        Class<?> titleBreakClass = null;
        try {
            itemModelClass = Class.forName("com.anthonyhilyard.legendarytooltips.tooltip.ItemModelComponent");
        } catch (ClassNotFoundException ignored) {}
        try {
            paddingClass = Class.forName("com.anthonyhilyard.legendarytooltips.tooltip.PaddingComponent");
        } catch (ClassNotFoundException ignored) {}
        try {
            titleBreakClass = Class.forName("com.anthonyhilyard.iceberg.util.Tooltips$TitleBreakComponent");
        } catch (ClassNotFoundException ignored) {}
        ITEM_MODEL_COMPONENT_CLASS = itemModelClass;
        PADDING_COMPONENT_CLASS = paddingClass;
        TITLE_BREAK_COMPONENT_CLASS = titleBreakClass;
    }

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

    // Legendary Tooltips compat
    public static int getExtraComponentWidth(List<TooltipComponent> components) {
        if (ITEM_MODEL_COMPONENT_CLASS == null) return 0;

        for (TooltipComponent comp : components) {
            if (ITEM_MODEL_COMPONENT_CLASS.isInstance(comp)) {
                return 22;
            }
        }
        return 0;
    }

    public static List<Text> enforceWidthLimit(List<Text> text) {
        isCurrentTooltipItemTooltip = nextTooltipIsItem;
        nextTooltipIsItem = false;

        if (!config.custom_tooltip_dimensions || !isCurrentTooltipItemTooltip || text.isEmpty()) {
            expectedTitleString = "";
            return text;
        }

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int maxTitleWidth = getScaledTooltipWidth();

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
        componentList = components;
        if (components.isEmpty()) return components;

        int splitIndex = getSplitIndex(components);
        List<TooltipComponent> pinned = new ArrayList<>(components.subList(0, splitIndex));
        List<TooltipComponent> scrollableContent = new ArrayList<>(components.subList(splitIndex, components.size()));

        if (currentTextRenderer != null) {
            int scaledTooltipWidth = getScaledTooltipWidth();
            pinned = TitleOverflowStrategyFactory.getStrategy().processComponentPhase(pinned, currentTextRenderer, scaledTooltipWidth);
            // Scrollable content is always wrapped
            scrollableContent = TooltipTextUtil.wrapComponents(scrollableContent, scaledTooltipWidth, currentTextRenderer);
        }

        int scaledTooltipHeight = getScaledTooltipHeight();
        
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

            pinned.add(new ScrollableTooltipComponent(scrollableContent, pinned, availableHeight, currentTextRenderer));
            return pinned;
        }
        TooltipScrollManager.updateMaxScroll(0);

        return combined;
    }

    // Legendary Tooltips compat
    public static int getSplitIndex(List<TooltipComponent> components) {
        int splitIndex = 1;
        if (PADDING_COMPONENT_CLASS != null || TITLE_BREAK_COMPONENT_CLASS != null) {
            for (int i = 0; i < components.size(); i++) {
                TooltipComponent comp = components.get(i);

                if ((PADDING_COMPONENT_CLASS != null && PADDING_COMPONENT_CLASS.isInstance(comp)) ||
                        (TITLE_BREAK_COMPONENT_CLASS != null && TITLE_BREAK_COMPONENT_CLASS.isInstance(comp))) {
                    splitIndex = i + 1;
                    break;
                }
            }
            return Math.min(splitIndex, components.size());
        }

        // Vanilla Forge logic
        if (expectedTitleString != null && !expectedTitleString.isEmpty()) {
            StringBuilder accumulated = new StringBuilder();
            for (int i = 0; i < components.size(); i++) {
                accumulated.append(getComponentString(components.get(i)).replace(" ", ""));
                if (accumulated.length() >= expectedTitleString.length()) {
                    splitIndex = i + 1;
                    break;
                }
            }
        }

        return Math.min(splitIndex, components.size());
    }

    private static String getComponentString(TooltipComponent comp) {
        StringBuilder sb = new StringBuilder();
        try {
            for (Field field : comp.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(comp);
                if (value instanceof OrderedText orderedText) {
                    orderedText.accept((index, style, codePoint) -> {
                        sb.appendCodePoint(codePoint);
                        return true;
                    });
                } else if (value instanceof StringVisitable visitable) {
                    sb.append(visitable.getString());
                }
            }
        } catch (Exception ignored) {
        }
        return sb.toString();
    }

    public static void setState(DrawContext context, TextRenderer textRenderer) {
        currentContext = context;
        currentTextRenderer = textRenderer;
    }

    public static void clearState() {
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