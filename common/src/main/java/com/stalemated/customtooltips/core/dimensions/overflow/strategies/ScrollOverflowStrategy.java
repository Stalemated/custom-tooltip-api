package com.stalemated.customtooltips.core.dimensions.overflow.strategies;

import com.stalemated.customtooltips.core.dimensions.overflow.TitleOverflowStrategy;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ScrollOverflowStrategy implements TitleOverflowStrategy {

    @Override
    public List<Text> processTextPhase(List<Text> textList, TextRenderer textRenderer, int maxTitleWidth) {
        return textList;
    }

    @Override
    public List<TooltipComponent> processComponentPhase(List<TooltipComponent> components, TextRenderer textRenderer, int maxTitleWidth) {
        List<TooltipComponent> modified = new ArrayList<>();
        for (TooltipComponent component : components) {
            OrderedText extractedText = extractText(component);

            if (extractedText != null && component.getWidth(textRenderer) > maxTitleWidth) {
                modified.add(new ScrollingTitleTooltipComponent(extractedText, maxTitleWidth));
            } else {
                modified.add(component);
            }
        }
        return modified;
    }

    private OrderedText extractText(TooltipComponent component) {
        try {
            for (Field field : component.getClass().getDeclaredFields()) {
                if (OrderedText.class.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);
                    return (OrderedText) field.get(component);
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}
