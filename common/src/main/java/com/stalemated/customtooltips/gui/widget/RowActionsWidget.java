package com.stalemated.customtooltips.gui.widget;

import com.stalemated.customtooltips.ConfigManager;
import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.config.TooltipConfig;
import com.stalemated.customtooltips.gui.screen.TooltipEditScreen;
import com.stalemated.customtooltips.util.ToastManager;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
                            TooltipConfig config = ConfigManager.getConfig();
                            config.entries.remove(entry);
                            if (isDisabled) config.disabled_entries.remove(identifier);
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

        apiButtons.add(ButtonWidget.builder(getCopyIcon(), btn -> {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            client.keyboard.setClipboard(gson.toJson(entry));
            
            ToastManager.showCopiedToast(entry.target);
        })
                .tooltip(Tooltip.of(Text.translatable("customtooltips.tooltip_list_widget.copy_button")))
                .build());

        apiButtons.add(ButtonWidget.builder(Text.literal("\uDAC1\uDF29"), btn -> {
            TooltipEntry newEntry = entry.copy();

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
        int btnAmount = buttons.size();
        return (buttons.size() * BUTTON_STEP) - btnAmount;
    }

    private Text getDisabledEntryIcon(boolean isDisabled) {
        return isDisabled ? Text.literal("\uDAC1\uDF26").formatted(Formatting.RED) : Text.literal("\uDAC1\uDF30").formatted(Formatting.GREEN);
    }

    private Tooltip getDisabledEntryTooltip(boolean isDisabled) {
        return Tooltip.of(Text.translatable(isDisabled ? "customtooltips.tooltip_list_widget.disabled_button" : "customtooltips.tooltip_list_widget.enabled_button"));
    }

    private Text getCopyIcon() {
        return Text.literal("\uDAC1\uDF32");
    }
}