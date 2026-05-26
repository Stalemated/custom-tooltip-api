package com.stalemated.customtooltips.core.position.strategies;

import net.minecraft.text.Text;
import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.core.position.TooltipPositionStrategy;
import com.stalemated.customtooltips.core.text.TextFormatter;
import java.util.List;

public class AppendPrependStrategy implements TooltipPositionStrategy {
    private final String prefix;
    private final String suffix;

    public AppendPrependStrategy(boolean isAppend) {
        this.prefix = isAppend ? " " : "";
        this.suffix = isAppend ? "" : " ";
    }

    @Override
    public void modifyTooltip(List<Text> lines, List<Text> componentsToInsert, TooltipEntry entry) {
        int insertIndex = entry.getLineOffset(lines.size());
        if (insertIndex >= 0 && insertIndex < lines.size()) {
            lines.set(insertIndex, TextFormatter.appendToLine(lines.get(insertIndex), componentsToInsert, prefix, suffix));
        }
    }

    @Override
    public Text modifyHeldItemName(Text originalName, List<Text> componentsToInsert, TooltipEntry entry) {
        return entry.lineOffset == 0 ? TextFormatter.appendToLine(originalName, componentsToInsert, prefix, suffix) : originalName;
    }
}