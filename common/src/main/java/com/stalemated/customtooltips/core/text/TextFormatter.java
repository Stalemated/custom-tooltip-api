package com.stalemated.customtooltips.core.text;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.api.enums.TooltipStyle;
import com.stalemated.customtooltips.core.text.parser.TextParser;
import com.stalemated.lib.util.math.MathUtils;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TextFormatter {

    public static List<Text> getOrGenerateComponents(TooltipEntry entry, ItemStack stack) {
        boolean isStatic = (entry.style == TooltipStyle.SOLID || entry.style == TooltipStyle.STATIC_GRADIENT) && entry.dynamicTextProvider == null && !entry.hasDynamicText;
        if (isStatic && entry.getCachedStaticText() != null) return entry.getCachedStaticText();

        List<Text> linesList = new ArrayList<>();
        List<String> rawText = entry.dynamicTextProvider != null ? entry.dynamicTextProvider.apply(stack) : entry.text;
        if (rawText == null) return linesList;

        List<String> parsedLines = TextParser.parseAll(rawText, stack);

        for (String parsedLine : parsedLines) {
            MutableText processedText = StyleApplier.apply(Text.literal(parsedLine), entry);
            StyleApplier.applyModifiers(processedText, entry.getCachedStyleModifier());

            linesList.add(processedText);
        }

        if (isStatic) entry.setCachedStaticText(linesList);
        return linesList;
    }

    public static void insertLines(List<Text> destination, List<Text> source, int startIndex, int sourceOffset) {
        int currentIndex = startIndex;
        for (int i = sourceOffset; i < source.size(); i++) {
            currentIndex = MathUtils.clamp(currentIndex, 0, destination.size());
            destination.add(currentIndex, source.get(i));
            currentIndex++;
        }
    }

    public static MutableText appendToLine(Text baseLine, List<Text> components, String prefix, String suffix) {
        if (components.isEmpty()) return baseLine.copy();

        MutableText modified = suffix.equals(" ") ? Text.empty() : baseLine.copy();
        for (Text component : components) {
            if (!prefix.isEmpty()) modified.append(Text.literal(prefix));
            modified.append(component);
            if (!suffix.isEmpty()) modified.append(Text.literal(suffix));
        }
        if (suffix.equals(" ")) modified.append(baseLine);
        return modified;
    }

    public static void replaceLine(List<Text> lines, List<Text> componentsToInsert, int index) {
        if (lines.isEmpty()) {
            insertLines(lines, componentsToInsert, 0, 0);
            return;
        }
        lines.set(index, componentsToInsert.getFirst());
        TextFormatter.insertLines(lines, componentsToInsert, index + 1, 1);
    }
}