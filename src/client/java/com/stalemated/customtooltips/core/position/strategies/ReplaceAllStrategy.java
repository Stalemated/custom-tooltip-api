package com.stalemated.customtooltips.core.position.strategies;

import net.minecraft.text.Text;
import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.core.position.TooltipPositionStrategy;
import java.util.List;

public class ReplaceAllStrategy implements TooltipPositionStrategy {
    @Override
    public void modifyTooltip(List<Text> lines, List<Text> componentsToInsert, TooltipEntry entry) {
        lines.clear();
        lines.addAll(componentsToInsert);
    }
    @Override
    public Text modifyHeldItemName(Text originalName, List<Text> componentsToInsert, TooltipEntry entry) {
        return componentsToInsert.isEmpty() ? originalName : componentsToInsert.get(0);
    }
}