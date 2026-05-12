package com.stalemated.customtooltips.util;

import com.stalemated.customtooltips.TooltipEntry;
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
        Integer argb = resolveARGBColor(colorStr);
        return argb != null ? new Color(argb, true) : Color.WHITE;
    }

    public static int parseColor(String colorStr) {
        TextColor color = resolveTextColor(colorStr);
        return color != null ? color.getRgb() : TooltipEntry.DEFAULT_COLOR;
    }

    public static boolean isInvalidARGBColor(String color, int index) {
        return parseARGBColor(color, index) == null;
    }

    public static Integer parseARGBColor(String colorStr, int index) {
        Integer color = resolveARGBColor(colorStr);
        return color != null ? color : TooltipEntry.DEFAULT_BORDER_COLORS.get(index);
    }

    public static Integer resolveARGBColor(String colorStr) {
        if (colorStr == null || colorStr.trim().isEmpty()) return null;

        String lowerColor = colorStr.trim().toLowerCase(Locale.ROOT);

        String hex = colorStr.trim();
        if (hex.startsWith("#")) hex = hex.substring(1);
        else if (hex.startsWith("0x") || hex.startsWith("0X")) hex = hex.substring(2);
        else if (hex.startsWith("x") || hex.startsWith("X")) hex = hex.substring(1);

        if (hex.matches("^[0-9a-fA-F]{8}$")) {
            return (int) Long.parseLong(hex, 16);
        }

        if (hex.matches("^[0-9a-fA-F]{6}$")) {
            return (0xFF << 24) | Integer.parseInt(hex, 16);
        }

        TextColor textColor = resolveTextColor(lowerColor);
        if (textColor != null) {
            return (0xFF << 24) | textColor.getRgb();
        }

        return null;
    }
}
