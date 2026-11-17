package com.stalemated.customtooltips.gui;

import com.stalemated.lib.compat.yacl.SLibYacl;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

public final class EditButtons {
    private EditButtons() {
    }

    public static Button requireYacl(Button button) {
        if (SLibYacl.isAvailable()) return button;

        button.active = false;
        button.setTooltip(Tooltip.create(Component.translatable("customtooltips.tooltip_list_screen.yacl_missing")));
        return button;
    }
}
