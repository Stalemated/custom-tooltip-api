package com.stalemated.customtooltips.core.position.strategies;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.core.position.TooltipPositionStrategy;
import com.stalemated.customtooltips.core.text.TextFormatter;
import java.util.List;
import net.minecraft.network.chat.Component;

public class AppendPrependStrategy implements TooltipPositionStrategy {
    private final String prefix;
    private final String suffix;

    public AppendPrependStrategy(boolean isAppend) {
        this.prefix = isAppend ? " " : "";
        this.suffix = isAppend ? "" : " ";
    }

    @Override
    public void modifyTooltip(List<Component> lines, List<Component> componentsToInsert, TooltipEntry entry) {
        int insertIndex = entry.getLineOffset(lines.size());
        if (insertIndex >= 0 && insertIndex < lines.size()) {
            lines.set(insertIndex, TextFormatter.appendToLine(lines.get(insertIndex), componentsToInsert, prefix, suffix));
        }
    }

    @Override
    public Component modifyHeldItemName(Component originalName, List<Component> componentsToInsert, TooltipEntry entry) {
        return entry.lineOffset == 0 ? TextFormatter.appendToLine(originalName, componentsToInsert, prefix, suffix) : originalName;
    }
}