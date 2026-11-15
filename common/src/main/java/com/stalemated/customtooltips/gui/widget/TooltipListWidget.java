package com.stalemated.customtooltips.gui.widget;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.api.CustomTooltipApi;
import com.stalemated.customtooltips.config.TooltipConfig;
import com.stalemated.customtooltips.core.sorting.SortMode;
import com.stalemated.customtooltips.core.sorting.TooltipSorter;
import com.stalemated.customtooltips.config.ConfigManager;
import com.stalemated.customtooltips.gui.screen.TooltipListScreen;
import com.stalemated.lib.helper.text.ScrollingTextRenderer;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class TooltipListWidget extends ObjectSelectionList<TooltipListWidget.Entry> {

    public final TooltipListScreen parentScreen;

    public TooltipListWidget(Minecraft client, int width, int height, int top, int bottom, int itemHeight, TooltipListScreen parentScreen) {
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
            if (apiEntries != null) sortedEntries.addAll(apiEntries);

        } else {
            if (config.entries != null) sortedEntries.addAll(config.entries);
        }

        TooltipSorter.sort(sortedEntries, config);

        for (TooltipEntry entry : sortedEntries) {
            if (lowerSearch.isEmpty() || entryMatchesSearch(entry, lowerSearch)) {
                this.addEntry(new com.stalemated.customtooltips.gui.widget.TooltipListWidget.Entry(this, entry));
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
    public int getRowWidth() {
        int rowWidth = Math.toIntExact(Math.round(this.width * 0.9));
        int maxRowWidth = 500;
        return Math.min(rowWidth, maxRowWidth);
    }

    @Override
    protected int getScrollbarPosition() { return this.width - 5; }

    public class Entry extends ObjectSelectionList.Entry<com.stalemated.customtooltips.gui.widget.TooltipListWidget.Entry> {
        private final TooltipEntry tooltipEntry;
        private final RowActionsWidget rowActions;
        private final ReorderActionsWidget reorderActions;
        private final ScrollingTextRenderer scrollingText;

        public Entry(TooltipListWidget parent, TooltipEntry tooltipEntry) {
            this.tooltipEntry = tooltipEntry;
            this.rowActions = new RowActionsWidget(parent, tooltipEntry);
            this.reorderActions = new ReorderActionsWidget(parent, tooltipEntry);
            this.scrollingText = new ScrollingTextRenderer();
        }

        @Override
        public void render(GuiGraphics context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            boolean isDisabled = ConfigManager.getConfig().disabled_entries.contains(this.tooltipEntry.getIdentifier());
            String targetText = this.tooltipEntry.target.isEmpty() ? "New Tooltip" : this.tooltipEntry.target;

            rowActions.render(context, x, y, entryWidth, mouseX, mouseY, tickDelta, this.tooltipEntry.apiEntry);

            int textStartX = x;
            if (ConfigManager.getConfig().sort_mode == SortMode.CREATION_DATE && !TooltipListScreen.showApiEntries && !this.tooltipEntry.apiEntry) {
                reorderActions.render(context, x, y, mouseX, mouseY, tickDelta);
                textStartX += 48; // 2 buttons * 24px
            }

            int buttonsStartX = x + entryWidth - rowActions.getWidth() - 5;
            int availableTextWidth = buttonsStartX - textStartX - 5;

            context.enableScissor(textStartX, y, buttonsStartX, y + entryHeight);
            scrollingText.render(context, targetText, textStartX + 4, y + 6, availableTextWidth, isDisabled);
            context.disableScissor();

            boolean isHoveringOverText = mouseX >= textStartX && mouseX < buttonsStartX && mouseY >= y && mouseY < y + entryHeight;
            if (isHoveringOverText) {
                List<Component> hoverTooltip = new ArrayList<>(this.tooltipEntry.getTextComponents(ItemStack.EMPTY));
                TooltipListWidget.this.parentScreen.setHoveredTooltip(hoverTooltip, this.tooltipEntry);
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (rowActions.mouseClicked(mouseX, mouseY, button)) return true;
            if (!tooltipEntry.apiEntry && reorderActions.mouseClicked(mouseX, mouseY, button)) return true;
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public Component getNarration() {
            return Component.translatable("customtooltips.tooltip_list_widget.entry.narration", this.tooltipEntry.target);
        }
    }
}