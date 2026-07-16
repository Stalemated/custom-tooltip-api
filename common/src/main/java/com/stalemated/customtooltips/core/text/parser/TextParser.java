package com.stalemated.customtooltips.core.text.parser;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TextParser {
    public static String parse(String text, ItemStack stack) {
        String processed = text;
        processed = LegacyFormattingParser.parse(processed);
        processed = TranslationParser.parse(processed);
        processed = KeybindParser.parse(processed);
        processed = PlaceholderParser.parse(processed, stack);
        return processed;
    }

    public static List<String> parseAll(List<String> rawLines, ItemStack stack) {
        List<String> processedLines = new ArrayList<>();
        for (String line : rawLines) {
            if (line == null || line.isEmpty()) continue;

            String parsed = parse(line, stack);
            if (parsed.contains("\n")) {
                processedLines.addAll(Arrays.asList(parsed.split("\n")));
            } else {
                processedLines.add(parsed);
            }
        }
        return processedLines;
    }
}