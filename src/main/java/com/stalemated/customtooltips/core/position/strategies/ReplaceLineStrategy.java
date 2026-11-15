package com.stalemated.customtooltips.core.position.strategies;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.core.position.TooltipPositionStrategy;
import com.stalemated.customtooltips.core.text.TextFormatter;
import java.util.List;
import net.minecraft.network.chat.Component;

public class ReplaceLineStrategy implements TooltipPositionStrategy {
    @Override
    public void modifyTooltip(List<Component> lines, List<Component> componentsToInsert, TooltipEntry entry) {
        if (componentsToInsert.isEmpty() || lines.isEmpty()) return;

        int size = lines.size();
        int targetIndex = Math.min(entry.getLineOffset(size) + 1, size - 1);
        
        if (targetIndex >= 1 && targetIndex < size) {
            TextFormatter.replaceLine(lines, componentsToInsert, targetIndex);
        }
    }

    @Override
    public Component modifyHeldItemName(Component originalName, List<Component> componentsToInsert, TooltipEntry entry) {
        return originalName;
    }
}
