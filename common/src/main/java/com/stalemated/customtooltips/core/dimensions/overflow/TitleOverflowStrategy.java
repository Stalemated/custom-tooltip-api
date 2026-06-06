package com.stalemated.customtooltips.core.dimensions.overflow;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.Text;

import java.util.List;

public interface TitleOverflowStrategy {
    List<Text> processTextPhase(List<Text> textList, TextRenderer textRenderer, int maxTitleWidth);

    List<TooltipComponent> processComponentPhase(List<TooltipComponent> components, TextRenderer textRenderer, int maxTitleWidth);
}
