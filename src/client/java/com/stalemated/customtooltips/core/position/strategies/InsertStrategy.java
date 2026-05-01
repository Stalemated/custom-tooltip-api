package com.stalemated.customtooltips.core.position.strategies;

import net.minecraft.text.Text;
import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.core.position.TooltipPositionStrategy;
import com.stalemated.customtooltips.core.text.TextFormatter;
import java.util.List;

public class InsertStrategy implements TooltipPositionStrategy {
    private final boolean isTop;
    public InsertStrategy(boolean isTop) { this.isTop = isTop; }

    @Override
    public void modifyTooltip(List<Text> lines, List<Text> componentsToInsert, TooltipEntry entry) {
        int baseIndex = isTop ? 1 : lines.size();
        int insertIndex = baseIndex + entry.getLineOffset(lines.size());

        if (entry.empty_line_before) lines.add(insertIndex++, Text.empty());
        TextFormatter.insertLines(lines, componentsToInsert, insertIndex, 0);
    }

    @Override
    public Text modifyHeldItemName(Text originalName, List<Text> componentsToInsert, TooltipEntry entry) {
        return originalName;
    }
}