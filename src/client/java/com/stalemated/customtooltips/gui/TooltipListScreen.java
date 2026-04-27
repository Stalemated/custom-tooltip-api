package com.stalemated.customtooltips.gui;

import com.stalemated.customtooltips.ConfigManager;
import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.config.TooltipConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import static com.stalemated.customtooltips.CustomTooltipApiClient.openConfigKeybind;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TooltipListScreen extends Screen {
    private final Screen parent;
    public TooltipListWidget listWidget;
    TextFieldWidget searchBox;
    private static boolean hasShownKeybindToast = false;
    private String searchText = "";
    private List<Text> activeTooltip = null;
    public static boolean showApiEntries = false;

    public TooltipListScreen(Screen parent) {
        super(Text.translatable("customtooltips.tooltip_list_screen.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.listWidget = new TooltipListWidget(this.client, this.width, this.height, 55, this.height - 32, 25, this);
        this.addSelectableChild(this.listWidget);

        int buttonAmount = 4;
        int actionBarHeight = 20;
        int buttonSize = 20;
        int spacing = 8;
        int startX = this.width / 4;
        int startY = 24;

        this.searchBox = new TextFieldWidget(this.textRenderer, startX, startY, getSearchBarWidth(buttonAmount, buttonSize, spacing), actionBarHeight, Text.translatable("customtooltips.tooltip_list_screen.search"));
        this.searchBox.setText(this.searchText);
        this.searchBox.setChangedListener(newSearchText -> {
            this.searchText = newSearchText;
            this.listWidget.updateEntries(newSearchText);
        });
        this.searchBox.setMaxLength(1024);
        this.addSelectableChild(this.searchBox);
        this.setInitialFocus(this.searchBox);

        this.listWidget.updateEntries(this.searchText);

        if (!hasShownKeybindToast) {
            checkAndShowKeybindToast();
            hasShownKeybindToast = true;
        }

        if (ConfigManager.configLoadFailed) {
            checkAndShowBrokenConfigToast();
            ConfigManager.configLoadFailed = false;
        }

        this.addDrawableChild(ButtonWidget.builder(getAlignIconsIcon(), button -> {
                    TooltipConfig config = ConfigManager.getConfig();
                    config.align_attribute_icons = !config.align_attribute_icons;
                    ConfigManager.save();
                    button.setMessage(getAlignIconsIcon());
                    button.setTooltip(Tooltip.of(getAlignIconsTooltip()));
                }).dimensions(getButtonStartX(4, buttonSize, spacing), startY, buttonSize, buttonSize)
                .tooltip(Tooltip.of(getAlignIconsTooltip()))
                .build());

        this.addDrawableChild(ButtonWidget.builder(getDoubleClickIcon(), button -> {
                    TooltipConfig config = ConfigManager.getConfig();
                    config.enable_double_click_selection = !config.enable_double_click_selection;
                    ConfigManager.save();
                    button.setMessage(getDoubleClickIcon());
                    button.setTooltip(Tooltip.of(getDoubleClickTooltip()));
                }).dimensions(getButtonStartX(3, buttonSize, spacing), startY, buttonSize, buttonSize)
                .tooltip(Tooltip.of(getDoubleClickTooltip()))
                .build());

        this.addDrawableChild(ButtonWidget.builder(getSortIcon(), button -> {
                    TooltipConfig config = ConfigManager.getConfig();
                    config.sort_by_name = !config.sort_by_name;
                    ConfigManager.save();
                    button.setMessage(getSortIcon());
                    button.setTooltip(Tooltip.of(getSortTooltip()));
                    this.listWidget.updateEntries(this.searchText);
                }).dimensions(getButtonStartX(2, buttonSize, spacing), startY, buttonSize, buttonSize)
                .tooltip(Tooltip.of(getSortTooltip()))
                .build());

        this.addDrawableChild(ButtonWidget.builder(getApiEntriesIcon(), button -> {
                    showApiEntries = !showApiEntries;
                    button.setMessage(getApiEntriesIcon());
                    button.setTooltip(Tooltip.of(getApiEntriesTooltip()));
                    this.listWidget.updateEntries(this.searchText);
                }).dimensions(getButtonStartX(1, buttonSize, spacing), startY, buttonSize, buttonSize)
                .tooltip(Tooltip.of(getApiEntriesTooltip()))
                .build());

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("customtooltips.tooltip_list_screen.add_new_tooltip"), button -> {
            TooltipEntry newEntry = new TooltipEntry();
            newEntry.colors = new ArrayList<>(Arrays.asList("#FFFFFF"));
            newEntry.text = new ArrayList<>(Arrays.asList("Default text"));

            if (this.client != null) {
                this.client.setScreen(TooltipEditScreen.create(this, newEntry, true));
            }
        }).dimensions(this.width / 2 - 155, this.height - 28, 150, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.done"), button -> {
            this.close();
        }).dimensions(this.width / 2 + 5, this.height - 28, 150, 20).build());
    }

    // Helpers

    public void setHoveredTooltip(List<Text> tooltip) {
        this.activeTooltip = tooltip;
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

    private void checkAndShowBrokenConfigToast() {
        if (this.client == null) return;
        SystemToast.add(
                this.client.getToastManager(),
                SystemToast.Type.PACK_LOAD_FAILURE,
                Text.translatable("customtooltips.toast.config_backup.title"),
                Text.translatable("customtooltips.toast.config_backup.desc")
        );
    }

    private int getButtonStartX(int buttonIndex, int buttonSize, int spacing) {
        return this.width - spacing * buttonIndex - buttonSize * buttonIndex;
    }

    private int getButtonsWidth(int buttonAmount, int buttonSize, int spacing) {
        return buttonAmount * buttonSize + buttonAmount * spacing;
    }

    private int getSearchBarWidth(int buttonAmount, int buttonSize, int spacing) {
        return Math.min(3 * this.width / 4 - getButtonsWidth(buttonAmount, buttonSize, spacing) - spacing, this.width / 2);
    }

    // Getters

    private Text getAlignIconsIcon() {
        boolean isOn = ConfigManager.getConfig().align_attribute_icons;
        return Text.literal("\uDAC1\uDF24").formatted(isOn ? Formatting.GREEN : Formatting.RED);

    }

    private Text getAlignIconsTooltip() {
        boolean isOn = ConfigManager.getConfig().align_attribute_icons;
        return Text.translatable("customtooltips.tooltip_list_screen.align_icons")
                .append(Text.literal("\n"))
                .append(Text.translatable(isOn ? "options.on" : "options.off").formatted(isOn ? Formatting.GREEN : Formatting.RED));
    }

    private Text getDoubleClickIcon() {
        boolean isOn = ConfigManager.getConfig().enable_double_click_selection;
        return Text.literal("\uDAC1\uDF23").formatted(isOn ? Formatting.GREEN : Formatting.RED);
    }

    private Text getDoubleClickTooltip() {
        boolean isOn = ConfigManager.getConfig().enable_double_click_selection;
        return Text.translatable("customtooltips.tooltip_list_screen.double_click_selection")
                .append(Text.literal("\n"))
                .append(Text.translatable(isOn ? "options.on" : "options.off").formatted(isOn ? Formatting.GREEN : Formatting.RED));
    }

    private Text getSortIcon() {
        boolean isOn = ConfigManager.getConfig().sort_by_name;
        return isOn ? Text.literal("AZ").formatted(Formatting.GOLD) : Text.literal("\uDAC1\uDF25").formatted(Formatting.GOLD);
    }

    private Text getSortTooltip() {
        boolean isOn = ConfigManager.getConfig().sort_by_name;
        return Text.translatable("customtooltips.tooltip_list_screen.sort_order")
                .append(Text.literal("\n"))
                .append(Text.translatable(isOn ? "customtooltips.tooltip_list_screen.sort_name" : "customtooltips.tooltip_list_screen.sort_date").formatted(Formatting.GRAY));
    }

    private Text getApiEntriesIcon() {
        return Text.literal("</>").formatted(showApiEntries ? Formatting.GREEN : Formatting.GRAY);
    }

    private Text getApiEntriesTooltip() {
        return Text.translatable("customtooltips.tooltip_list_screen.toggle_api_entries")
                .append(Text.literal("\n"))
                .append(Text.translatable(showApiEntries ? "customtooltips.tooltip_list_screen.showing_api_entries" : "customtooltips.tooltip_list_screen.showing_normal").formatted(Formatting.GRAY));
    }

    // Overrides

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.activeTooltip = null;
        this.listWidget.render(context, mouseX, mouseY, delta);
        this.searchBox.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
        
        if (this.activeTooltip != null) {
            context.drawTooltip(this.textRenderer, this.activeTooltip, mouseX, mouseY);
        }
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