package com.stalemated.customtooltips.gui.widget;

import com.stalemated.customtooltips.config.CustomTooltipsConfigRegistry;
import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.core.LegacyApiIds;
import com.stalemated.customtooltips.core.TooltipRegistry;
import com.stalemated.customtooltips.gui.screen.TooltipEditScreen;
import com.stalemated.customtooltips.gui.EditButtons;
import com.stalemated.customtooltips.gui.GuiIcon;
import com.stalemated.customtooltips.gui.TargetName;
import com.stalemated.customtooltips.gui.TooltipClipboard;
import com.stalemated.customtooltips.gui.TooltipListChanges;
import com.stalemated.customtooltips.gui.TooltipListToasts;
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

    public RowActionsWidget(Runnable refreshList, TooltipEntry entry) {
        Minecraft client = Minecraft.getInstance();
        String identifier = entry.getIdentifier();
        boolean isDisabled = TooltipRegistry.isDisabled(entry);

        buttons.add(Button.builder(GuiIcon.DELETE.text().withStyle(ChatFormatting.RED), button -> {
            Screen currentScreen = client.screen;
            client.setScreen(new ConfirmScreen(
                    (confirmed) -> {
                        int index = CustomTooltipsConfigRegistry.CONFIG.get().entries.indexOf(entry);
                        if (confirmed && index >= 0) {
                            TooltipListChanges.save(config -> {
                                config.entries.remove(index);
                                if (isDisabled) config.disabled_entries.remove(identifier);
                            });
                            refreshList.run();
                        }
                        client.setScreen(currentScreen);
                    },
                    Component.translatable("customtooltips.tooltip_list_widget.delete_confirm.title"),
                    Component.translatable("customtooltips.tooltip_list_widget.delete_confirm.message", TargetName.of(entry))
            ));
        })
                .tooltip(Tooltip.create(Component.translatable("customtooltips.tooltip_list_widget.delete_button")))
                .build());

        buttons.add(EditButtons.requireYacl(Button.builder(GuiIcon.EDIT.text(), btn -> client.setScreen(TooltipEditScreen.edit(client.screen, entry)))
                .tooltip(Tooltip.create(Component.translatable("customtooltips.tooltip_list_widget.edit_button")))
                .build()));

        apiButtons.add(Button.builder(getCopyIcon(), btn -> {
            TooltipClipboard.copy(entry);

            TooltipListToasts.showCopiedToast(TargetName.of(entry));
        })
                .tooltip(Tooltip.create(Component.translatable("customtooltips.tooltip_list_widget.copy_button")))
                .build());

        apiButtons.add(Button.builder(GuiIcon.DUPLICATE.text(), btn -> {
            TooltipEntry newEntry = entry.copy();

            newEntry.apiEntry = false;
            newEntry.apiId = null;

            TooltipListChanges.save(config -> config.entries.add(newEntry));

            TooltipListToasts.showDuplicatedToast(TargetName.of(entry));
            refreshList.run();
        })
                .tooltip(Tooltip.create(Component.translatable("customtooltips.tooltip_list_widget.duplicate_button")))
                .build());

        apiButtons.add(Button.builder(getDisabledEntryIcon(isDisabled), btn -> {
            TooltipListChanges.save(config -> {
                config.disabled_entries = LegacyApiIds.replaceIn(config.disabled_entries, TooltipRegistry.apiEntries());
                if (isDisabled) config.disabled_entries.remove(identifier);
                else config.disabled_entries.add(identifier);
            });
            refreshList.run();
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
        return isDisabled ? GuiIcon.DISABLED.text().withStyle(ChatFormatting.RED) : GuiIcon.ENABLED.text().withStyle(ChatFormatting.GREEN);
    }

    private Tooltip getDisabledEntryTooltip(boolean isDisabled) {
        return Tooltip.create(Component.translatable(isDisabled ? "customtooltips.tooltip_list_widget.disabled_button" : "customtooltips.tooltip_list_widget.enabled_button"));
    }

    private Component getCopyIcon() {
        return GuiIcon.COPY.text();
    }
}