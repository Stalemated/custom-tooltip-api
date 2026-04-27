package com.stalemated.customtooltips.gui;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.api.CustomTooltipApi;
import com.stalemated.customtooltips.config.TooltipConfig;
import com.stalemated.customtooltips.ConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class TooltipListWidget extends AlwaysSelectedEntryListWidget<TooltipListWidget.Entry> {

    private final TooltipListScreen parentScreen;

    public TooltipListWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight, TooltipListScreen parentScreen) {
        super(client, width, height, top, bottom, itemHeight);
        this.parentScreen = parentScreen;
        this.updateEntries();
    }

    public void updateEntries(String searchText) {
        this.clearEntries();
        TooltipConfig config = ConfigManager.getConfig();
        String lowerSearch = searchText != null ? searchText.toLowerCase() : "";

        List<TooltipEntry> sortedEntries = new ArrayList<>();

        if (TooltipListScreen.showApiEntries ) {
            List<TooltipEntry> apiEntries = CustomTooltipApi.getApiEntries();
            if (apiEntries != null) {
                sortedEntries.addAll(apiEntries);
            }
        } else {
            if (config.entries != null) {
                sortedEntries.addAll(config.entries);
            }
        }

        if (ConfigManager.getConfig().sort_by_name) {
            // Tags first then alphabetically (groups by mod id)
            sortedEntries.sort((a, b) -> {
                String targetA = a.target == null ? "" : a.target;
                String targetB = b.target == null ? "" : b.target;

                boolean isTagA = targetA.startsWith("#");
                boolean isTagB = targetB.startsWith("#");

                if (isTagA && !isTagB) return -1;
                if (!isTagA && isTagB) return 1;

                return targetA.compareToIgnoreCase(targetB);
            });
        }

        for (TooltipEntry entry : sortedEntries) {
            if (lowerSearch.isEmpty() || entryMatchesSearch(entry, lowerSearch)) {
                this.addEntry(new Entry(this, entry));
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
        private final ButtonWidget disableButton;
        private static final double SCROLL_SPEED_PIXELS_PER_SECOND = 25.0;
        private static final long SCROLL_PAUSE_MS = 1500L;
        private final long startTime;

        public Entry(TooltipListWidget parent, TooltipEntry tooltipEntry) {
            this.tooltipEntry = tooltipEntry;
            this.startTime = System.currentTimeMillis();

            this.editButton = ButtonWidget.builder(Text.translatable("customtooltips.tooltip_list_widget.edit_button"), button -> client.setScreen(TooltipEditScreen.create(client.currentScreen, this.tooltipEntry, false))).dimensions(0, 0, 50, 20).build();

            String identifier = this.tooltipEntry.getIdentifier();
            Text toggleText = Text.translatable(ConfigManager.getConfig().disabled_entries.contains(identifier) ? "customtooltips.tooltip_list_widget.enable_button" : "customtooltips.tooltip_list_widget.disable_button");

            this.disableButton = ButtonWidget.builder(toggleText, button -> {
                TooltipConfig config = ConfigManager.getConfig();
                if (config.disabled_entries.contains(identifier)) {
                    config.disabled_entries.remove(identifier);
                } else {
                    config.disabled_entries.add(identifier);
                }
                ConfigManager.save();
                parent.updateEntries(parent.parentScreen.searchBox.getText());
            }).dimensions(0, 0, 50, 20).build();

            this.deleteButton = ButtonWidget.builder(Text.translatable("customtooltips.tooltip_list_widget.delete_button"), button -> {
                Screen currentScreen = client.currentScreen;
                client.setScreen(new ConfirmScreen(
                        (confirmed) -> {
                            if (confirmed) {
                                TooltipConfig config = ConfigManager.getConfig();
                                config.entries.remove(this.tooltipEntry);
                                ConfigManager.save();
                                parent.updateEntries(parent.parentScreen.searchBox.getText());
                            }
                            client.setScreen(currentScreen);
                        },
                        Text.translatable("customtooltips.tooltip_list_widget.delete_confirm.title"),
                        Text.translatable("customtooltips.tooltip_list_widget.delete_confirm.message", this.tooltipEntry.target.isEmpty() ? "New Tooltip" : this.tooltipEntry.target)
                ));
            }).dimensions(0, 0, 50, 20).build();

            this.duplicateEntryButton = ButtonWidget.builder(Text.translatable("customtooltips.tooltip_list_widget.duplicate_button"), button -> {
                TooltipConfig config = ConfigManager.getConfig();
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

                newEntry.apiEntry = false;
                newEntry.apiEntryId = "";
                config.entries.add(newEntry);
                ConfigManager.save();
                parent.updateEntries(parent.parentScreen.searchBox.getText());
            }).dimensions(0, 0, 50, 20).build();
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            boolean isDisabled = ConfigManager.getConfig().disabled_entries.contains(this.tooltipEntry.getIdentifier());
            String targetText = this.tooltipEntry.target.isEmpty() ? "New Tooltip" : this.tooltipEntry.target;
            int textY = y + 6;

            int buttonCount = this.tooltipEntry.apiEntry ? 2 : 4;
            int buttonsStartX = x + entryWidth - (buttonCount * 55);
            int availableTextWidth = buttonsStartX - x - 5;
            int originalTextWidth = client.textRenderer.getWidth(targetText);

            renderScrollingText(context, targetText, originalTextWidth, x, textY, availableTextWidth, y, entryHeight, buttonsStartX, isDisabled);

            boolean isHoveringOverText = mouseX >= x && mouseX < buttonsStartX && mouseY >= y && mouseY < y + entryHeight;
            if (isHoveringOverText) {
                List<Text> hoverTooltip = new ArrayList<>();
                if (originalTextWidth > availableTextWidth) {
                    hoverTooltip.add(Text.literal(targetText));
                    hoverTooltip.add(Text.empty());
                }
                hoverTooltip.addAll(this.tooltipEntry.getTextComponents());
                TooltipListWidget.this.parentScreen.setHoveredTooltip(hoverTooltip);
            }

            if (this.tooltipEntry.apiEntry) {
                this.disableButton.setX(x + entryWidth - 55 * 2);
                this.disableButton.setY(y);
                this.disableButton.render(context, mouseX, mouseY, tickDelta);

                this.duplicateEntryButton.setX(x + entryWidth - 55);
                this.duplicateEntryButton.setY(y);
                this.duplicateEntryButton.render(context, mouseX, mouseY, tickDelta);

                this.editButton.visible = false;
                this.editButton.active = false;

                this.deleteButton.visible = false;
                this.deleteButton.active = false;

            } else {
                this.disableButton.setX(x + entryWidth - 55 * 4);
                this.disableButton.setY(y);
                this.disableButton.render(context, mouseX, mouseY, tickDelta);

                this.duplicateEntryButton.setX(x + entryWidth - 55 * 3);
                this.duplicateEntryButton.setY(y);
                this.duplicateEntryButton.render(context, mouseX, mouseY, tickDelta);

                this.editButton.visible = true;
                this.editButton.active = true;
                this.editButton.setX(x + entryWidth - 55 * 2);
                this.editButton.setY(y);
                this.editButton.render(context, mouseX, mouseY, tickDelta);

                this.deleteButton.visible = true;
                this.deleteButton.active = true;
                this.deleteButton.setX(x + entryWidth - 55);
                this.deleteButton.setY(y);
                this.deleteButton.render(context, mouseX, mouseY, tickDelta);
            }
        }

        private void renderScrollingText(DrawContext context, String text, int textWidth, int x, int y, int availableWidth, int scissorY, int scissorHeight, int scissorEndX, boolean isDisabled) {
            if (textWidth > availableWidth) {
                int overflowWidth = textWidth - availableWidth;
                double speed = SCROLL_SPEED_PIXELS_PER_SECOND / 1000.0;
                long travelTime = (long) (overflowWidth / speed);
                if (travelTime <= 0) travelTime = 1;

                long halfCycle = travelTime + SCROLL_PAUSE_MS;
                long totalCycle = 2 * halfCycle;

                long elapsed = System.currentTimeMillis() - this.startTime;
                long cycleTime = elapsed % totalCycle;
                double progress = getProgress(cycleTime, halfCycle, travelTime);

                int color = isDisabled ? 0xAAAAAA : 0xFFFFFF;
                int scrollOffset = (int) (progress * overflowWidth);

                context.enableScissor(x, scissorY, scissorEndX, scissorY + scissorHeight);
                context.drawTextWithShadow(client.textRenderer, text, x - scrollOffset, y, color);
                context.disableScissor();
            } else {
                context.drawTextWithShadow(client.textRenderer, text, x, y, isDisabled ? 0xAAAAAA : 0xFFFFFF);
            }
        }

        private double getProgress(long cycleTime, long halfCycle, long travelTime) {
            double progress;
            if (cycleTime < SCROLL_PAUSE_MS) {
                progress = 0.0; // Pause at the start
            } else if (cycleTime < halfCycle) {
                progress = (double) (cycleTime - SCROLL_PAUSE_MS) / travelTime; // Moving right
            } else if (cycleTime < halfCycle + SCROLL_PAUSE_MS) {
                progress = 1.0; // Pause at the end
            } else {
                progress = 1.0 - ((double) (cycleTime - (halfCycle + SCROLL_PAUSE_MS)) / travelTime); // Moving left
            }
            return progress;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!this.tooltipEntry.apiEntry && this.editButton.mouseClicked(mouseX, mouseY, button)) return true;
            if (this.disableButton.mouseClicked(mouseX, mouseY, button)) return true;
            if (!this.tooltipEntry.apiEntry && this.deleteButton.mouseClicked(mouseX, mouseY, button)) return true;
            if (this.duplicateEntryButton.mouseClicked(mouseX, mouseY, button)) return true;
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public Text getNarration() {
            return Text.translatable("customtooltips.tooltip_list_widget.entry.narration", this.tooltipEntry.target);
        }
    }
}