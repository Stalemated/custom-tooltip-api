package com.stalemated.customtooltips.gui.widget;

import com.stalemated.customtooltips.ConfigManager;
import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.config.TooltipConfig;
import com.stalemated.customtooltips.gui.TooltipListWidget;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.Collections;

public class ReorderActionsWidget {
    private final ButtonWidget moveUpButton;
    private final ButtonWidget moveDownButton;
    private final TooltipEntry entry;
    private final TooltipListWidget parent;

    public ReorderActionsWidget(TooltipListWidget parent, TooltipEntry entry) {
        this.parent = parent;
        this.entry = entry;
        this.moveUpButton = ButtonWidget.builder(Text.literal("▲"), btn -> move(-1)).dimensions(0, 0, 20, 20).build();
        this.moveDownButton = ButtonWidget.builder(Text.literal("▼"), btn -> move(1)).dimensions(0, 0, 20, 20).build();
    }

    private void move(int direction) {
        TooltipConfig config = ConfigManager.getConfig();
        int index = config.entries.indexOf(entry);
        int newIndex = index + direction;
        if (newIndex >= 0 && newIndex < config.entries.size()) {
            Collections.swap(config.entries, index, newIndex);
            ConfigManager.save();
            parent.updateEntries(parent.parentScreen.searchBox.getText());
        }
    }

    public void render(DrawContext context, int x, int y, int mouseX, int mouseY, float tickDelta) {
        TooltipConfig config = ConfigManager.getConfig();
        int idx = config.entries.indexOf(this.entry);

        moveUpButton.setX(x);
        moveUpButton.setY(y);
        moveUpButton.active = (idx > 0);
        moveUpButton.render(context, mouseX, mouseY, tickDelta);

        moveDownButton.setX(x + 24);
        moveDownButton.setY(y);
        moveDownButton.active = (idx >= 0 && idx < config.entries.size() - 1);
        moveDownButton.render(context, mouseX, mouseY, tickDelta);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return moveUpButton.mouseClicked(mouseX, mouseY, button) || moveDownButton.mouseClicked(mouseX, mouseY, button);
    }
}