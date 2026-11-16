package com.stalemated.customtooltips.gui.factories;

import com.stalemated.customtooltips.config.CustomTooltipsConfigRegistry;
import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.config.TooltipConfig;
import com.stalemated.customtooltips.gui.screen.TooltipEditScreen;
import com.stalemated.customtooltips.gui.screen.TooltipListScreen;

import com.stalemated.customtooltips.util.ToastManager;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import static com.stalemated.customtooltips.CustomTooltips.LOGGER;

public class ListScreenUIFactory {

    private static final int BUTTON_SIZE = 20;
    private static final int SPACING = 4;
    private static final int START_Y = 24;
    private static final int ACTION_BAR_BUTTON_AMOUNT = 5;

    public static EditBox createSearchBox(TooltipListScreen screen, Font textRenderer, String searchText) {
        int screenWidth = screen.width;
        int searchBarWidth = Math.min(3 * screenWidth / 4 - getButtonsWidth() - SPACING, screenWidth / 2);
        int startX = screenWidth / 4;

        EditBox searchBox = new EditBox(textRenderer, startX, START_Y, searchBarWidth, BUTTON_SIZE, Component.translatable("customtooltips.tooltip_list_screen.search"));
        searchBox.setValue(searchText);
        searchBox.setResponder(newSearchText -> {
            screen.setSearchText(newSearchText);
            screen.listWidget.updateEntries(newSearchText);
        });
        searchBox.setMaxLength(1024);
        return searchBox;
    }

    public static List<Button> createActionButtons(TooltipListScreen screen) {
        List<Button> buttons = new ArrayList<>();

        buttons.add(createToggleButton(
            screen, 4,
            () -> CustomTooltipsConfigRegistry.CONFIG.get().align_attribute_icons,
            (config, newVal) -> config.align_attribute_icons = newVal,
            (isOn) -> Component.literal("\uDAC1\uDF24").withStyle(isOn ? ChatFormatting.GREEN : ChatFormatting.RED),
            (isOn) -> Component.translatable("customtooltips.tooltip_list_screen.align_icons")
                    .append("\n")
                    .append(getOnOffText(isOn))
        ));

        buttons.add(createToggleButton(
            screen, 3,
            () -> CustomTooltipsConfigRegistry.CONFIG.get().enable_double_click_selection,
            (config, newVal) -> config.enable_double_click_selection = newVal,
            (isOn) -> Component.literal("\uDAC1\uDF23").withStyle(isOn ? ChatFormatting.GREEN : ChatFormatting.RED),
            (isOn) -> Component.translatable("customtooltips.tooltip_list_screen.double_click_selection")
                    .append("\n")
                    .append(getOnOffText(isOn))
        ));

        buttons.add(Button.builder(getSortIcon(), button -> {
            CustomTooltipsConfigRegistry.CONFIG.modify(config -> config.sort_mode = config.sort_mode.next());

            button.setMessage(getSortIcon());
            button.setTooltip(Tooltip.create(getSortTooltip()));
            screen.listWidget.updateEntries(screen.searchBox.getValue());
        })
                .bounds(getButtonStartX(2, screen.width), START_Y, BUTTON_SIZE, BUTTON_SIZE)
                .tooltip(Tooltip.create(getSortTooltip()))
                .build());

        buttons.add(Button.builder(getApiEntriesIcon(), button -> {
            TooltipListScreen.showApiEntries = !TooltipListScreen.showApiEntries;

            button.setMessage(getApiEntriesIcon());
            button.setTooltip(Tooltip.create(getApiEntriesTooltip()));
            screen.listWidget.updateEntries(screen.searchBox.getValue());
        })
                .bounds(getButtonStartX(1, screen.width), START_Y, BUTTON_SIZE, BUTTON_SIZE)
                .tooltip(Tooltip.create(getApiEntriesTooltip()))
                .build());

        buttons.add(Button.builder(getPasteIcon(), button -> {
            try {
                String clipboard = Minecraft.getInstance().keyboardHandler.getClipboard();
                TooltipEntry pasted = CustomTooltipsConfigRegistry.CONFIG.fromJson(clipboard, TooltipEntry.class);

                if (pasted != null && pasted.target != null) {
                    pasted.uuid = UUID.randomUUID();
                    pasted.apiEntry = false;
                    pasted.apiEntryId = "";

                    CustomTooltipsConfigRegistry.CONFIG.modify(config -> config.entries.add(pasted));
                    screen.listWidget.updateEntries(screen.searchBox.getValue());

                    ToastManager.showPastedToast(pasted.target);
                }
            } catch (Exception e) {
                LOGGER.error("Error pasting a new entry from clipboard", e);
            }
        })
                .bounds(getButtonStartX(5, screen.width), START_Y, BUTTON_SIZE, BUTTON_SIZE)
                .tooltip(Tooltip.create(Component.translatable("customtooltips.tooltip_list_screen.paste_button")))
                .build());

        return buttons;
    }

    public static List<Button> createFooterButtons(TooltipListScreen screen) {
        List<Button> buttons = new ArrayList<>();
        int btnWidth = 150;
        int btnY = screen.height - 28;

        buttons.add(Button.builder(Component.translatable("customtooltips.tooltip_list_screen.add_new_tooltip"),
                        button -> Minecraft.getInstance().setScreen(TooltipEditScreen.add(screen)))
                .bounds(screen.width / 2 - 155, btnY, btnWidth, BUTTON_SIZE)
                .build());

        buttons.add(Button.builder(Component.translatable("gui.done"), button -> screen.onClose())
                .bounds(screen.width / 2 + 5, btnY, btnWidth, BUTTON_SIZE)
                .build());

        return buttons;
    }

    private static Button createToggleButton(Screen screen, int index, Supplier<Boolean> getter, BiConsumer<TooltipConfig, Boolean> setter, Function<Boolean, Component> icon, Function<Boolean, Component> tooltip) {
        boolean initialValue = getter.get();
        return Button.builder(icon.apply(initialValue), button -> {
            boolean newValue = !getter.get();
            CustomTooltipsConfigRegistry.CONFIG.modify(config -> setter.accept(config, newValue));
            button.setMessage(icon.apply(newValue));
            button.setTooltip(Tooltip.create(tooltip.apply(newValue)));
        })
                .bounds(getButtonStartX(index, screen.width), START_Y, BUTTON_SIZE, BUTTON_SIZE)
                .tooltip(Tooltip.create(tooltip.apply(initialValue)))
                .build();
    }

    private static int getButtonStartX(int buttonIndex, int screenWidth) {
        return screenWidth - (SPACING * buttonIndex) - (BUTTON_SIZE * buttonIndex);
    }

    private static int getButtonsWidth() {
        return ACTION_BAR_BUTTON_AMOUNT * BUTTON_SIZE + ACTION_BAR_BUTTON_AMOUNT * SPACING;
    }

    private static Component getOnOffText(boolean isOn) {
        return Component.translatable(isOn ? "options.on" : "options.off").withStyle(isOn ? ChatFormatting.GREEN : ChatFormatting.RED);
    }

    private static Component getSortIcon() {
        return switch (CustomTooltipsConfigRegistry.CONFIG.get().sort_mode) {
            case NAME_AND_TAG -> Component.literal("AZ").withStyle(ChatFormatting.GOLD);
            case DISABLED_FIRST -> Component.literal("\uDAC1\uDF26").withStyle(ChatFormatting.RED);
            default -> Component.literal("\uDAC1\uDF25").withStyle(ChatFormatting.GOLD);
        };
    }

    private static Component getSortTooltip() {
        Component modeText = switch (CustomTooltipsConfigRegistry.CONFIG.get().sort_mode) {
            case NAME_AND_TAG -> Component.translatable("customtooltips.tooltip_list_screen.sort_name");
            case DISABLED_FIRST -> Component.translatable("customtooltips.tooltip_list_screen.sort_disabled");
            default -> Component.translatable("customtooltips.tooltip_list_screen.sort_date");
        };
        return Component.translatable("customtooltips.tooltip_list_screen.sort_order").append("\n").append(modeText.copy().withStyle(ChatFormatting.GRAY));
    }

    private static Component getApiEntriesIcon() {
        return Component.literal("\uDAC1\uDF31").withStyle(TooltipListScreen.showApiEntries ? ChatFormatting.GREEN : ChatFormatting.GRAY);
    }

    private static Component getApiEntriesTooltip() {
        return Component.translatable("customtooltips.tooltip_list_screen.toggle_api_entries")
                .append("\n")
                .append(Component.translatable(TooltipListScreen.showApiEntries ? "customtooltips.tooltip_list_screen.showing_api_entries" : "customtooltips.tooltip_list_screen.showing_normal")
                        .withStyle(ChatFormatting.GRAY)
                );
    }

    private static Component getPasteIcon() {
        return Component.literal("\uDAC1\uDF33");
    }

    private static Component getDimensionsScreenIcon() {
        return Component.literal("\uDAC1\uDF34");
    }
}