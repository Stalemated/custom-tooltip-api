package com.stalemated.customtooltips.core.position.strategies;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.core.position.TooltipPositionStrategy;
import com.stalemated.customtooltips.core.text.TextFormatter;
import net.minecraft.text.Text;

import java.util.List;

public class ReplaceLineStrategy implements TooltipPositionStrategy {
    @Override
    public void modifyTooltip(List<Text> lines, List<Text> componentsToInsert, TooltipEntry entry) {
        if (componentsToInsert.isEmpty() || lines.isEmpty()) return;

        int size = lines.size();
        int targetIndex = Math.min(entry.getLineOffset(size) + 1, size - 1);
        
        if (targetIndex >= 1 && targetIndex < size) {
            TextFormatter.replaceLine(lines, componentsToInsert, targetIndex);
        }
    }

    @Override
    public Text modifyHeldItemName(Text originalName, List<Text> componentsToInsert, TooltipEntry entry) {
        return originalName;
    }
}
