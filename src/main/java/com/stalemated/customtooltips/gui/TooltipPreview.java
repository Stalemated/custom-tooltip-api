package com.stalemated.customtooltips.gui;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.api.enums.TooltipPosition;
import com.stalemated.customtooltips.core.CurrentTooltip;
import com.stalemated.customtooltips.core.TooltipLook;
import com.stalemated.lib.util.state.SharedTooltipState;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public final class TooltipPreview {

    private TooltipPreview() {
    }

    public static void render(TooltipEntry entry, List<Component> lines, GuiGraphics context, int mouseX, int mouseY) {
        if (entry.position != TooltipPosition.REPLACE_NAME) {
            lines.add(0, Component.nullToEmpty(entry.target));
        }

        //? if >=1.21.6 {
        /*CurrentTooltip.render(TooltipLook.of(List.of(entry)), () -> SharedTooltipState.withCustomDimensions(() -> context.setComponentTooltipForNextFrame(Minecraft.getInstance().font, lines, mouseX, mouseY)));
        *///?} else {
        CurrentTooltip.render(TooltipLook.of(List.of(entry)), () -> SharedTooltipState.withCustomDimensions(() -> context.renderComponentTooltip(Minecraft.getInstance().font, lines, mouseX, mouseY)));
        //?}
    }
}
