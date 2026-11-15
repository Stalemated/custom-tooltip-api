package com.stalemated.customtooltips.gui.helper;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.api.enums.TooltipPosition;
import com.stalemated.customtooltips.core.TooltipBackgroundManager;
import com.stalemated.lib.util.state.SharedTooltipState;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class RenderGuiTooltipHelper {
    public static void renderGuiTooltip(TooltipEntry entry, List<Component> lines, GuiGraphics context, int mouseX, int mouseY) {

        if (entry.position != TooltipPosition.REPLACE_NAME) {
            lines.add(0, Component.nullToEmpty(entry.target));
        }

        TooltipBackgroundManager.setCurrentBackgroundOpacity(entry.backgroundOpacity);
        TooltipBackgroundManager.setCurrentBorderOpacity(entry.borderOpacity);
        TooltipBackgroundManager.setCurrentEntry(entry);

        SharedTooltipState.forceCustomDimensions = true;
        context.renderComponentTooltip(Minecraft.getInstance().font, lines, mouseX, mouseY);
        SharedTooltipState.forceCustomDimensions = false;
        TooltipBackgroundManager.clearState();
    }
}
