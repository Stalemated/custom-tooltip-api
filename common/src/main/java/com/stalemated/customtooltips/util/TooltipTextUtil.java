package com.stalemated.customtooltips.util;

import com.stalemated.customtooltips.core.dimensions.TooltipDimensionManager;
import com.stalemated.customtooltips.core.dimensions.overflow.components.IndentedTextTooltipComponent;
import com.stalemated.customtooltips.mixin.client.OrderedTextTooltipComponentAccessor;
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

    public static Optional<Object> getExtractedTextValue(TooltipComponent comp) {
        if (comp instanceof OrderedTextTooltipComponentAccessor accessor) {
            return Optional.ofNullable(accessor.getText());
        }
        return Optional.empty();
    }

    public static List<TooltipComponent> wrapComponents(List<TooltipComponent> components, int targetWidth, TextRenderer textRenderer) {
        List<TooltipComponent> wrappedComponents = new ArrayList<>();
        int extraWidth = TooltipDimensionManager.getExtraComponentWidth(components);

        for (TooltipComponent comp : components) {
            boolean wrappedFallback = false;

            if (textRenderer != null) {
                Optional<Object> extracted = getExtractedTextValue(comp);

                if (extracted.isPresent()) {
                    Object value = extracted.get();

                    if (value instanceof OrderedText orderedText) {
                        if (textRenderer.getWidth(orderedText) > targetWidth) {
                            MutableText mutable = convertOrderedTextToMutable(orderedText);
                            wrappedFallback = handleCustomWrap(targetWidth, textRenderer, wrappedComponents, mutable, extraWidth);
                        }
                    } else if (value instanceof StringVisitable visitable) {
                        if (textRenderer.getWidth(visitable) > targetWidth) {
                            wrappedFallback = handleCustomWrap(targetWidth, textRenderer, wrappedComponents, visitable, extraWidth);
                        }
                    }
                }
            }
            if (wrappedFallback) continue;
            wrappedComponents.add(comp);
        }
        return wrappedComponents;
    }

    public static boolean handleCustomWrap(int targetWidth, TextRenderer textRenderer, List<TooltipComponent> wrappedComponents, StringVisitable visitable, int extraWidth) {
        isHandlingCustomWrap = true;
        List<OrderedText> wrapped = new ArrayList<>(textRenderer.wrapLines(visitable, targetWidth));
        isHandlingCustomWrap = false;

        for (int i = 0; i < wrapped.size(); i++) {
            OrderedText w = wrapped.get(i);

            if (i == 0 || extraWidth == 0) {
                wrappedComponents.add(TooltipComponent.of(w));
            } else {
                wrappedComponents.add(new IndentedTextTooltipComponent(w, extraWidth));
            }
        }
        return true;
    }
}
