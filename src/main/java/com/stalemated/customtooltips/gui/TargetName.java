package com.stalemated.customtooltips.gui;

import com.stalemated.customtooltips.TooltipEntry;
import net.minecraft.network.chat.Component;

public final class TargetName {

    private TargetName() {
    }

    public static Component of(TooltipEntry entry) {
        if (entry.target == null || entry.target.isBlank()) return Component.translatable("customtooltips.tooltip_list_widget.new_tooltip");
        return Component.literal(entry.target);
    }
}
