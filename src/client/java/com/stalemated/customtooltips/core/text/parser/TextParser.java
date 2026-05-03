package com.stalemated.customtooltips.core.text.parser;

import net.minecraft.item.ItemStack;

public class TextParser {
    public static String parse(String text, ItemStack stack) {
        String processed = text;
        processed = LegacyFormattingParser.parse(processed);
        processed = KeybindParser.parse(processed);
        processed = PlaceholderParser.parse(processed, stack);
        return processed;
    }
}