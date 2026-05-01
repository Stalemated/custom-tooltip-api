package com.stalemated.customtooltips.core.text;

import net.minecraft.text.Text;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KeybindParser {
    private static final Pattern KEYBIND_PATTERN = Pattern.compile("<key:([^>]+)>");

    public static String parse(String text) {
        String translatedLine = text.replaceAll("(?i)&([0-9a-fk-or])", "§$1").replace("&&", "&");

        if (translatedLine.contains("<key:")) {
            Matcher matcher = KEYBIND_PATTERN.matcher(translatedLine);
            StringBuilder sb = new StringBuilder();

            while (matcher.find()) {
                String keyId = matcher.group(1);
                String keyText = "[" + Text.keybind(keyId).getString() + "]";
                matcher.appendReplacement(sb, Matcher.quoteReplacement(keyText));
            }
            matcher.appendTail(sb);
            translatedLine = sb.toString();
        }
        return translatedLine;
    }
}