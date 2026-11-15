package com.stalemated.customtooltips.core.position;

import com.stalemated.customtooltips.TooltipEntry;
import java.util.List;
import net.minecraft.network.chat.Component;

public interface TooltipPositionStrategy {
    void modifyTooltip(List<Component> lines, List<Component> componentsToInsert, TooltipEntry entry);
    
    Component modifyHeldItemName(Component originalName, List<Component> componentsToInsert, TooltipEntry entry);
}