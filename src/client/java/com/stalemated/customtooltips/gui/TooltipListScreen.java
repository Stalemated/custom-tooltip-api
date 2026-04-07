package com.stalemated.customtooltips.gui;

import com.stalemated.customtooltips.TooltipEntry;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Arrays;

public class TooltipListScreen extends Screen {
    private final Screen parent;
    public TooltipListWidget listWidget;

    public TooltipListScreen(Screen parent) {
        super(Text.translatable("customtooltips.tooltip_list_screen.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.listWidget = new TooltipListWidget(this.client, this.width, this.height, 32, this.height - 32, 25);
        this.addSelectableChild(this.listWidget);

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("customtooltips.tooltip_list_screen.add_new_tooltip"), button -> {
            TooltipEntry newEntry = new TooltipEntry();
            newEntry.colors = new ArrayList<>(Arrays.asList("gray"));
            newEntry.text = new ArrayList<>(Arrays.asList("Default text"));

            if (this.client != null) {
                this.client.setScreen(TooltipEditScreen.create(this, newEntry, true));
            }
        }).dimensions(this.width / 2 - 100, this.height - 28, 200, 20).build());
    }

    @Override
    public void render(net.minecraft.client.gui.DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.listWidget.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 13, 0xFFFFFF);
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(this.parent);
        }
    }
}
