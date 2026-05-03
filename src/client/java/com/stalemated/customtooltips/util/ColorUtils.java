package com.stalemated.customtooltips.util;

import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import java.awt.Color;
import java.util.Locale;

public class ColorUtils {

    private static TextColor resolveTextColor(String colorStr) {
        if (colorStr == null || colorStr.isEmpty()) return null;

        String lowerColor = colorStr.toLowerCase(Locale.ROOT);

        // Legacy code (e.g., "&c")
        if (lowerColor.length() == 2 && lowerColor.charAt(0) == '&') {
            Formatting format = Formatting.byCode(lowerColor.charAt(1));
            if (format != null && format.getColorValue() != null) {
                return TextColor.fromFormatting(format);
            }
        }

        // Formatting name (e.g., "red")
        Formatting format = Formatting.byName(lowerColor);
        if (format != null && format.getColorValue() != null) {
            return TextColor.fromFormatting(format);
        }

        // Hex code
        String hex = colorStr;
        if (hex.startsWith("#")) hex = hex.substring(1);
        else if (hex.startsWith("0x") || hex.startsWith("0X")) hex = hex.substring(2);
        else if (hex.startsWith("x") || hex.startsWith("X")) hex = hex.substring(1);

        if (hex.matches("^[0-9a-fA-F]{6}$")) {
            return TextColor.parse("#" + hex);
        }

        return null;
    }

    public static boolean isInvalidColorCode(String color) {
        return resolveTextColor(color) == null;
    }

    public static Color parseToAWT(String colorStr) {
        TextColor textColor = resolveTextColor(colorStr);
        return textColor != null ? new Color(textColor.getRgb()) : Color.GRAY;
    }

    public static int parseColor(String colorStr) {
        TextColor color = resolveTextColor(colorStr);
        return color != null ? color.getRgb() : 0xFFFFFF;
    }
}
