package com.stalemated.customtooltips.util;

import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import java.awt.*;
import java.util.Locale;

public class ColorUtils {
    public static boolean isValidColorCode(String color) {
        if (color == null || color.isEmpty()) return false;

        if (isPureHex(color)) return true;
        if (isLegacyCode(color)) return true;

        return Formatting.byName(color.toUpperCase(Locale.ROOT)) != null;
    }

    public static boolean isPureHex(String color) {
        if (color == null || color.isEmpty()) return false;
        return color.matches("^(#|0x|x|0X|X)?([0-9a-fA-F]{6})$");
    }

    public static boolean isLegacyCode(String color) {
        if (color == null || color.isEmpty()) return false;
        return color.matches("^(&)([0-9a-fA-F])$");
    }

    public static Color parseToAWT(String colorStr) {
        if (colorStr == null || colorStr.isEmpty()) return Color.GRAY;

        if (ColorUtils.isPureHex(colorStr)) {
            String hex = colorStr.replaceAll("^(#|0x|x|0X|X)", "");
            return new Color(Integer.parseInt(hex, 16));
        }

        Formatting format = Formatting.byName(colorStr.toUpperCase(Locale.ROOT));
        if (format != null && format.getColorValue() != null) {
            return new Color(format.getColorValue());
        }

        if (ColorUtils.isLegacyCode(colorStr)) {
            Formatting legacy = Formatting.byCode(colorStr.toLowerCase(Locale.ROOT).charAt(1));
            if (legacy != null && legacy.getColorValue() != null) return new Color(legacy.getColorValue());
        }

        return Color.GRAY;
    }

    public static int parseColor(String colorStr) {
        TextColor color = resolveTextColor(colorStr);
        return color != null ? color.getRgb() : 0xFFFFFF;
    }

    public static TextColor resolveTextColor(String colorStr) {
        if (colorStr == null || colorStr.isEmpty()) return null;

        if (colorStr.length() == 2 && colorStr.charAt(0) == '&') {
            Formatting format = Formatting.byCode(Character.toLowerCase(colorStr.charAt(1)));
            if (format != null && format.getColorValue() != null) {
                return TextColor.fromFormatting(format);
            }
        }

        Formatting format = Formatting.byName(colorStr.toLowerCase(Locale.ROOT));
        if (format != null && format.getColorValue() != null) {
            return TextColor.fromFormatting(format);
        }

        String hex = colorStr;
        if (hex.startsWith("#")) hex = hex.substring(1);
        else if (hex.startsWith("0x") || hex.startsWith("0X")) hex = hex.substring(2);
        else if (hex.startsWith("x") || hex.startsWith("X")) hex = hex.substring(1);

        if (hex.matches("^[0-9a-fA-F]{6}$")) {
            return TextColor.parse("#" + hex);
        }

        return null;
    }
}
