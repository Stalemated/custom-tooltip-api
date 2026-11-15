package com.stalemated.customtooltips.core.position.strategies;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.core.position.TooltipPositionStrategy;
import com.stalemated.customtooltips.core.text.TextFormatter;
import java.util.List;
import net.minecraft.network.chat.Component;

public class InsertStrategy implements TooltipPositionStrategy {
    private final boolean isTop;
    public InsertStrategy(boolean isTop) { this.isTop = isTop; }

    @Override
    public void modifyTooltip(List<Component> lines, List<Component> componentsToInsert, TooltipEntry entry) {
        int baseIndex = isTop ? Math.min(1, lines.size()) : lines.size();
        int insertIndex = baseIndex + entry.getLineOffset(lines.size());

        if (entry.emptyLineBefore) {
            lines.add(insertIndex, Component.empty());
            insertIndex++;
        }
        TextFormatter.insertLines(lines, componentsToInsert, insertIndex, 0);
    }

    @Override
    public Component modifyHeldItemName(Component originalName, List<Component> componentsToInsert, TooltipEntry entry) {
        return originalName;
    }
}