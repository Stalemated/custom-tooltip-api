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

    private static List<TooltipComponent> wrapComponents(List<TooltipComponent> components) {
        int scaledTooltipWidth = getScaledTooltipWidth();
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

    public static MutableText preserveStyles(StringVisitable visitable) {
        MutableText result = Text.empty();
        class StyleAccumulator {
            final StringBuilder currentText = new StringBuilder();
            Style currentStyle = Style.EMPTY;

            void accept(Style style, String text) {
                if (!style.equals(currentStyle) && !currentText.isEmpty()) {
                    result.append(Text.literal(currentText.toString()).setStyle(currentStyle));
                    currentText.setLength(0);
                }
                currentStyle = style;
                currentText.append(text);
            }

            void finish() {
                if (!currentText.isEmpty()) {
                    result.append(Text.literal(currentText.toString()).setStyle(currentStyle));
                }
            }
        }
        StyleAccumulator acc = new StyleAccumulator();
        visitable.visit((style, string) -> {
            acc.accept(style, string);
            return Optional.empty();
        }, Style.EMPTY);

        acc.finish();
        return result;
    }

    private static MutableText convertOrderedTextToMutable(OrderedText orderedText) {
        MutableText result = Text.empty();
        class StyleAccumulator {
            final StringBuilder currentText = new StringBuilder();
            Style currentStyle = Style.EMPTY;

            void accept(Style style, int codePoint) {
                if (!style.equals(currentStyle) && !currentText.isEmpty()) {
                    result.append(Text.literal(currentText.toString()).setStyle(currentStyle));
                    currentText.setLength(0);
                }
                currentStyle = style;
                currentText.appendCodePoint(codePoint);
            }

            void finish() {
                if (!currentText.isEmpty()) {
                    result.append(Text.literal(currentText.toString()).setStyle(currentStyle));
                }
            }
        }
        StyleAccumulator acc = new StyleAccumulator();
        orderedText.accept((index, style, codePoint) -> {
            acc.accept(style, codePoint);
            return true;
        });
        acc.finish();
        return result;
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
        List<TooltipComponent> pinned = new ArrayList<>(components.subList(0, splitIndex));
        List<TooltipComponent> scrollableContent = new ArrayList<>(components.subList(splitIndex, components.size()));

        if (currentTextRenderer != null) {
            scrollableContent = wrapComponents(scrollableContent);
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
        for (int i = 0; i < components.size(); i++) {
            String className = components.get(i).getClass().getSimpleName();
            if (className.equals("PaddingComponent") || className.equals("TitleBreakComponent")) {
                splitIndex = i + 1;
                break;
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