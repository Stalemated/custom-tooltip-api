package com.stalemated.customtooltips.gui;

import com.stalemated.customtooltips.ConfigManager;
import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.config.TooltipConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
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

        this.addDrawableChild(ButtonWidget.builder(getAlignIconsText(), button -> {
            TooltipConfig config = ConfigManager.getConfig();
            config.align_attribute_icons = !config.align_attribute_icons;
            AutoConfig.getConfigHolder(TooltipConfig.class).save();
            button.setMessage(getAlignIconsText()); // Actualiza el texto (ON/OFF) dinámicamente
        }).dimensions(this.width - 135, 6, 125, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("customtooltips.tooltip_list_screen.add_new_tooltip"), button -> {
            TooltipEntry newEntry = new TooltipEntry();
            newEntry.colors = new ArrayList<>(Arrays.asList("gray"));
            newEntry.text = new ArrayList<>(Arrays.asList("Default text"));

            if (this.client != null) {
                this.client.setScreen(TooltipEditScreen.create(this, newEntry, true));
            }
        }).dimensions(this.width / 2 - 155, this.height - 28, 150, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.done"), button -> {
            this.close();
        }).dimensions(this.width / 2 + 5, this.height - 28, 150, 20).build());
    }

    // Helper para generar el texto dinámico "Align Icons: ON" / "Align Icons: OFF"
    private Text getAlignIconsText() {
        boolean isOn = ConfigManager.getConfig().align_attribute_icons;
        return Text.translatable("customtooltips.tooltip_list_screen.align_icons")
                .append(": ")
                .append(isOn ? ScreenTexts.ON : ScreenTexts.OFF);
    }

    @Override
    public void render(net.minecraft.client.gui.DrawContext context, int mouseX, int mouseY, float delta) {
        this.listWidget.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 13, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(this.parent);
        }
    }
}