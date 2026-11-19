package com.stalemated.customtooltips.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IconAlignerTest {

    @Test
    void twoLinesWithTheSameTextKeepTheirOwnColors() {
        List<Component> lines = new ArrayList<>(List.of(lineWithIcon(ChatFormatting.RED), lineWithIcon(ChatFormatting.BLUE)));

        IconAligner.alignIcons(lines);

        assertEquals(TextColor.fromLegacyFormat(ChatFormatting.RED), colorOf("Damage", lines.get(0)));
        assertEquals(TextColor.fromLegacyFormat(ChatFormatting.BLUE), colorOf("Damage", lines.get(1)));
    }

    private static Component lineWithIcon(ChatFormatting color) {
        return Component.literal("Damage ✔").withStyle(color);
    }

    private static TextColor colorOf(String word, Component line) {
        return line.visit((style, text) -> text.contains(word) ? Optional.ofNullable(style.getColor()) : Optional.empty(), Style.EMPTY).orElseThrow();
    }
}
