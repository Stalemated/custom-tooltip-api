package com.stalemated.customtooltips.core.position.strategies;

import net.minecraft.text.Text;
import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.core.position.TooltipPositionStrategy;
import com.stalemated.customtooltips.core.text.TextFormatter;
import java.util.List;

public class PrependStrategy implements TooltipPositionStrategy {
    @Override
    public void modifyTooltip(List<Text> lines, List<Text> componentsToInsert, TooltipEntry entry) {
        int insertIndex = entry.getLineOffset(lines.size());
        if (insertIndex >= 0 && insertIndex < lines.size()) {
            lines.set(insertIndex, TextFormatter.appendToLine(lines.get(insertIndex), componentsToInsert, "", " "));
        }
    }
    @Override
    public Text modifyHeldItemName(Text originalName, List<Text> componentsToInsert, TooltipEntry entry) {
        return entry.lineOffset == 0 ? TextFormatter.appendToLine(originalName, componentsToInsert, "", " ") : originalName;
    }
}