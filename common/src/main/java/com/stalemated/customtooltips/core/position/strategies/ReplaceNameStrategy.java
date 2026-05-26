package com.stalemated.customtooltips.core.position.strategies;

import net.minecraft.text.Text;
import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.core.position.TooltipPositionStrategy;
import com.stalemated.customtooltips.core.text.TextFormatter;
import java.util.List;

public class ReplaceNameStrategy implements TooltipPositionStrategy {
    @Override
    public void modifyTooltip(List<Text> lines, List<Text> componentsToInsert, TooltipEntry entry) {
        if (componentsToInsert.isEmpty()) return;
        lines.set(0, componentsToInsert.get(0));
        TextFormatter.insertLines(lines, componentsToInsert, 1, 1);
    }
    @Override
    public Text modifyHeldItemName(Text originalName, List<Text> componentsToInsert, TooltipEntry entry) {
        return componentsToInsert.isEmpty() ? originalName : componentsToInsert.get(0);
    }
}