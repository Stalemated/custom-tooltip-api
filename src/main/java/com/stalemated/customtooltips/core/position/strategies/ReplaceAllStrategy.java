package com.stalemated.customtooltips.core.position.strategies;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.core.position.TooltipPositionStrategy;
import java.util.List;
import net.minecraft.network.chat.Component;

public class ReplaceAllStrategy implements TooltipPositionStrategy {
    @Override
    public void modifyTooltip(List<Component> lines, List<Component> componentsToInsert, TooltipEntry entry) {
        lines.clear();
        if (componentsToInsert.isEmpty()) componentsToInsert = List.of(Component.nullToEmpty(""));
        lines.addAll(componentsToInsert);
    }
    @Override
    public Component modifyHeldItemName(Component originalName, List<Component> componentsToInsert, TooltipEntry entry) {
        return componentsToInsert.isEmpty() ? originalName : componentsToInsert.get(0);
    }
}