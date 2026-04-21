package com.stalemated.customtooltips.gui;

import com.stalemated.customtooltips.ConfigManager;
import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.config.TooltipConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import static com.stalemated.customtooltips.CustomTooltipApiClient.openConfigKeybind;

import java.util.ArrayList;
import java.util.Arrays;

public class TooltipListScreen extends Screen {
    private final Screen parent;
    public TooltipListWidget listWidget;
    private TextFieldWidget searchBox;
    private static boolean hasShownKeybindToast = false;

    public TooltipListScreen(Screen parent) {
        super(Text.translatable("customtooltips.tooltip_list_screen.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.listWidget = new TooltipListWidget(this.client, this.width, this.height, 55, this.height - 32, 25);
        this.addSelectableChild(this.listWidget);

        this.searchBox = new TextFieldWidget(this.textRenderer,  this.width / 4, 24, this.width / 2, 20, Text.translatable("customtooltips.tooltip_list_screen.search"));
        this.searchBox.setChangedListener(searchText -> this.listWidget.updateEntries(searchText));
        this.searchBox.setMaxLength(1024);
        this.addSelectableChild(this.searchBox);
        this.setInitialFocus(this.searchBox);

        if (!hasShownKeybindToast) {
            checkAndShowKeybindToast();
            hasShownKeybindToast = true;
        }

        this.addDrawableChild(ButtonWidget.builder(getAlignIconsText(), button -> {
            TooltipConfig config = ConfigManager.getConfig();
            config.align_attribute_icons = !config.align_attribute_icons;
            AutoConfig.getConfigHolder(TooltipConfig.class).save();
            button.setMessage(getAlignIconsText());
        }).dimensions(this.width - 100, 6, 90, 20).build());

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

    private void checkAndShowKeybindToast() {
        if (this.client == null || this.client.options == null) return;

        if (openConfigKeybind.getTranslationKey().equals("key.customtooltips.open_config") && openConfigKeybind.isUnbound()) {
            SystemToast.add(
                    this.client.getToastManager(),
                    SystemToast.Type.TUTORIAL_HINT,
                    Text.translatable("customtooltips.toast.keybind_missing.title"),
                    Text.translatable("customtooltips.toast.keybind_missing.desc")
            );
        }

    }

    private Text getAlignIconsText() {
        boolean isOn = ConfigManager.getConfig().align_attribute_icons;
        return Text.translatable("customtooltips.tooltip_list_screen.align_icons")
                .append(": ")
                .append(isOn ? ScreenTexts.ON : ScreenTexts.OFF);
    }

    @Override
    public void render(net.minecraft.client.gui.DrawContext context, int mouseX, int mouseY, float delta) {
        this.listWidget.render(context, mouseX, mouseY, delta);
        this.searchBox.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void tick() {
        if (this.searchBox != null) {
            this.searchBox.tick();
        }
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(this.parent);
        }
    }
}