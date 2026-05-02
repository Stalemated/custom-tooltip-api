package com.stalemated.customtooltips.gui.widget;

import com.stalemated.customtooltips.ConfigManager;
import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.config.TooltipConfig;
import com.stalemated.customtooltips.gui.TooltipEditScreen;
import com.stalemated.customtooltips.gui.TooltipListWidget;
import com.stalemated.customtooltips.util.ToastManager;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

public class RowActionsWidget {
    private final List<ButtonWidget> buttons = new ArrayList<>();
    private final List<ButtonWidget> apiButtons = new ArrayList<>();

    private final int BUTTON_STEP = 24;

    public RowActionsWidget(TooltipListWidget parent, TooltipEntry entry) {
        MinecraftClient client = MinecraftClient.getInstance();
        String identifier = entry.getIdentifier();
        boolean isDisabled = ConfigManager.getConfig().disabled_entries.contains(identifier);

        buttons.add(ButtonWidget.builder(Text.literal("\uDAC1\uDF27").formatted(Formatting.RED), button -> {
            Screen currentScreen = client.currentScreen;
            client.setScreen(new ConfirmScreen(
                    (confirmed) -> {
                        if (confirmed) {
                            ConfigManager.getConfig().entries.remove(entry);
                            ConfigManager.save();
                            parent.updateEntries(parent.parentScreen.searchBox.getText());
                        }
                        client.setScreen(currentScreen);
                    },
                    Text.translatable("customtooltips.tooltip_list_widget.delete_confirm.title"),
                    Text.translatable("customtooltips.tooltip_list_widget.delete_confirm.message", entry.target.isEmpty() ? "New Tooltip" : entry.target)
            ));
        })
                .tooltip(Tooltip.of(Text.translatable("customtooltips.tooltip_list_widget.delete_button")))
                .build());

        buttons.add(ButtonWidget.builder(Text.literal("\uDAC1\uDF28"), btn -> client.setScreen(TooltipEditScreen.create(client.currentScreen, entry, false)))
                .tooltip(Tooltip.of(Text.translatable("customtooltips.tooltip_list_widget.edit_button")))
                .build());

        apiButtons.add(ButtonWidget.builder(Text.literal("\uDAC1\uDF29"), btn -> {
            TooltipEntry newEntry = TooltipEntry.builder(entry.target)
                    .text(entry.text)
                    .style(entry.style)
                    .colors(entry.colors)
                    .bold(entry.bold)
                    .italic(entry.italic)
                    .underlined(entry.underlined)
                    .strikethrough(entry.strikethrough)
                    .obfuscated(entry.obfuscated)
                    .requireShift(entry.require_shift)
                    .emptyLineBefore(entry.empty_line_before)
                    .position(entry.position)
                    .lineOffset(entry.lineOffset)
                    .animationOffset(entry.animation_offset)
                    .tickrate(entry.tickrate)
                    .font(entry.font)
                    .build();

            newEntry.apiEntry = false;
            newEntry.apiEntryId = "";

            ConfigManager.getConfig().entries.add(newEntry);
            ConfigManager.save();

            ToastManager.showDuplicatedToast(entry.target.isEmpty() ? "New Tooltip" : entry.target);
            parent.updateEntries(parent.parentScreen.searchBox.getText());
        })
                .tooltip(Tooltip.of(Text.translatable("customtooltips.tooltip_list_widget.duplicate_button")))
                .build());

        apiButtons.add(ButtonWidget.builder(getDisabledEntryIcon(isDisabled), btn -> {
            TooltipConfig config = ConfigManager.getConfig();
            if (isDisabled) config.disabled_entries.remove(identifier);
            else config.disabled_entries.add(identifier);

            ConfigManager.save();
            parent.updateEntries(parent.parentScreen.searchBox.getText());
        })
                .tooltip(getDisabledEntryTooltip(isDisabled))
                .build());

        buttons.addAll(apiButtons);
    }

    public void render(DrawContext context, int x, int y, int entryWidth, int mouseX, int mouseY, float tickDelta, boolean isApiEntry) {
        int currentX = x + entryWidth;

        List<ButtonWidget> visibleButtons = isApiEntry ? apiButtons : buttons;

        for (ButtonWidget button : visibleButtons) {
            currentX -= BUTTON_STEP;
            button.setX(currentX);
            button.setY(y);
            button.setWidth(20);
            button.render(context, mouseX, mouseY, tickDelta);
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (ButtonWidget btn : buttons) {
            if (btn.mouseClicked(mouseX, mouseY, button)) return true;
        }
        return false;
    }

    public int getWidth() {
        int btnAmount = 4;
        return (buttons.size() * BUTTON_STEP) - btnAmount;
    }

    private Text getDisabledEntryIcon(boolean isDisabled) {
        return isDisabled ? Text.literal("\uDAC1\uDF26").formatted(Formatting.RED) : Text.literal("\uDAC1\uDF30").formatted(Formatting.GREEN);
    }

    private Tooltip getDisabledEntryTooltip(boolean isDisabled) {
        return Tooltip.of(Text.translatable(isDisabled ? "customtooltips.tooltip_list_widget.disabled_button" : "customtooltips.tooltip_list_widget.enabled_button"));
    }
}