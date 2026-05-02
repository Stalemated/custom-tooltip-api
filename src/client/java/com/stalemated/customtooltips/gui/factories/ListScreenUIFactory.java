package com.stalemated.customtooltips.gui.factories;

import com.stalemated.customtooltips.ConfigManager;
import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.config.TooltipConfig;
import com.stalemated.customtooltips.gui.TooltipEditScreen;
import com.stalemated.customtooltips.gui.TooltipListScreen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ListScreenUIFactory {

    private static final int BUTTON_SIZE = 20;
    private static final int SPACING = 4;
    private static final int START_Y = 24;
    private static final int ACTION_BAR_BUTTON_AMOUNT = 4;

    public static TextFieldWidget createSearchBox(TooltipListScreen screen, TextRenderer textRenderer, String searchText) {
        int screenWidth = screen.width;
        int searchBarWidth = Math.min(3 * screenWidth / 4 - getButtonsWidth() - SPACING, screenWidth / 2);
        int startX = screenWidth / 4;

        TextFieldWidget searchBox = new TextFieldWidget(textRenderer, startX, START_Y, searchBarWidth, BUTTON_SIZE, Text.translatable("customtooltips.tooltip_list_screen.search"));
        searchBox.setText(searchText);
        searchBox.setChangedListener(newSearchText -> {
            screen.setSearchText(newSearchText);
            screen.listWidget.updateEntries(newSearchText);
        });
        searchBox.setMaxLength(1024);
        return searchBox;
    }

    public static List<ButtonWidget> createActionButtons(TooltipListScreen screen) {
        List<ButtonWidget> buttons = new ArrayList<>();

        buttons.add(createToggleButton(
            screen, 4,
            () -> ConfigManager.getConfig().align_attribute_icons,
            (config, newVal) -> config.align_attribute_icons = newVal,
            (isOn) -> Text.literal("\uDAC1\uDF24").formatted(isOn ? Formatting.GREEN : Formatting.RED),
            (isOn) -> Text.translatable("customtooltips.tooltip_list_screen.align_icons")
                    .append("\n")
                    .append(getOnOffText(isOn))
        ));

        buttons.add(createToggleButton(
            screen, 3,
            () -> ConfigManager.getConfig().enable_double_click_selection,
            (config, newVal) -> config.enable_double_click_selection = newVal,
            (isOn) -> Text.literal("\uDAC1\uDF23").formatted(isOn ? Formatting.GREEN : Formatting.RED),
            (isOn) -> Text.translatable("customtooltips.tooltip_list_screen.double_click_selection")
                    .append("\n")
                    .append(getOnOffText(isOn))
        ));

        buttons.add(ButtonWidget.builder(getSortIcon(), button -> {
            TooltipConfig config = ConfigManager.getConfig();
            config.sort_mode = config.sort_mode.next();
            ConfigManager.save();

            button.setMessage(getSortIcon());
            button.setTooltip(Tooltip.of(getSortTooltip()));
            screen.listWidget.updateEntries(screen.searchBox.getText());
        })
                .dimensions(getButtonStartX(2, screen.width), START_Y, BUTTON_SIZE, BUTTON_SIZE)
                .tooltip(Tooltip.of(getSortTooltip()))
                .build());

        buttons.add(ButtonWidget.builder(getApiEntriesIcon(), button -> {
            TooltipListScreen.showApiEntries = !TooltipListScreen.showApiEntries;

            button.setMessage(getApiEntriesIcon());
            button.setTooltip(Tooltip.of(getApiEntriesTooltip()));
            screen.listWidget.updateEntries(screen.searchBox.getText());
        })
                .dimensions(getButtonStartX(1, screen.width), START_Y, BUTTON_SIZE, BUTTON_SIZE)
                .tooltip(Tooltip.of(getApiEntriesTooltip()))
                .build());

        return buttons;
    }

    public static List<ButtonWidget> createFooterButtons(TooltipListScreen screen) {
        List<ButtonWidget> buttons = new ArrayList<>();
        int btnWidth = 150;
        int btnY = screen.height - 28;

        buttons.add(ButtonWidget.builder(Text.translatable("customtooltips.tooltip_list_screen.add_new_tooltip"), button -> {
            TooltipEntry newEntry = TooltipEntry.builder("")
                    .addLine("Default Text")
                    .colors("white")
                    .build();

            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null) {
                client.setScreen(TooltipEditScreen.create(screen, newEntry, true));
            }
        })
                .dimensions(screen.width / 2 - 155, btnY, btnWidth, BUTTON_SIZE)
                .build());

        buttons.add(ButtonWidget.builder(Text.translatable("gui.done"), button -> screen.close())
                .dimensions(screen.width / 2 + 5, btnY, btnWidth, BUTTON_SIZE)
                .build());

        return buttons;
    }

    private static ButtonWidget createToggleButton(Screen screen, int index, Supplier<Boolean> getter, BiConsumer<TooltipConfig, Boolean> setter, Function<Boolean, Text> icon, Function<Boolean, Text> tooltip) {
        boolean initialValue = getter.get();
        return ButtonWidget.builder(icon.apply(initialValue), button -> {
            boolean newValue = !getter.get();
            setter.accept(ConfigManager.getConfig(), newValue);
            ConfigManager.save();
            button.setMessage(icon.apply(newValue));
            button.setTooltip(Tooltip.of(tooltip.apply(newValue)));
        })
                .dimensions(getButtonStartX(index, screen.width), START_Y, BUTTON_SIZE, BUTTON_SIZE)
                .tooltip(Tooltip.of(tooltip.apply(initialValue)))
                .build();
    }

    private static int getButtonStartX(int buttonIndex, int screenWidth) {
        return screenWidth - (SPACING * buttonIndex) - (BUTTON_SIZE * buttonIndex);
    }

    private static int getButtonsWidth() {
        return ACTION_BAR_BUTTON_AMOUNT * BUTTON_SIZE + ACTION_BAR_BUTTON_AMOUNT * SPACING;
    }

    private static Text getOnOffText(boolean isOn) {
        return Text.translatable(isOn ? "options.on" : "options.off").formatted(isOn ? Formatting.GREEN : Formatting.RED);
    }

    private static Text getSortIcon() {
        return switch (ConfigManager.getConfig().sort_mode) {
            case NAME_AND_TAG -> Text.literal("AZ").formatted(Formatting.GOLD);
            case DISABLED_FIRST -> Text.literal("\uDAC1\uDF26").formatted(Formatting.RED);
            default -> Text.literal("\uDAC1\uDF25").formatted(Formatting.GOLD);
        };
    }

    private static Text getSortTooltip() {
        Text modeText = switch (ConfigManager.getConfig().sort_mode) {
            case NAME_AND_TAG -> Text.translatable("customtooltips.tooltip_list_screen.sort_name");
            case DISABLED_FIRST -> Text.translatable("customtooltips.tooltip_list_screen.sort_disabled");
            default -> Text.translatable("customtooltips.tooltip_list_screen.sort_date");
        };
        return Text.translatable("customtooltips.tooltip_list_screen.sort_order").append("\n").append(modeText.copy().formatted(Formatting.GRAY));
    }

    private static Text getApiEntriesIcon() {
        return Text.literal("</>").formatted(TooltipListScreen.showApiEntries ? Formatting.GREEN : Formatting.GRAY);
    }

    private static Text getApiEntriesTooltip() {
        return Text.translatable("customtooltips.tooltip_list_screen.toggle_api_entries")
                .append("\n")
                .append(Text.translatable(TooltipListScreen.showApiEntries ? "customtooltips.tooltip_list_screen.showing_api_entries" : "customtooltips.tooltip_list_screen.showing_normal")
                        .formatted(Formatting.GRAY)
                );
    }
}