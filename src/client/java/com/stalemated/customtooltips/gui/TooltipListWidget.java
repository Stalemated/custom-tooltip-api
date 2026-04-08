package com.stalemated.customtooltips.gui;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.config.TooltipConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class TooltipListWidget extends AlwaysSelectedEntryListWidget<TooltipListWidget.Entry> {

    public TooltipListWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
        super(client, width, height, top, bottom, itemHeight);
        this.updateEntries();
    }

    public void updateEntries() {
        this.clearEntries();
        TooltipConfig config = AutoConfig.getConfigHolder(TooltipConfig.class).getConfig();
        if (config.entries != null) {
            for (TooltipEntry entry : config.entries) {
                this.addEntry(new Entry(this, entry));
            }
        }
    }

    @Override
    public int getRowWidth() {
        return 300;
    }

    @Override
    protected int getScrollbarPositionX() {
        return this.width / 2 + 154;
    }

    public class Entry extends AlwaysSelectedEntryListWidget.Entry<Entry> {
        private final TooltipEntry tooltipEntry;
        private final ButtonWidget editButton;
        private final ButtonWidget deleteButton;

        public Entry(TooltipListWidget parent, TooltipEntry tooltipEntry) {
            this.tooltipEntry = tooltipEntry;

            this.editButton = ButtonWidget.builder(Text.translatable("customtooltips.tooltip_list_widget.edit_button"), button -> client.setScreen(TooltipEditScreen.create(client.currentScreen, this.tooltipEntry, false))).dimensions(0, 0, 50, 20).build();

            this.deleteButton = ButtonWidget.builder(Text.translatable("customtooltips.tooltip_list_widget.delete_button"), button -> {
                TooltipConfig config = AutoConfig.getConfigHolder(TooltipConfig.class).getConfig();
                config.entries.remove(this.tooltipEntry);
                AutoConfig.getConfigHolder(TooltipConfig.class).save();
                parent.updateEntries();
            }).dimensions(0, 0, 50, 20).build();
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            String targetText = this.tooltipEntry.target.isEmpty() ? "New Tooltip" : this.tooltipEntry.target;
            int textX = x + 5;
            int textY = y + 6;
            context.drawTextWithShadow(client.textRenderer, targetText, textX, textY, 0xFFFFFF);

            int textW = client.textRenderer.getWidth(targetText);
            int textH = client.textRenderer.fontHeight;
            boolean isHoveringOverTarget = mouseX >= textX - 5 && mouseX <= textX + textW + 5 && mouseY >= textY - 5 && mouseY <= textY + textH + 5;

            if (isHoveringOverTarget) {
                context.drawTooltip(client.textRenderer, this.tooltipEntry.getTextComponents(), mouseX, mouseY);
            }

            this.editButton.setX(x + entryWidth - 105);
            this.editButton.setY(y);
            this.editButton.render(context, mouseX, mouseY, tickDelta);

            this.deleteButton.setX(x + entryWidth - 50);
            this.deleteButton.setY(y);
            this.deleteButton.render(context, mouseX, mouseY, tickDelta);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (this.editButton.mouseClicked(mouseX, mouseY, button)) return true;
            if (this.deleteButton.mouseClicked(mouseX, mouseY, button)) return true;
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public Text getNarration() {
            return Text.translatable("customtooltips.tooltip_list_widget.entry.narration", this.tooltipEntry.target);
        }
    }
}
