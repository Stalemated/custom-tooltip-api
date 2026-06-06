package com.stalemated.customtooltips.util;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TooltipTextUtil {

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

    public static List<TooltipComponent> wrapComponents(List<TooltipComponent> components, int targetWidth, TextRenderer textRenderer) {
        List<TooltipComponent> wrappedComponents = new ArrayList<>();

        for (TooltipComponent comp : components) {
            boolean wrappedFallback = false;

            if (textRenderer != null) {
                try {
                    for (Field field : comp.getClass().getDeclaredFields()) {
                        field.setAccessible(true);
                        Object value = field.get(comp);

                        if (value instanceof OrderedText orderedText) {
                            if (textRenderer.getWidth(orderedText) > targetWidth) {
                                MutableText mutable = convertOrderedTextToMutable(orderedText);
                                List<OrderedText> wrapped = textRenderer.wrapLines(mutable, targetWidth);

                                for (OrderedText w : wrapped) {
                                    wrappedComponents.add(TooltipComponent.of(w));
                                }
                                wrappedFallback = true;
                                break;
                            }
                        } else if (value instanceof StringVisitable visitable) {
                            if (textRenderer.getWidth(visitable) > targetWidth) {
                                List<OrderedText> wrapped = textRenderer.wrapLines(visitable, targetWidth);

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
}
