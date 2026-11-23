package com.stalemated.customtooltips.gui.widget;

import com.stalemated.customtooltips.config.CustomTooltipsConfigRegistry;
import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.config.TooltipConfig;
import com.stalemated.customtooltips.gui.TooltipListChanges;
import java.util.Collections;
import java.util.List;
//? if >=26.1 {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
*///?} else {
import net.minecraft.client.gui.GuiGraphics;
//?}
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
//? if >=1.21.9 {
/*import net.minecraft.client.input.MouseButtonEvent;
*///?}

public class ReorderActionsWidget {
    private final Button moveUpButton;
    private final Button moveDownButton;
    private final TooltipEntry entry;
    private final Runnable refreshList;

    public ReorderActionsWidget(Runnable refreshList, TooltipEntry entry) {
        this.refreshList = refreshList;
        this.entry = entry;
        this.moveUpButton = Button.builder(Component.literal("▲"), btn -> move(-1)).bounds(0, 0, 20, 20).build();
        this.moveDownButton = Button.builder(Component.literal("▼"), btn -> move(1)).bounds(0, 0, 20, 20).build();
    }

    private void move(int direction) {
        List<TooltipEntry> entries = CustomTooltipsConfigRegistry.CONFIG.get().entries;
        int index = entries.indexOf(entry);
        int newIndex = index + direction;
        if (index < 0 || newIndex < 0 || newIndex >= entries.size()) return;

        TooltipListChanges.save(config -> Collections.swap(config.entries, index, newIndex));
        this.refreshList.run();
    }

    //? if >=26.1 {
    /*public void render(GuiGraphicsExtractor context, int x, int y, int mouseX, int mouseY, float tickDelta) {
    *///?} else {
    public void render(GuiGraphics context, int x, int y, int mouseX, int mouseY, float tickDelta) {
    //?}
        TooltipConfig config = CustomTooltipsConfigRegistry.CONFIG.get();
        int idx = config.entries.indexOf(this.entry);

        moveUpButton.setX(x);
        moveUpButton.setY(y);
        moveUpButton.active = (idx > 0);
        //? if >=26.1 {
        /*moveUpButton.extractRenderState(context, mouseX, mouseY, tickDelta);
        *///?} else {
        moveUpButton.render(context, mouseX, mouseY, tickDelta);
        //?}

        moveDownButton.setX(x + 24);
        moveDownButton.setY(y);
        moveDownButton.active = (idx >= 0 && idx < config.entries.size() - 1);
        //? if >=26.1 {
        /*moveDownButton.extractRenderState(context, mouseX, mouseY, tickDelta);
        *///?} else {
        moveDownButton.render(context, mouseX, mouseY, tickDelta);
        //?}
    }

    //? if >=1.21.9 {
    /*public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        return moveUpButton.mouseClicked(event, doubleClick) || moveDownButton.mouseClicked(event, doubleClick);
    }
    *///?} else {
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return moveUpButton.mouseClicked(mouseX, mouseY, button) || moveDownButton.mouseClicked(mouseX, mouseY, button);
    }
    //?}
}