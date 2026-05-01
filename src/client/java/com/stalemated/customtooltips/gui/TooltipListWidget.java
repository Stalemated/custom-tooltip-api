package com.stalemated.customtooltips.gui;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.api.CustomTooltipApi;
import com.stalemated.customtooltips.config.TooltipConfig;
import com.stalemated.customtooltips.ConfigManager;
import com.stalemated.customtooltips.util.ToastManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collections;
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

        TooltipConfig.SortMode sortMode = config.sort_mode;
        if (sortMode == TooltipConfig.SortMode.NAME_AND_TAG) {
            sortedEntries.sort((a, b) -> {
                String targetA = a.target == null ? "" : a.target;
                String targetB = b.target == null ? "" : b.target;

                boolean isTagA = targetA.startsWith("#");
                boolean isTagB = targetB.startsWith("#");

                if (isTagA && !isTagB) return -1;
                if (!isTagA && isTagB) return 1;

                return targetA.compareToIgnoreCase(targetB);
            });
        } else if (sortMode == TooltipConfig.SortMode.DISABLED_FIRST) {
            sortedEntries.sort((a, b) -> {
                String targetA = a.target == null ? "" : a.target;
                String targetB = b.target == null ? "" : b.target;

                boolean disabledA = config.disabled_entries.contains(a.getIdentifier());
                boolean disabledB = config.disabled_entries.contains(b.getIdentifier());
                
                if (disabledA && !disabledB) return -1;
                if (!disabledA && disabledB) return 1;
                
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
        private final ButtonWidget moveUpButton;
        private final ButtonWidget moveDownButton;
        private static final double SCROLL_SPEED_PIXELS_PER_SECOND = 25.0;
        private static final long SCROLL_PAUSE_MS = 1500L;
        private final long startTime;

        public Entry(TooltipListWidget parent, TooltipEntry tooltipEntry) {
            this.tooltipEntry = tooltipEntry;
            this.startTime = System.currentTimeMillis();
            int btnDim = 20;
            String identifier = this.tooltipEntry.getIdentifier();
            boolean isDisabled = ConfigManager.getConfig().disabled_entries.contains(identifier);

            this.editButton = ButtonWidget.builder(Text.literal("✎"), button -> client.setScreen(TooltipEditScreen.create(client.currentScreen, this.tooltipEntry, false)))
                    .dimensions(0, 0, btnDim, btnDim)
                    .tooltip(Tooltip.of(Text.translatable("customtooltips.tooltip_list_widget.edit_button")))
                    .build();

            this.disableButton = ButtonWidget.builder(Text.literal(isDisabled ? "▶" : "⏸"), button -> {
                TooltipConfig config = ConfigManager.getConfig();
                if (isDisabled) {
                    config.disabled_entries.remove(identifier);
                } else {
                    config.disabled_entries.add(identifier);
                }
                ConfigManager.save();
                parent.updateEntries(parent.parentScreen.searchBox.getText());
            }).dimensions(0, 0, btnDim, btnDim)
                    .tooltip(Tooltip.of(Text.translatable(isDisabled ? "customtooltips.tooltip_list_widget.enable_button" : "customtooltips.tooltip_list_widget.disable_button")))
                    .build();

            this.deleteButton = ButtonWidget.builder(Text.literal("✖"), button -> {
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
            }).dimensions(0, 0, btnDim, btnDim)
                    .tooltip(Tooltip.of(Text.translatable("customtooltips.tooltip_list_widget.delete_button")))
                    .build();

            this.duplicateEntryButton = ButtonWidget.builder(Text.literal("⧉"), button -> {
                TooltipConfig config = ConfigManager.getConfig();
                TooltipEntry newEntry = TooltipEntry.builder(this.tooltipEntry.target)
                        .text(this.tooltipEntry.text)
                        .style(this.tooltipEntry.style)
                        .colors(this.tooltipEntry.colors)
                        .bold(this.tooltipEntry.bold)
                        .italic(this.tooltipEntry.italic)
                        .underlined(this.tooltipEntry.underlined)
                        .strikethrough(this.tooltipEntry.strikethrough)
                        .obfuscated(this.tooltipEntry.obfuscated)
                        .requireShift(this.tooltipEntry.require_shift)
                        .emptyLineBefore(this.tooltipEntry.empty_line_before)
                        .position(this.tooltipEntry.position)
                        .lineOffset(this.tooltipEntry.lineOffset)
                        .animationOffset(this.tooltipEntry.animation_offset)
                        .tickrate(this.tooltipEntry.tickrate)
                        .font(this.tooltipEntry.font)
                        .build();

                newEntry.apiEntry = false;
                newEntry.apiEntryId = "";
                config.entries.add(newEntry);
                ConfigManager.save();
                
                String targetText = this.tooltipEntry.target.isEmpty() ? "New Tooltip" : this.tooltipEntry.target;
                ToastManager.showDuplicatedToast(targetText);
                
                parent.updateEntries(parent.parentScreen.searchBox.getText());
            }).dimensions(0, 0, btnDim, btnDim)
                    .tooltip(Tooltip.of(Text.translatable("customtooltips.tooltip_list_widget.duplicate_button")))
                    .build();

            this.moveUpButton = ButtonWidget.builder(Text.literal("▲"), button -> {
                TooltipConfig config = ConfigManager.getConfig();
                int index = config.entries.indexOf(this.tooltipEntry);
                if (index > 0) {
                    Collections.swap(config.entries, index, index - 1);
                    ConfigManager.save();
                    parent.updateEntries(parent.parentScreen.searchBox.getText());
                }
            }).dimensions(0, 0, btnDim, btnDim).build();

            this.moveDownButton = ButtonWidget.builder(Text.literal("▼"), button -> {
                TooltipConfig config = ConfigManager.getConfig();
                int index = config.entries.indexOf(this.tooltipEntry);
                if (index >= 0 && index < config.entries.size() - 1) {
                    Collections.swap(config.entries, index, index + 1);
                    ConfigManager.save();
                    parent.updateEntries(parent.parentScreen.searchBox.getText());
                }
            }).dimensions(0, 0, btnDim, btnDim).build();
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            boolean isDisabled = ConfigManager.getConfig().disabled_entries.contains(this.tooltipEntry.getIdentifier());
            String targetText = this.tooltipEntry.target.isEmpty() ? "New Tooltip" : this.tooltipEntry.target;
            int textY = y + 6;
            int btnStep = 24;

            int currentX = x + entryWidth;

            if (this.tooltipEntry.apiEntry) {
                currentX -= btnStep;
                this.duplicateEntryButton.setX(currentX);
                this.duplicateEntryButton.setY(y);
                this.duplicateEntryButton.render(context, mouseX, mouseY, tickDelta);

                currentX -= btnStep;
                this.disableButton.setX(currentX);
                this.disableButton.setY(y);
                this.disableButton.render(context, mouseX, mouseY, tickDelta);

                this.editButton.visible = false;
                this.editButton.active = false;

                this.deleteButton.visible = false;
                this.deleteButton.active = false;

                this.moveUpButton.visible = false;
                this.moveUpButton.active = false;

                this.moveDownButton.visible = false;
                this.moveDownButton.active = false;
            } else {
                currentX -= btnStep;
                this.deleteButton.setX(currentX);
                this.deleteButton.setY(y);
                this.deleteButton.visible = true;
                this.deleteButton.active = true;
                this.deleteButton.render(context, mouseX, mouseY, tickDelta);

                currentX -= btnStep;
                this.editButton.setX(currentX);
                this.editButton.setY(y);
                this.editButton.visible = true;
                this.editButton.active = true;
                this.editButton.render(context, mouseX, mouseY, tickDelta);

                currentX -= btnStep;
                this.duplicateEntryButton.setX(currentX);
                this.duplicateEntryButton.setY(y);
                this.duplicateEntryButton.visible = true;
                this.duplicateEntryButton.active = true;
                this.duplicateEntryButton.render(context, mouseX, mouseY, tickDelta);

                currentX -= btnStep;
                this.disableButton.setX(currentX);
                this.disableButton.setY(y);
                this.disableButton.visible = true;
                this.disableButton.active = true;
                this.disableButton.render(context, mouseX, mouseY, tickDelta);
            }

            int textStartX = x;

            if (ConfigManager.getConfig().sort_mode == TooltipConfig.SortMode.CREATION_DATE && !TooltipListScreen.showApiEntries && !this.tooltipEntry.apiEntry) {
                TooltipConfig config = ConfigManager.getConfig();
                int idx = config.entries.indexOf(this.tooltipEntry);

                this.moveUpButton.setX(textStartX);
                this.moveUpButton.setY(y);
                this.moveUpButton.visible = true;
                this.moveUpButton.active = (idx > 0);
                this.moveUpButton.render(context, mouseX, mouseY, tickDelta);

                textStartX += btnStep;
                this.moveDownButton.setX(textStartX);
                this.moveDownButton.setY(y);
                this.moveDownButton.visible = true;
                this.moveDownButton.active = (idx >= 0 && idx < config.entries.size() - 1);
                this.moveDownButton.render(context, mouseX, mouseY, tickDelta);
                
                textStartX += btnStep;
            } else {
                this.moveUpButton.visible = false;
                this.moveUpButton.active = false;
                this.moveDownButton.visible = false;
                this.moveDownButton.active = false;
            }

            int buttonsStartX = currentX - 5;
            int availableTextWidth = buttonsStartX - textStartX - 5;
            int originalTextWidth = client.textRenderer.getWidth(targetText);

            renderScrollingText(context, targetText, originalTextWidth, textStartX, textY, availableTextWidth, y, entryHeight, buttonsStartX, isDisabled);

            boolean isHoveringOverText = mouseX >= textStartX && mouseX < buttonsStartX && mouseY >= y && mouseY < y + entryHeight;
            if (isHoveringOverText) {
                List<Text> hoverTooltip = new ArrayList<>();
                if (originalTextWidth > availableTextWidth) {
                    hoverTooltip.add(Text.literal(targetText));
                    hoverTooltip.add(Text.empty());
                }
                hoverTooltip.addAll(this.tooltipEntry.getTextComponents());
                TooltipListWidget.this.parentScreen.setHoveredTooltip(hoverTooltip);
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
            if (this.moveUpButton.visible && this.moveUpButton.mouseClicked(mouseX, mouseY, button)) return true;
            if (this.moveDownButton.visible && this.moveDownButton.mouseClicked(mouseX, mouseY, button)) return true;
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