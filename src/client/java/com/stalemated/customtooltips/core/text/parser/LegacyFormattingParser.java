package com.stalemated.customtooltips.core.text.parser;

public class LegacyFormattingParser {
    public static String parse(String text) {
        return text.replaceAll("(?i)&([0-9a-fk-or])", "§$1").replace("&&", "&");
    }
}