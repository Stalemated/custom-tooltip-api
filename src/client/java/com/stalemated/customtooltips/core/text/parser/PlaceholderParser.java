package com.stalemated.customtooltips.core.text.parser;

import com.stalemated.customtooltips.registry.PlaceholderRegistry;
import net.minecraft.item.ItemStack;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderParser {
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("%([a-zA-Z_]+)%");

    public static String parse(String text, ItemStack stack) {
        if (!text.contains("%")) return text;

        Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            String key = matcher.group(1);
            Function<ItemStack, String> replacer = PlaceholderRegistry.getProvider(key);
            
            if (replacer != null) matcher.appendReplacement(sb, Matcher.quoteReplacement(replacer.apply(stack)));
            else matcher.appendReplacement(sb, Matcher.quoteReplacement(matcher.group(0)));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static boolean containsDynamicPlaceholders(String text) {
        if (text == null || !text.contains("%")) return false;
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
        while (matcher.find()) {
            if (PlaceholderRegistry.contains(matcher.group(1))) return true;
        }
        return false;
    }
}