package com.stalemated.customtooltips.core.dimensions.overflow.strategies;

import com.stalemated.customtooltips.core.dimensions.overflow.TitleOverflowStrategy;
import com.stalemated.customtooltips.core.dimensions.util.TooltipTextUtil;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.Text;

import java.util.List;

public class WrapOverflowStrategy implements TitleOverflowStrategy {

    @Override
    public List<Text> processTextPhase(List<Text> textList, TextRenderer textRenderer, int maxTitleWidth) {
        return textList;
    }

    @Override
    public List<TooltipComponent> processComponentPhase(List<TooltipComponent> components, TextRenderer textRenderer, int maxTitleWidth) {
        if (components.isEmpty() || textRenderer == null) {
            return components;
        }
        return TooltipTextUtil.wrapComponents(components, maxTitleWidth, textRenderer, true);
    }
}
