package com.stalemated.customtooltips.core.text;

import com.stalemated.customtooltips.registry.PlaceholderRegistry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public final class TooltipTextParser {
    private static final Pattern FORMATTING_CODE = Pattern.compile("(?i)&([0-9a-fk-or])");
    private static final Pattern TRANSLATION = Pattern.compile("<translate:([^>]+)>");
    private static final Pattern KEYBIND = Pattern.compile("<key:([^>]+)>");
    private static final Pattern PLACEHOLDER = Pattern.compile("%([a-zA-Z_]+)%");
    private static final List<Pattern> CHANGING_PARTS = List.of(TRANSLATION, KEYBIND, PLACEHOLDER);

    private TooltipTextParser() {
    }

    public static List<String> parseLines(List<String> rawLines, ItemStack stack) {
        List<String> lines = new ArrayList<>();
        for (String rawLine : rawLines) {
            if (rawLine == null || rawLine.isEmpty()) continue;
            lines.addAll(Arrays.asList(parse(rawLine, stack).split("\n")));
        }
        return lines;
    }

    public static boolean isDynamic(String line) {
        if (line == null) return false;
        return CHANGING_PARTS.stream().anyMatch(part -> part.matcher(line).find());
    }

    private static String parse(String text, ItemStack stack) {
        String formatted = FORMATTING_CODE.matcher(text).replaceAll(ChatFormatting.PREFIX_CODE + "$1").replace("&&", "&");
        String translated = TRANSLATION.matcher(formatted).replaceAll(match -> Matcher.quoteReplacement(Component.translatable(match.group(1)).getString()));
        String withKeys = KEYBIND.matcher(translated).replaceAll(match -> Matcher.quoteReplacement("[" + Component.keybind(match.group(1)).getString() + "]"));
        return PLACEHOLDER.matcher(withKeys).replaceAll(match -> Matcher.quoteReplacement(placeholderValue(match.group(1), match.group(), stack)));
    }

    private static String placeholderValue(String name, String asWritten, ItemStack stack) {
        Function<ItemStack, String> provider = PlaceholderRegistry.getProvider(name);
        return provider != null ? provider.apply(stack) : asWritten;
    }
}
