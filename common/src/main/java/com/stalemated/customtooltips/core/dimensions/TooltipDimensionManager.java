package com.stalemated.customtooltips.core.dimensions;

import com.stalemated.customtooltips.ConfigManager;
import com.stalemated.customtooltips.config.TooltipConfig;
import com.stalemated.customtooltips.util.MathUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TooltipDimensionManager {

    public static DrawContext currentContext = null;
    public static TextRenderer currentTextRenderer = null;
    private static final TooltipConfig config = ConfigManager.getConfig();
    private static final int MIN_TOOLTIP_HEIGHT = 24;
    private static final int MIN_TOOLTIP_WIDTH = 48;
    private static final int TOOLTIP_PADDING_X = 8;
    private static final int TOOLTIP_PADDING_Y = 4;

    public static boolean nextTooltipIsItem = false;
    public static boolean isCurrentTooltipItemTooltip = false;

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
                this.cachedSize = MathUtils.clamp(maxAllowedSize * currentConfigPercent / 100, minSideLength, maxAllowedSize);
            }
            return this.cachedSize;
        }
    }

    private static int getExtraComponentWidth(List<TooltipComponent> components) {
        if (ITEM_MODEL_COMPONENT_CLASS == null) return 0;

        for (TooltipComponent comp : components) {
            if (ITEM_MODEL_COMPONENT_CLASS.isInstance(comp)) {
                return 22;
            }
        }
        return 0;
    }

    private static List<TooltipComponent> wrapComponents(List<TooltipComponent> components, int extraWidth) {
        int scaledTooltipWidth = getScaledTooltipWidth() + extraWidth;
        List<TooltipComponent> wrappedComponents = new ArrayList<>();

        for (TooltipComponent comp : components) {
            boolean wrappedFallback = false;

            if (currentTextRenderer != null) {
                try {
                    for (Field field : comp.getClass().getDeclaredFields()) {
                        field.setAccessible(true);
                        Object value = field.get(comp);

                        if (value instanceof OrderedText orderedText) {
                            if (currentTextRenderer.getWidth(orderedText) > scaledTooltipWidth) {
                                MutableText mutable = convertOrderedTextToMutable(orderedText);
                                List<OrderedText> wrapped = currentTextRenderer.wrapLines(mutable, scaledTooltipWidth);

                                for (OrderedText w : wrapped) {
                                    wrappedComponents.add(TooltipComponent.of(w));
                                }
                                wrappedFallback = true;
                                break;
                            }
                        } else if (value instanceof StringVisitable visitable) {
                            if (currentTextRenderer.getWidth(visitable) > scaledTooltipWidth) {
                                List<OrderedText> wrapped = currentTextRenderer.wrapLines(visitable, scaledTooltipWidth);

                                for (OrderedText w : wrapped) {
                                    wrappedComponents.add(TooltipComponent.of(w));
                                }
                                wrappedFallback = true;
                                break;
                            }
                        }
                    }
                } catch (Exception ignored) {
                }
            }
            if (wrappedFallback) continue;
            wrappedComponents.add(comp);
        }
        return wrappedComponents;
    }

    private static class StyleAccumulator {
        private final MutableText result = Text.empty();
        private final StringBuilder currentText = new StringBuilder();
        private Style currentStyle = Style.EMPTY;

        void append(Style style, String text) {
            flushIfStyleChanged(style);
            currentText.append(text);
        }

        void append(Style style, int codePoint) {
            flushIfStyleChanged(style);
            currentText.appendCodePoint(codePoint);
        }

        private void flushIfStyleChanged(Style newStyle) {
            if (!newStyle.equals(currentStyle) && !currentText.isEmpty()) {
                result.append(Text.literal(currentText.toString()).setStyle(currentStyle));
                currentText.setLength(0);
            }
            currentStyle = newStyle;
        }

        MutableText build() {
            if (!currentText.isEmpty()) {
                result.append(Text.literal(currentText.toString()).setStyle(currentStyle));
                currentText.setLength(0);
            }
            return result;
        }
    }

    public static MutableText preserveStyles(StringVisitable visitable) {
        StyleAccumulator acc = new StyleAccumulator();
        visitable.visit((style, string) -> {
            acc.append(style, string);
            return Optional.empty();
        }, Style.EMPTY);
        return acc.build();
    }

    private static MutableText convertOrderedTextToMutable(OrderedText orderedText) {
        StyleAccumulator acc = new StyleAccumulator();
        orderedText.accept((index, style, codePoint) -> {
            acc.append(style, codePoint);
            return true;
        });
        return acc.build();
    }

    public static List<Text> enforceWidthLimit(List<Text> text) {
        isCurrentTooltipItemTooltip = nextTooltipIsItem;
        nextTooltipIsItem = false;

        if (!config.custom_tooltip_dimensions || !isCurrentTooltipItemTooltip || text.isEmpty()) {
            return text;
        }

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int maxTitleWidth = getScaledTooltipWidth();
        Text title = text.get(0);

        // Truncate mode
        if (textRenderer.getWidth(title) > maxTitleWidth) {
            List<Text> mutableText = new ArrayList<>(text);
            mutableText.set(0, truncateTitle(title, textRenderer, maxTitleWidth));
            return mutableText;
        }
        return text;
    }

    private static MutableText truncateTitle(Text title, TextRenderer textRenderer, int maxWidth) {
        String truncatedIndicator = "...";
        int indicatorWidth = textRenderer.getWidth(truncatedIndicator);
        int availableWidth = Math.max(10, maxWidth - indicatorWidth);

        StringVisitable truncated = textRenderer.trimToWidth(title, availableWidth);
        MutableText rebuilt = preserveStyles(truncated);

        rebuilt.append(Text.literal(truncatedIndicator).setStyle(title.getStyle()));
        return rebuilt;
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

        int extraWidth = getExtraComponentWidth(components);
        int splitIndex = getSplitIndex(components);
        List<TooltipComponent> pinned = new ArrayList<>(components.subList(0, splitIndex));
        List<TooltipComponent> scrollableContent = new ArrayList<>(components.subList(splitIndex, components.size()));

        if (currentTextRenderer != null) {
            // Wrap mode
            //pinned = wrapComponents(pinned, extraWidth);
            scrollableContent = wrapComponents(scrollableContent, extraWidth);
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
        }

        return Math.min(splitIndex, components.size());
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