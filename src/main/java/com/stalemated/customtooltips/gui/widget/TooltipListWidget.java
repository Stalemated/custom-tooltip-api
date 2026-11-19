package com.stalemated.customtooltips.gui.widget;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.config.TooltipConfig;
import com.stalemated.customtooltips.core.TooltipRegistry;
import com.stalemated.customtooltips.core.sorting.SortMode;
import com.stalemated.customtooltips.core.sorting.TooltipSorter;
import com.stalemated.customtooltips.config.CustomTooltipsConfigRegistry;
import com.stalemated.customtooltips.gui.TargetName;
import com.stalemated.customtooltips.gui.screen.TooltipListScreen;
import com.stalemated.lib.helper.text.ScrollingTextRenderer;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TooltipListWidget extends ObjectSelectionList<TooltipListWidget.Entry> {

    private final TooltipListScreen screen;

    public TooltipListWidget(Minecraft client, int width, int height, int top, int bottom, int itemHeight, TooltipListScreen screen) {
        super(client, width, height, top, bottom, itemHeight);
        this.screen = screen;
        this.updateEntries();
    }

    public void updateEntries(String searchText) {
        this.clearEntries();
        TooltipConfig config = CustomTooltipsConfigRegistry.CONFIG.get();
        String lowerSearch = searchText != null ? searchText.toLowerCase() : "";

        List<TooltipEntry> sortedEntries = new ArrayList<>();

        if (TooltipListScreen.showsApiEntries()) {
            sortedEntries.addAll(TooltipRegistry.apiEntries());

        } else {
            if (config.entries != null) sortedEntries.addAll(config.entries);
        }

        TooltipSorter.sort(sortedEntries, config);

        for (TooltipEntry entry : sortedEntries) {
            if (lowerSearch.isEmpty() || entryMatchesSearch(entry, lowerSearch)) {
                this.addEntry(new TooltipListWidget.Entry(entry));
            }
        }
    }

    public void updateEntries() {
        this.updateEntries("");
    }

    private boolean entryMatchesSearch(TooltipEntry entry, String lowerSearch) {
        if (entry.target != null && entry.target.toLowerCase().contains(lowerSearch)) return true;
        if (entry.apiEntry && entry.getIdentifier().toLowerCase().contains(lowerSearch)) return true;

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

    public class Entry extends ObjectSelectionList.Entry<TooltipListWidget.Entry> {
        private final TooltipEntry tooltipEntry;
        private final RowActionsWidget rowActions;
        private final ReorderActionsWidget reorderActions;
        private final ScrollingTextRenderer scrollingText;

        public Entry(TooltipEntry tooltipEntry) {
            this.tooltipEntry = tooltipEntry;
            this.rowActions = new RowActionsWidget(TooltipListWidget.this.screen::refreshList, tooltipEntry);
            this.reorderActions = new ReorderActionsWidget(TooltipListWidget.this.screen::refreshList, tooltipEntry);
            this.scrollingText = new ScrollingTextRenderer();
        }

        @Override
        public void render(GuiGraphics context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            boolean isDisabled = TooltipRegistry.isDisabled(this.tooltipEntry);
            String targetText = TargetName.of(this.tooltipEntry).getString();
            if (this.tooltipEntry.apiEntry) targetText += "  (" + this.tooltipEntry.getIdentifier() + ")";

            rowActions.render(context, x, y, entryWidth, mouseX, mouseY, tickDelta, this.tooltipEntry.apiEntry);

            int textStartX = x;
            if (CustomTooltipsConfigRegistry.CONFIG.get().sort_mode == SortMode.MANUAL_ORDER && !TooltipListScreen.showsApiEntries() && !this.tooltipEntry.apiEntry) {
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
                TooltipListWidget.this.screen.setHoveredTooltip(hoverTooltip, this.tooltipEntry);
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (rowActions.mouseClicked(mouseX, mouseY, button)) return true;
            if (!tooltipEntry.apiEntry && reorderActions.mouseClicked(mouseX, mouseY, button)) return true;
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public @NotNull Component getNarration() {
            return Component.translatable("customtooltips.tooltip_list_widget.entry.narration", TargetName.of(this.tooltipEntry));
        }
    }
}