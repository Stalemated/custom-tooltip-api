package com.stalemated.customtooltips.gui;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.config.TooltipConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class TooltipListWidget extends AlwaysSelectedEntryListWidget<TooltipListWidget.Entry> {

    private final TooltipListScreen parentScreen;

    public TooltipListWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight, TooltipListScreen parentScreen) {
        super(client, width, height, top, bottom, itemHeight);
        this.parentScreen = parentScreen;
        this.updateEntries("");
    }

    public void updateEntries(String searchText) {
        this.clearEntries();
        TooltipConfig config = AutoConfig.getConfigHolder(TooltipConfig.class).getConfig();
        String lowerSearch = searchText != null ? searchText.toLowerCase() : "";

        if (config.entries != null) {
            for (TooltipEntry entry : config.entries) {
                if (lowerSearch.isEmpty() || entryMatchesSearch(entry, lowerSearch)) {
                    this.addEntry(new Entry(this, entry));
                }
            }
        }
    }

    public void updateEntries() {
        this.updateEntries("");
    }

    private boolean entryMatchesSearch(TooltipEntry entry, String lowerSearch) {
        if (entry.target != null && entry.target.toLowerCase().contains(lowerSearch)) return true;

        if (entry.text != null) {
            for (String line : entry.text) {
                if (line.toLowerCase().contains(lowerSearch)) return true;
            }
        }
        return false;
    }

    @Override
    public int getRowWidth() { return Math.toIntExact(Math.round(this.width * 0.9)); }

    @Override
    protected int getScrollbarPositionX() { return this.width - 5; }

    public class Entry extends AlwaysSelectedEntryListWidget.Entry<Entry> {
        private final TooltipEntry tooltipEntry;
        private final ButtonWidget editButton;
        private final ButtonWidget deleteButton;
        private final ButtonWidget duplicateEntryButton;

        public Entry(TooltipListWidget parent, TooltipEntry tooltipEntry) {
            this.tooltipEntry = tooltipEntry;

            this.editButton = ButtonWidget.builder(Text.translatable("customtooltips.tooltip_list_widget.edit_button"), button -> client.setScreen(TooltipEditScreen.create(client.currentScreen, this.tooltipEntry, false))).dimensions(0, 0, 50, 20).build();

            this.deleteButton = ButtonWidget.builder(Text.translatable("customtooltips.tooltip_list_widget.delete_button"), button -> {
                Screen currentScreen = client.currentScreen;
                client.setScreen(new ConfirmScreen(
                        (confirmed) -> {
                            if (confirmed) {
                                TooltipConfig config = AutoConfig.getConfigHolder(TooltipConfig.class).getConfig();
                                config.entries.remove(this.tooltipEntry);
                                AutoConfig.getConfigHolder(TooltipConfig.class).save();
                                parent.updateEntries(parent.parentScreen.searchBox.getText());
                            }
                            client.setScreen(currentScreen);
                        },
                        Text.translatable("customtooltips.tooltip_list_widget.delete_confirm.title"),
                        Text.translatable("customtooltips.tooltip_list_widget.delete_confirm.message", this.tooltipEntry.target.isEmpty() ? "New Tooltip" : this.tooltipEntry.target)
                ));
            }).dimensions(0, 0, 50, 20).build();

            this.duplicateEntryButton = ButtonWidget.builder(Text.translatable("customtooltips.tooltip_list_widget.duplicate_button"), button -> {
                TooltipConfig config = AutoConfig.getConfigHolder(TooltipConfig.class).getConfig();
                TooltipEntry newEntry = new TooltipEntry(
                        this.tooltipEntry.target, this.tooltipEntry.text,
                        this.tooltipEntry.style, this.tooltipEntry.colors,
                        this.tooltipEntry.bold, this.tooltipEntry.italic,
                        this.tooltipEntry.underlined, this.tooltipEntry.strikethrough,
                        this.tooltipEntry.obfuscated, this.tooltipEntry.require_shift,
                        this.tooltipEntry.empty_line_before, this.tooltipEntry.position,
                        this.tooltipEntry.lineOffset, this.tooltipEntry.animation_offset,
                        this.tooltipEntry.tickrate
                );
                config.entries.add(newEntry);
                AutoConfig.getConfigHolder(TooltipConfig.class).save();
                parent.updateEntries(parent.parentScreen.searchBox.getText());
            }).dimensions(0, 0, 50, 20).build();
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            String targetText = this.tooltipEntry.target.isEmpty() ? "New Tooltip" : this.tooltipEntry.target;
            int textY = y + 6;
            context.drawTextWithShadow(client.textRenderer, targetText, x, textY, 0xFFFFFF);

            int textW = client.textRenderer.getWidth(targetText);
            int textH = client.textRenderer.fontHeight;
            boolean isHoveringOverTarget = mouseX >= x - 5 && mouseX <= x + textW + 5 && mouseY >= textY - 5 && mouseY <= textY + textH + 5;

            if (isHoveringOverTarget) {
                context.drawTooltip(client.textRenderer, this.tooltipEntry.getTextComponents(), mouseX, mouseY);
            }

            this.duplicateEntryButton.setX(x + entryWidth - 165);
            this.duplicateEntryButton.setY(y);
            this.duplicateEntryButton.render(context, mouseX, mouseY, tickDelta);

            this.editButton.setX(x + entryWidth - 110);
            this.editButton.setY(y);
            this.editButton.render(context, mouseX, mouseY, tickDelta);

            this.deleteButton.setX(x + entryWidth - 55);
            this.deleteButton.setY(y);
            this.deleteButton.render(context, mouseX, mouseY, tickDelta);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (this.editButton.mouseClicked(mouseX, mouseY, button)) return true;
            if (this.deleteButton.mouseClicked(mouseX, mouseY, button)) return true;
            if (this.duplicateEntryButton.mouseClicked(mouseX, mouseY, button)) return true;
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public Text getNarration() {
            return Text.translatable("customtooltips.tooltip_list_widget.entry.narration", this.tooltipEntry.target);
        }
    }
}