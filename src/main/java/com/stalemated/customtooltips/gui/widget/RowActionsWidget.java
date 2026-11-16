package com.stalemated.customtooltips.gui.widget;

import com.stalemated.customtooltips.config.CustomTooltipsConfigRegistry;
import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.config.TooltipConfig;
import com.stalemated.customtooltips.gui.screen.TooltipEditScreen;
import com.stalemated.customtooltips.util.ToastManager;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class RowActionsWidget {
    private final List<Button> buttons = new ArrayList<>();
    private final List<Button> apiButtons = new ArrayList<>();

    private final int BUTTON_STEP = 24;

    public RowActionsWidget(TooltipListWidget parent, TooltipEntry entry) {
        Minecraft client = Minecraft.getInstance();
        String identifier = entry.getIdentifier();
        boolean isDisabled = CustomTooltipsConfigRegistry.CONFIG.get().disabled_entries.contains(identifier);

        buttons.add(Button.builder(Component.literal("\uDAC1\uDF27").withStyle(ChatFormatting.RED), button -> {
            Screen currentScreen = client.screen;
            client.setScreen(new ConfirmScreen(
                    (confirmed) -> {
                        if (confirmed) {
                            TooltipConfig config = CustomTooltipsConfigRegistry.CONFIG.get();
                            config.entries.remove(entry);
                            if (isDisabled) config.disabled_entries.remove(identifier);
                            CustomTooltipsConfigRegistry.CONFIG.save();
                            parent.updateEntries(parent.parentScreen.searchBox.getValue());
                        }
                        client.setScreen(currentScreen);
                    },
                    Component.translatable("customtooltips.tooltip_list_widget.delete_confirm.title"),
                    Component.translatable("customtooltips.tooltip_list_widget.delete_confirm.message", entry.target.isEmpty() ? "New Tooltip" : entry.target)
            ));
        })
                .tooltip(Tooltip.create(Component.translatable("customtooltips.tooltip_list_widget.delete_button")))
                .build());

        buttons.add(Button.builder(Component.literal("\uDAC1\uDF28"), btn -> client.setScreen(TooltipEditScreen.edit(client.screen, entry)))
                .tooltip(Tooltip.create(Component.translatable("customtooltips.tooltip_list_widget.edit_button")))
                .build());

        apiButtons.add(Button.builder(getCopyIcon(), btn -> {
            client.keyboardHandler.setClipboard(CustomTooltipsConfigRegistry.CONFIG.toJson(entry));
            
            ToastManager.showCopiedToast(entry.target);
        })
                .tooltip(Tooltip.create(Component.translatable("customtooltips.tooltip_list_widget.copy_button")))
                .build());

        apiButtons.add(Button.builder(Component.literal("\uDAC1\uDF29"), btn -> {
            TooltipEntry newEntry = entry.copy();

            newEntry.apiEntry = false;
            newEntry.apiEntryId = "";

            CustomTooltipsConfigRegistry.CONFIG.get().entries.add(newEntry);
            CustomTooltipsConfigRegistry.CONFIG.save();

            ToastManager.showDuplicatedToast(entry.target.isEmpty() ? "New Tooltip" : entry.target);
            parent.updateEntries(parent.parentScreen.searchBox.getValue());
        })
                .tooltip(Tooltip.create(Component.translatable("customtooltips.tooltip_list_widget.duplicate_button")))
                .build());

        apiButtons.add(Button.builder(getDisabledEntryIcon(isDisabled), btn -> {
            TooltipConfig config = CustomTooltipsConfigRegistry.CONFIG.get();
            if (isDisabled) config.disabled_entries.remove(identifier);
            else config.disabled_entries.add(identifier);

            CustomTooltipsConfigRegistry.CONFIG.save();
            parent.updateEntries(parent.parentScreen.searchBox.getValue());
        })
                .tooltip(getDisabledEntryTooltip(isDisabled))
                .build());

        buttons.addAll(apiButtons);
    }

    public void render(GuiGraphics context, int x, int y, int entryWidth, int mouseX, int mouseY, float tickDelta, boolean isApiEntry) {
        int currentX = x + entryWidth;

        List<Button> visibleButtons = isApiEntry ? apiButtons : buttons;

        for (Button button : visibleButtons) {
            currentX -= BUTTON_STEP;
            button.setX(currentX);
            button.setY(y);
            button.setWidth(20);
            button.render(context, mouseX, mouseY, tickDelta);
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (Button btn : buttons) {
            if (btn.mouseClicked(mouseX, mouseY, button)) return true;
        }
        return false;
    }

    public int getWidth() {
        int btnAmount = buttons.size();
        return (buttons.size() * BUTTON_STEP) - btnAmount;
    }

    private Component getDisabledEntryIcon(boolean isDisabled) {
        return isDisabled ? Component.literal("\uDAC1\uDF26").withStyle(ChatFormatting.RED) : Component.literal("\uDAC1\uDF30").withStyle(ChatFormatting.GREEN);
    }

    private Tooltip getDisabledEntryTooltip(boolean isDisabled) {
        return Tooltip.create(Component.translatable(isDisabled ? "customtooltips.tooltip_list_widget.disabled_button" : "customtooltips.tooltip_list_widget.enabled_button"));
    }

    private Component getCopyIcon() {
        return Component.literal("\uDAC1\uDF32");
    }
}