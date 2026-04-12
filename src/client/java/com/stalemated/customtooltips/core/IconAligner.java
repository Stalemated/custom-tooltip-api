package com.stalemated.customtooltips.core;

import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IconAligner {

    public static void alignIcons(List<Text> lines) {

        for (int i = 0; i < lines.size(); i++) {
            Text original = lines.get(i);

            if (!containsIcon(original.getString())) continue;

            lines.set(i, shiftIconToStart(original));
        }
    }

    private static boolean containsIcon(String str) {
        for (int i = 0; i < str.length(); ) {
            int codePoint = str.codePointAt(i);
            if (isIconCodePoint(codePoint)) return true;
            i += Character.charCount(codePoint);
        }
        return false;
    }

    private static boolean isIconCodePoint(int codePoint) {
        return (codePoint >= 0xE000 && codePoint <= 0xF8FF) || // Surrogates
                (codePoint >= 0xF0000 && codePoint <= 0x10FFFF) || // Private Use Area
                (codePoint >= 0x1CD00 && codePoint <= 0x1CDEF) || // Private Use Area Extension
                (codePoint >= 0x2700 && codePoint <= 0x27BF); // Misc Symbols
    }

    private static class StyledChar {
        String character;
        Style style;
        public StyledChar(String character, Style style) {
            this.character = character;
            this.style = style;
        }
    }

    private static Text shiftIconToStart(Text originalText) {
        List<StyledChar> chars = new ArrayList<>();

        originalText.visit((style, string) -> {
            for (int i = 0; i < string.length(); ) {

                int codePoint = string.codePointAt(i);
                int charCount = Character.charCount(codePoint);

                chars.add(new StyledChar(string.substring(i, i + charCount), style));
                i += charCount;
            }
            return Optional.empty();
        }, Style.EMPTY);

        int iconStart = -1;
        int iconEnd = -1;

        for (int i = 0; i < chars.size(); i++) {
            int codePoint = chars.get(i).character.codePointAt(0);

            if (isIconCodePoint(codePoint)) {
                if (iconStart == -1) iconStart = i;
                iconEnd = i;
            } else if (iconStart != -1) {
                break;
            }
        }

        if (iconStart != -1) {
            List<StyledChar> iconChars = new ArrayList<>(chars.subList(iconStart, iconEnd + 1));
            chars.subList(iconStart, iconEnd + 1).clear();

            for (StyledChar iconChar : iconChars) {
                iconChar.style = iconChar.style.withColor(Formatting.WHITE);
            }

            chars.addAll(0, iconChars);

            if (chars.size() > iconChars.size()) {
                if (!chars.get(iconChars.size()).character.isBlank()) {
                    chars.add(iconChars.size(), new StyledChar(" ", Style.EMPTY));
                }
            }

            boolean lastWasSpace = false;
            for (int i = 0; i < chars.size(); i++) {
                String c = chars.get(i).character;

                if (c.equals("§") && i + 1 < chars.size()) {
                    i++;
                    continue;
                }

                if (c.isBlank()) {
                    if (lastWasSpace) {
                        chars.remove(i);
                        i--;
                    } else {
                        lastWasSpace = true;
                    }
                } else {
                    lastWasSpace = false;
                }
            }
        }

        MutableText result = Text.empty();
        StringBuilder stringBuilder = new StringBuilder();
        Style currentStyle = null;

        for (StyledChar styledChar : chars) {

            if (currentStyle == null) {
                currentStyle = styledChar.style;
                stringBuilder.append(styledChar.character);
            } else if (currentStyle.equals(styledChar.style)) {
                stringBuilder.append(styledChar.character);
            } else {
                result.append(Text.literal(stringBuilder.toString()).setStyle(currentStyle));
                currentStyle = styledChar.style;
                stringBuilder.setLength(0);
                stringBuilder.append(styledChar.character);
            }
        }
        if (!stringBuilder.isEmpty()) {
            result.append(Text.literal(stringBuilder.toString()).setStyle(currentStyle));
        }

        return result;
    }
}