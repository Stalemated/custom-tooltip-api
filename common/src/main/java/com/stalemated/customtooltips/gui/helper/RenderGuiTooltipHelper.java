package com.stalemated.customtooltips.gui.helper;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.core.TooltipBackgroundManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.List;

public class RenderGuiTooltipHelper {
    public static void renderGuiTooltip(TooltipEntry entry, List<Text> lines, DrawContext context, int mouseX, int mouseY) {

        if (entry.position != TooltipEntry.TooltipPosition.REPLACE_NAME) {
            lines.add(0, Text.of(entry.target));
        }

        TooltipBackgroundManager.setCurrentBackgroundOpacity(entry.backgroundOpacity);
        TooltipBackgroundManager.setCurrentBorderOpacity(entry.borderOpacity);
        TooltipBackgroundManager.setCurrentEntry(entry);

        //TooltipDimensionManager.isCurrentTooltipItemTooltip = true;
        context.drawTooltip(MinecraftClient.getInstance().textRenderer, lines, mouseX, mouseY);
        //TooltipDimensionManager.isCurrentTooltipItemTooltip = false;
        TooltipBackgroundManager.clearState();
    }
}
