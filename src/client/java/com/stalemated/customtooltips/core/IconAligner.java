package com.stalemated.customtooltips.core;

import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class IconAligner {

    private static final int CACHE_CAPACITY = 200;
    private static final Map<String, Text> PROCESSED_CACHE = new LinkedHashMap<String, Text>(16, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Text> eldest) {
            return size() > CACHE_CAPACITY;
        }
    };

    public static void alignIcons(List<Text> lines) {

        for (int i = 0; i < lines.size(); i++) {
            Text originalText = lines.get(i);
            String rawString = originalText.getString();

            if (!containsIcon(rawString)) continue;

            Text cached = PROCESSED_CACHE.get(rawString);
            if (cached != null) {
                lines.set(i, cached);
                continue;
            }

            Text processed = processAndAlignIcon(originalText);
            PROCESSED_CACHE.put(rawString, processed);
            lines.set(i, processed);
        }
    }

    public static void clearCache() {
        PROCESSED_CACHE.clear();
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
        return (codePoint >= 0xF900 && codePoint <= 0xFAFF) || // CJK Compatibility Ideographs (Prom 2 compat)
                (codePoint >= 0xE000 && codePoint <= 0xF8FF) || // Private Use Area
                (codePoint >= 0xF0000 && codePoint <= 0xFFFFF) || // Supplementary Private Use Area-A
                (codePoint >= 0x100000 && codePoint <= 0x10FFFD) || // Supplementary Private Use Area-B
                (codePoint >= 0xDC00 && codePoint <= 0xDFFF) || // Low Surrogate Area
                (codePoint >= 0xD800 && codePoint <= 0xDBFF) || // High Surrogate Area
                (codePoint >= 0x1CC00 && codePoint <= 0x1CEBF) || // Symbols for Legacy Computing Supplement (RPG series icons compat)
                (codePoint >= 0x2700 && codePoint <= 0x27BF) || // Dingbats
                (codePoint >= 0xAB00 && codePoint <= 0xAB2F) || // Ethiopic Extended-A (Eldritch End compat)
                (codePoint >= 0xA980 && codePoint <= 0xA9DF); // Javanese (Eldritch End compat)
    }

    private static Text processAndAlignIcon(Text originalText) {
        List<StyledChar> chars = extractStyledChars(originalText);

        int[] iconBounds = findIconBounds(chars);
        if (iconBounds == null) return originalText;

        int iconStart = iconBounds[0];
        int iconEnd = iconBounds[1];

        int blockStart = iconStart;
        while (blockStart >= 2 && chars.get(blockStart - 2).character.equals("§")) {
            blockStart -= 2;
        }

        int blockEnd = iconEnd;
        while (blockEnd <= chars.size() - 3 && chars.get(blockEnd + 1).character.equals("§") && chars.get(blockEnd + 2).character.equals("r")) {
            blockEnd += 2;
        }

        List<StyledChar> iconBlock = new ArrayList<>(chars.subList(blockStart, blockEnd + 1));
        chars.subList(blockStart, blockEnd + 1).clear();

        chars.addAll(0, iconBlock);

        if (chars.size() > iconBlock.size() && !chars.get(iconBlock.size()).character.isBlank()) {
            chars.add(iconBlock.size(), new StyledChar(" ", Style.EMPTY));
        }

        if (!iconBlockEndsWithReset(iconBlock)) {
            chars.add(iconBlock.size(), new StyledChar("§", Style.EMPTY));
            chars.add(iconBlock.size() + 1, new StyledChar("r", Style.EMPTY));
        }

        cleanResidualSpaces(chars);

        return rebuildText(chars);
    }

    private static List<StyledChar> extractStyledChars(Text text) {
        List<StyledChar> chars = new ArrayList<>();
        text.visit((style, string) -> {

            for (int i = 0; i < string.length(); ) {

                int codePoint = string.codePointAt(i);
                int charCount = Character.charCount(codePoint);

                chars.add(new StyledChar(string.substring(i, i + charCount), style));
                i += charCount;
            }
            return Optional.empty();
        }, Style.EMPTY);
        return chars;
    }

    private static int[] findIconBounds(List<StyledChar> chars) {
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
        return iconStart == -1 ? null : new int[]{iconStart, iconEnd};
    }

    private static boolean iconBlockEndsWithReset(List<StyledChar> block) {
        if (block.size() < 2) return false;
        return block.get(block.size() - 2).character.equals("§") && block.get(block.size() - 1).character.equals("r");
    }

    private static void cleanResidualSpaces(List<StyledChar> chars) {
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

    private static Text rebuildText(List<StyledChar> chars) {
        MutableText result = Text.empty();
        StringBuilder stringBuilder = new StringBuilder();
        Style currentStyle = null;

        for (StyledChar styledChar : chars) {

            if (currentStyle == null) {
                currentStyle = styledChar.style;
            } else if (!currentStyle.equals(styledChar.style)) {
                result.append(Text.literal(stringBuilder.toString()).setStyle(currentStyle));
                currentStyle = styledChar.style;
                stringBuilder.setLength(0);
            }
            stringBuilder.append(styledChar.character);
        }

        if (!stringBuilder.isEmpty()) {
            result.append(Text.literal(stringBuilder.toString()).setStyle(currentStyle));
        }

        return result;
    }

    private static class StyledChar {
        String character;
        Style style;

        public StyledChar(String character, Style style) {
            this.character = character;
            this.style = style;
        }
    }
}