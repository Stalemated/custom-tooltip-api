package com.stalemated.customtooltips.core.position;

import net.minecraft.text.Text;
import com.stalemated.customtooltips.TooltipEntry;
import java.util.List;

public interface TooltipPositionStrategy {
    void modifyTooltip(List<Text> lines, List<Text> componentsToInsert, TooltipEntry entry);
    
    Text modifyHeldItemName(Text originalName, List<Text> componentsToInsert, TooltipEntry entry);
}