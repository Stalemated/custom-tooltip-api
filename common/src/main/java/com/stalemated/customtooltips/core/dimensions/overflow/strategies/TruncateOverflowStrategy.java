package com.stalemated.customtooltips.core.dimensions.overflow.strategies;

import com.stalemated.customtooltips.core.dimensions.components.ScrollableTooltipComponent;
import com.stalemated.customtooltips.core.dimensions.overflow.TitleOverflowStrategy;
import com.stalemated.customtooltips.core.dimensions.util.TooltipTextUtil;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TruncateOverflowStrategy implements TitleOverflowStrategy {

    @Override
    public List<Text> processTextPhase(List<Text> textList, TextRenderer textRenderer, int maxTitleWidth) {
        if (textList.isEmpty() || textRenderer == null) {
            return textList;
        }

        Text title = textList.get(0);
        if (textRenderer.getWidth(title) > maxTitleWidth) {
            List<Text> mutableText = new ArrayList<>(textList);

            mutableText.set(0, truncateTitle(title, textRenderer, maxTitleWidth));
            return mutableText;
        }

        return textList;
    }

    @Override
    public List<TooltipComponent> processComponentPhase(List<TooltipComponent> components, TextRenderer textRenderer, int maxTitleWidth) {
        if (components.isEmpty() || textRenderer == null) {
            return components;
        }

        TooltipComponent titleComponent = components.get(0);
        Optional<OrderedText> extracted = TooltipTextUtil.getExtractedTextValue(titleComponent);

        if (extracted.isPresent()) {
            OrderedText value = extracted.get();

            if (textRenderer.getWidth(value) > maxTitleWidth) {
                MutableText mutable = TooltipTextUtil.convertOrderedTextToMutable(value);
                List<TooltipComponent> mutableComponents = new ArrayList<>(components);

                mutableComponents.set(0, TooltipComponent.of(truncateTitle(mutable, textRenderer, maxTitleWidth).asOrderedText()));
                return mutableComponents;
            }
        }

        return components;
    }

    private MutableText truncateTitle(Text title, TextRenderer textRenderer, int maxWidth) {
        String truncatedIndicator = "...";
        int indicatorWidth = textRenderer.getWidth(truncatedIndicator);
        int availableWidth = Math.max(indicatorWidth, maxWidth - indicatorWidth - ScrollableTooltipComponent.SCROLLBAR_WIDTH);

        StringVisitable truncated = textRenderer.trimToWidth(title, availableWidth);
        MutableText rebuilt = TooltipTextUtil.preserveStyles(truncated);

        rebuilt.append(Text.literal(truncatedIndicator).setStyle(title.getStyle()));
        return rebuilt;
    }
}
