package com.stalemated.customtooltips.core.dimensions.util;

import com.stalemated.customtooltips.compat.LegendaryTooltipsCompat;
import com.stalemated.customtooltips.core.dimensions.TooltipDimensionManager;
import com.stalemated.customtooltips.core.dimensions.components.IndentedTextTooltipComponent;
import com.stalemated.customtooltips.core.dimensions.components.WrappedTitleTooltipComponent;
import com.stalemated.customtooltips.mixin.client.accessor.OrderedTextTooltipComponentAccessor;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.stalemated.customtooltips.core.dimensions.components.ScrollableTooltipComponent.SCROLLBAR_WIDTH;

public class TooltipTextUtil {
    public static boolean isHandlingCustomWrap = false;

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

    public static MutableText convertOrderedTextToMutable(OrderedText orderedText) {
        StyleAccumulator acc = new StyleAccumulator();
        orderedText.accept((index, style, codePoint) -> {
            acc.append(style, codePoint);
            return true;
        });
        return acc.build();
    }

    public static Optional<OrderedText> getExtractedTextValue(TooltipComponent comp) {
        if (comp instanceof OrderedTextTooltipComponentAccessor accessor) {
            return Optional.ofNullable(accessor.getText());
        }
        return Optional.empty();
    }

    public static String getComponentString(TooltipComponent comp) {
        StringBuilder sb = new StringBuilder();
        Optional<OrderedText> extracted = TooltipTextUtil.getExtractedTextValue(comp);

        extracted.ifPresent(value -> value.accept((index, style, codePoint) -> {
            sb.appendCodePoint(codePoint);
            return true;
        }));
        return sb.toString();
    }

    public static List<TooltipComponent> wrapComponents(List<TooltipComponent> components, int targetWidth, TextRenderer textRenderer, boolean isTitle) {
        List<TooltipComponent> wrappedComponents = new ArrayList<>();
        int wrapWidth = targetWidth - SCROLLBAR_WIDTH;

        for (TooltipComponent comp : components) {
            boolean wrappedFallback = false;

            if (textRenderer != null) {
                Optional<OrderedText> extracted = getExtractedTextValue(comp);

                if (extracted.isPresent()) {
                    OrderedText value = extracted.get();

                    if (textRenderer.getWidth(value) > targetWidth) {
                        MutableText mutable = convertOrderedTextToMutable(value);
                        wrappedFallback = handleCustomWrap(wrapWidth, textRenderer, wrappedComponents, mutable, isTitle);
                    }
                }
            }
            if (wrappedFallback) continue;
            wrappedComponents.add(comp);
        }
        return wrappedComponents;
    }

    public static boolean handleCustomWrap(int targetWidth, TextRenderer textRenderer, List<TooltipComponent> wrappedComponents, StringVisitable visitable, boolean isTitle) {
        isHandlingCustomWrap = true;
        List<OrderedText> wrapped = new ArrayList<>(textRenderer.wrapLines(visitable, targetWidth));
        isHandlingCustomWrap = false;

        List<TooltipComponent> titleLines = new ArrayList<>();

        for (int i = 0; i < wrapped.size(); i++) {
            OrderedText w = wrapped.get(i);

            int currentOffset = 0;
            if (isTitle) {
                if (i == 0) {
                    if (convertOrderedTextToMutable(w).getString().isBlank()) {
                        continue;
                    }
                } else {
                    currentOffset = LegendaryTooltipsCompat.getItemModelComponentWidth(TooltipDimensionManager.getCurrentStack());
                }
                titleLines.add(new IndentedTextTooltipComponent(w, currentOffset));
            } else {
                wrappedComponents.add(new IndentedTextTooltipComponent(w, currentOffset));
            }
        }

        if (isTitle && !titleLines.isEmpty()) {
            if (titleLines.size() == 1) {
                wrappedComponents.add(titleLines.get(0));
            } else {
                if (!TooltipDimensionManager.bodyComponentList.isEmpty()) {
                    wrappedComponents.addAll(titleLines);
                } else {
                    wrappedComponents.add(new WrappedTitleTooltipComponent(titleLines));
                }
            }
        }
        return true;
    }
}
