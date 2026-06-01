package com.stalemated.customtooltips.core.dimensions;

import com.stalemated.customtooltips.ConfigManager;
import com.stalemated.customtooltips.config.TooltipConfig;
import com.stalemated.customtooltips.util.MathUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TooltipDimensionManager {

    public static DrawContext currentContext = null;
    public static TextRenderer currentTextRenderer = null;
    private static final TooltipConfig config = ConfigManager.getConfig();
    private static final int minTooltipSide = 10;
    private static final int TOOLTIP_PADDING_X = 8;
    private static final int TOOLTIP_PADDING_Y = 4;

    public static List<TooltipComponent> wrapAndLimitWidth(TextRenderer textRenderer, List<Text> text, Optional<TooltipData> data) {
        int scaledTooltipWidth = getScaledTooltipWidth();
        int scaledTooltipHeight = getScaledTooltipHeight();

        List<TooltipComponent> components = doWrap(textRenderer, text, data, scaledTooltipWidth);

        if (calculateTotalHeight(components) > scaledTooltipHeight) {
            int maxWrapWidth = Math.max(minTooltipSide, scaledTooltipWidth - ScrollableTooltipComponent.SCROLLBAR_WIDTH);
            components = doWrap(textRenderer, text, data, maxWrapWidth);
        }

        return components;
    }

    private static List<TooltipComponent> doWrap(TextRenderer textRenderer, List<Text> text, Optional<TooltipData> data, int maxWidth) {
        List<TooltipComponent> components = new ArrayList<>();
        int firstLineCount = 0;
        
        for (int i = 0; i < text.size(); i++) {
            Text line = text.get(i);
            List<TooltipComponent> lineComponents = new ArrayList<>();

            if (textRenderer.getWidth(line) > maxWidth) {
                List<OrderedText> wrapped = textRenderer.wrapLines(line, maxWidth);
                for (OrderedText orderedText : wrapped) {
                    lineComponents.add(TooltipComponent.of(orderedText));
                }
            } else {
                lineComponents.add(TooltipComponent.of(line.asOrderedText()));
            }

            components.addAll(lineComponents);

            if (i == 0) {
                firstLineCount = lineComponents.size();
            }
        }

        if (data.isPresent()) {
            components.add(firstLineCount, TooltipComponent.of(data.get()));
        }

        return components;
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
        int scaledTooltipHeight = getScaledTooltipHeight();

        if (components.isEmpty()) return components;
        int totalHeight = calculateTotalHeight(components);

        if (totalHeight > scaledTooltipHeight && currentTextRenderer != null) {
            return List.of(new ScrollableTooltipComponent(components, scaledTooltipHeight, currentTextRenderer));
        }
        TooltipScrollManager.updateMaxScroll(0);

        return components;
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
        int scaledWindowHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();
        int maxAllowedHeight = scaledWindowHeight - TOOLTIP_PADDING_Y;
        return MathUtils.clamp(maxAllowedHeight * config.max_height_percentage / 100, minTooltipSide, maxAllowedHeight);
    }

    public static int getScaledTooltipWidth() {
        int scaledWindowWidth = MinecraftClient.getInstance().getWindow().getScaledWidth();
        int maxAllowedWidth = scaledWindowWidth - TOOLTIP_PADDING_X;
        return MathUtils.clamp(maxAllowedWidth * config.max_width_percentage / 100, minTooltipSide, maxAllowedWidth);
    }
}