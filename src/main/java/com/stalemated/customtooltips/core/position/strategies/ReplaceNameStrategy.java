package com.stalemated.customtooltips.core.position.strategies;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.core.position.TooltipPositionStrategy;
import com.stalemated.customtooltips.core.text.TextFormatter;
import java.util.List;
import net.minecraft.network.chat.Component;

public class ReplaceNameStrategy implements TooltipPositionStrategy {
    @Override
    public void modifyTooltip(List<Component> lines, List<Component> componentsToInsert, TooltipEntry entry) {
        if (componentsToInsert.isEmpty()) return;
        int targetIndex = 0;
        TextFormatter.replaceLine(lines, componentsToInsert, targetIndex);
    }
    @Override
    public Component modifyHeldItemName(Component originalName, List<Component> componentsToInsert, TooltipEntry entry) {
        return componentsToInsert.isEmpty() ? originalName : componentsToInsert.get(0);
    }
}