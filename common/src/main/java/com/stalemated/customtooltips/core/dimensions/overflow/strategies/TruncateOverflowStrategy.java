package com.stalemated.customtooltips.core.dimensions.overflow.strategies;

import com.stalemated.customtooltips.core.dimensions.TooltipDimensionManager;
import com.stalemated.customtooltips.core.dimensions.overflow.TitleOverflowStrategy;
import com.stalemated.customtooltips.util.TooltipTextUtil;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

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
        return components;
    }

    private MutableText truncateTitle(Text title, TextRenderer textRenderer, int maxWidth) {
        String truncatedIndicator = "...";
        int indicatorWidth = textRenderer.getWidth(truncatedIndicator);
        int availableWidth = Math.max(10, maxWidth - indicatorWidth - TooltipDimensionManager.getExtraComponentWidth(TooltipDimensionManager.titleComponentList));

        StringVisitable truncated = textRenderer.trimToWidth(title, availableWidth);
        MutableText rebuilt = TooltipTextUtil.preserveStyles(truncated);

        rebuilt.append(Text.literal(truncatedIndicator).setStyle(title.getStyle()));
        return rebuilt;
    }
}
