package com.stalemated.customtooltips.core.text;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.api.enums.TooltipStyle;
import com.stalemated.customtooltips.core.text.parser.TextParser;
import com.stalemated.lib.util.math.MathUtils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

public class TextFormatter {

    public static List<Component> getOrGenerateComponents(TooltipEntry entry, ItemStack stack) {
        boolean isStatic = (entry.style == TooltipStyle.SOLID || entry.style == TooltipStyle.STATIC_GRADIENT) && entry.dynamicTextProvider == null && !entry.hasDynamicText();
        if (isStatic && entry.getCachedStaticText() != null) return entry.getCachedStaticText();

        List<Component> linesList = new ArrayList<>();
        List<String> rawText = entry.dynamicTextProvider != null ? entry.dynamicTextProvider.apply(stack) : entry.text;
        if (rawText == null) return linesList;

        List<String> parsedLines = TextParser.parseAll(rawText, stack);

        for (String parsedLine : parsedLines) {
            MutableComponent processedText = StyleApplier.apply(Component.literal(parsedLine), entry);
            StyleApplier.applyModifiers(processedText, entry.getCachedStyleModifier());

            linesList.add(processedText);
        }

        if (isStatic) entry.setCachedStaticText(linesList);
        return linesList;
    }

    public static void insertLines(List<Component> destination, List<Component> source, int startIndex, int sourceOffset) {
        int currentIndex = startIndex;
        for (int i = sourceOffset; i < source.size(); i++) {
            currentIndex = MathUtils.clamp(currentIndex, 0, destination.size());
            destination.add(currentIndex, source.get(i));
            currentIndex++;
        }
    }

    public static MutableComponent appendToLine(Component baseLine, List<Component> components, String prefix, String suffix) {
        if (components.isEmpty()) return baseLine.copy();

        MutableComponent modified = suffix.equals(" ") ? Component.empty() : baseLine.copy();
        for (Component component : components) {
            if (!prefix.isEmpty()) modified.append(Component.literal(prefix));
            modified.append(component);
            if (!suffix.isEmpty()) modified.append(Component.literal(suffix));
        }
        if (suffix.equals(" ")) modified.append(baseLine);
        return modified;
    }

    public static void replaceLine(List<Component> lines, List<Component> componentsToInsert, int index) {
        if (lines.isEmpty()) {
            insertLines(lines, componentsToInsert, 0, 0);
            return;
        }
        lines.set(index, componentsToInsert.get(0));
        TextFormatter.insertLines(lines, componentsToInsert, index + 1, 1);
    }
}