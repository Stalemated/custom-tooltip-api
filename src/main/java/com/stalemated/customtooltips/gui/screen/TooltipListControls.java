package com.stalemated.customtooltips.gui.screen;

import com.stalemated.customtooltips.config.CustomTooltipsConfigRegistry;
import com.stalemated.customtooltips.config.TooltipConfig;
import com.stalemated.customtooltips.gui.EditButtons;
import com.stalemated.customtooltips.gui.GuiIcon;
import com.stalemated.customtooltips.gui.TargetName;
import com.stalemated.customtooltips.gui.TooltipClipboard;
import com.stalemated.customtooltips.gui.TooltipListChanges;
import com.stalemated.customtooltips.gui.TooltipListToasts;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

final class TooltipListControls {

    private static final int BUTTON_SIZE = 20;
    private static final int SPACING = 4;
    private static final int START_Y = 24;
    private static final int ACTION_BAR_BUTTON_AMOUNT = 5;

    private TooltipListControls() {
    }

    static EditBox createSearchBox(TooltipListScreen screen, Font font, String searchText) {
        int screenWidth = screen.width;
        int searchBarWidth = Math.min(3 * screenWidth / 4 - getButtonsWidth() - SPACING, screenWidth / 2);
        int startX = screenWidth / 4;

        EditBox searchBox = new EditBox(font, startX, START_Y, searchBarWidth, BUTTON_SIZE, Component.translatable("customtooltips.tooltip_list_screen.search"));
        searchBox.setValue(searchText);
        searchBox.setResponder(screen::search);
        searchBox.setMaxLength(1024);
        return searchBox;
    }

    static List<Button> createActionButtons(TooltipListScreen screen) {
        List<Button> buttons = new ArrayList<>();

        buttons.add(createToggleButton(
            screen, 4,
            () -> CustomTooltipsConfigRegistry.CONFIG.get().align_attribute_icons,
            (config, newVal) -> config.align_attribute_icons = newVal,
            (isOn) -> GuiIcon.ALIGN_ICONS.text().withStyle(isOn ? ChatFormatting.GREEN : ChatFormatting.RED),
            (isOn) -> Component.translatable("customtooltips.tooltip_list_screen.align_icons")
                    .append("\n")
                    .append(getOnOffText(isOn))
        ));

        buttons.add(createToggleButton(
            screen, 3,
            () -> CustomTooltipsConfigRegistry.CONFIG.get().enable_double_click_selection,
            (config, newVal) -> config.enable_double_click_selection = newVal,
            (isOn) -> GuiIcon.DOUBLE_CLICK.text().withStyle(isOn ? ChatFormatting.GREEN : ChatFormatting.RED),
            (isOn) -> Component.translatable("customtooltips.tooltip_list_screen.double_click_selection")
                    .append("\n")
                    .append(getOnOffText(isOn))
        ));

        buttons.add(iconButton(screen, 2, getSortIcon(), getSortTooltip(), button -> {
            TooltipListChanges.save(config -> config.sort_mode = config.sort_mode.next());

            button.setMessage(getSortIcon());
            button.setTooltip(Tooltip.create(getSortTooltip()));
            screen.refreshList();
        }));

        buttons.add(iconButton(screen, 1, getApiEntriesIcon(), getApiEntriesTooltip(), button -> {
            TooltipListScreen.toggleApiEntries();

            button.setMessage(getApiEntriesIcon());
            button.setTooltip(Tooltip.create(getApiEntriesTooltip()));
            screen.refreshList();
        }));

        buttons.add(iconButton(screen, 5, GuiIcon.PASTE.text(), Component.translatable("customtooltips.tooltip_list_screen.paste_button"), button ->
                TooltipClipboard.paste().ifPresent(pasted -> {
                    TooltipListChanges.save(config -> config.entries.add(pasted));
                    screen.refreshList();

                    TooltipListToasts.showPastedToast(TargetName.of(pasted));
                })));

        return buttons;
    }

    static List<Button> createFooterButtons(TooltipListScreen screen) {
        List<Button> buttons = new ArrayList<>();
        int btnWidth = 150;
        int btnY = screen.height - 28;

        buttons.add(EditButtons.requireYacl(Button.builder(Component.translatable("customtooltips.tooltip_list_screen.add_new_tooltip"),
                        //? if >=26.2 {
                        /*button -> Minecraft.getInstance().gui.setScreen(TooltipEditScreen.add(screen)))
                        *///?} else {
                        button -> Minecraft.getInstance().setScreen(TooltipEditScreen.add(screen)))
                        //?}
                .bounds(screen.width / 2 - 155, btnY, btnWidth, BUTTON_SIZE)
                .build()));

        buttons.add(Button.builder(Component.translatable("gui.done"), button -> screen.onClose())
                .bounds(screen.width / 2 + 5, btnY, btnWidth, BUTTON_SIZE)
                .build());

        return buttons;
    }

    private static Button createToggleButton(TooltipListScreen screen, int slot, Supplier<Boolean> getter, BiConsumer<TooltipConfig, Boolean> setter, Function<Boolean, Component> icon, Function<Boolean, Component> tooltip) {
        boolean initialValue = getter.get();
        return iconButton(screen, slot, icon.apply(initialValue), tooltip.apply(initialValue), button -> {
            boolean newValue = !getter.get();
            TooltipListChanges.save(config -> setter.accept(config, newValue));
            button.setMessage(icon.apply(newValue));
            button.setTooltip(Tooltip.create(tooltip.apply(newValue)));
        });
    }

    private static Button iconButton(TooltipListScreen screen, int slot, Component icon, Component tooltip, Button.OnPress onPress) {
        return Button.builder(icon, onPress)
                .bounds(getButtonStartX(slot, screen.width), START_Y, BUTTON_SIZE, BUTTON_SIZE)
                .tooltip(Tooltip.create(tooltip))
                .build();
    }

    private static int getButtonStartX(int slot, int screenWidth) {
        return screenWidth - (SPACING * slot) - (BUTTON_SIZE * slot);
    }

    private static int getButtonsWidth() {
        return ACTION_BAR_BUTTON_AMOUNT * BUTTON_SIZE + ACTION_BAR_BUTTON_AMOUNT * SPACING;
    }

    private static Component getOnOffText(boolean isOn) {
        return Component.translatable(isOn ? "options.on" : "options.off").withStyle(isOn ? ChatFormatting.GREEN : ChatFormatting.RED);
    }

    private static Component getSortIcon() {
        return switch (CustomTooltipsConfigRegistry.CONFIG.get().sort_mode) {
            case MANUAL_ORDER -> GuiIcon.CLOCK.text().withStyle(ChatFormatting.GOLD);
            case NAME_AND_TAG -> Component.literal("AZ").withStyle(ChatFormatting.GOLD);
            case DISABLED_FIRST -> GuiIcon.DISABLED.text().withStyle(ChatFormatting.RED);
        };
    }

    private static Component getSortTooltip() {
        Component modeText = switch (CustomTooltipsConfigRegistry.CONFIG.get().sort_mode) {
            case MANUAL_ORDER -> Component.translatable("customtooltips.tooltip_list_screen.sort_manual");
            case NAME_AND_TAG -> Component.translatable("customtooltips.tooltip_list_screen.sort_name");
            case DISABLED_FIRST -> Component.translatable("customtooltips.tooltip_list_screen.sort_disabled");
        };
        return Component.translatable("customtooltips.tooltip_list_screen.sort_order").append("\n").append(modeText.copy().withStyle(ChatFormatting.GRAY));
    }

    private static Component getApiEntriesIcon() {
        return GuiIcon.CODE.text().withStyle(TooltipListScreen.showsApiEntries() ? ChatFormatting.GREEN : ChatFormatting.GRAY);
    }

    private static Component getApiEntriesTooltip() {
        return Component.translatable("customtooltips.tooltip_list_screen.toggle_api_entries")
                .append("\n")
                .append(Component.translatable(TooltipListScreen.showsApiEntries() ? "customtooltips.tooltip_list_screen.showing_api_entries" : "customtooltips.tooltip_list_screen.showing_normal")
                        .withStyle(ChatFormatting.GRAY)
                );
    }
}
