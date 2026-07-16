package com.stalemated.customtooltips.core.text.parser;

import net.minecraft.text.Text;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TranslationParser {
    private static final Pattern TRANSLATION_PATTERN = Pattern.compile("<translate:([^>]+)>");

    public static String parse(String text) {
        if (!containsTranslation(text)) return text;
        
        Matcher matcher = TRANSLATION_PATTERN.matcher(text);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            String key = matcher.group(1);
            String translatedText = Text.translatable(key).getString();
            matcher.appendReplacement(sb, Matcher.quoteReplacement(translatedText));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static boolean containsTranslation(String text) {
        return text != null && text.contains("<translate:");
    }
}
