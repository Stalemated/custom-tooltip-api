package com.stalemated.customtooltips.gui.widget;

import com.stalemated.customtooltips.config.CustomTooltipsConfigRegistry;
import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.config.TooltipConfig;
import java.util.Collections;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class ReorderActionsWidget {
    private final Button moveUpButton;
    private final Button moveDownButton;
    private final TooltipEntry entry;
    private final TooltipListWidget parent;

    public ReorderActionsWidget(TooltipListWidget parent, TooltipEntry entry) {
        this.parent = parent;
        this.entry = entry;
        this.moveUpButton = Button.builder(Component.literal("▲"), btn -> move(-1)).bounds(0, 0, 20, 20).build();
        this.moveDownButton = Button.builder(Component.literal("▼"), btn -> move(1)).bounds(0, 0, 20, 20).build();
    }

    private void move(int direction) {
        TooltipConfig config = CustomTooltipsConfigRegistry.CONFIG.get();
        int index = config.entries.indexOf(entry);
        int newIndex = index + direction;
        if (newIndex >= 0 && newIndex < config.entries.size()) {
            Collections.swap(config.entries, index, newIndex);
            CustomTooltipsConfigRegistry.CONFIG.save();
            parent.updateEntries(parent.parentScreen.searchBox.getValue());
        }
    }

    public void render(GuiGraphics context, int x, int y, int mouseX, int mouseY, float tickDelta) {
        TooltipConfig config = CustomTooltipsConfigRegistry.CONFIG.get();
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