package com.stalemated.customtooltips.core.text.parser;

import net.minecraft.text.Text;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KeybindParser {
    private static final Pattern KEYBIND_PATTERN = Pattern.compile("<key:([^>]+)>");

    public static String parse(String text) {
        if (!text.contains("<key:")) return text;
        
        Matcher matcher = KEYBIND_PATTERN.matcher(text);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            String keyId = matcher.group(1);
            String keyText = "[" + Text.keybind(keyId).getString() + "]";
            matcher.appendReplacement(sb, Matcher.quoteReplacement(keyText));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}