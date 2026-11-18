package com.stalemated.customtooltips.core;

import com.stalemated.customtooltips.TooltipEntry;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.util.List;

import static com.stalemated.customtooltips.core.TooltipColors.UNCHANGED_OPACITY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class TooltipLookTest {

    @Test
    void anEntryThatOnlyAddsTextDoesNotDecideTheBackground() {
        TooltipEntry red = background(0xFFFF0000);
        TooltipEntry textOnly = new TooltipEntry();

        assertSame(red, TooltipLook.of(List.of(textOnly, red)).background());
        assertSame(red, TooltipLook.of(List.of(red, textOnly)).background());
    }

    @Test
    void theLastEntryThatSetsAPartWins() {
        TooltipEntry gold = background(0xFFFFD700);
        TooltipEntry blue = background(0xFF0000FF);

        assertSame(blue, TooltipLook.of(List.of(gold, blue)).background());
        assertSame(gold, TooltipLook.of(List.of(blue, gold)).background());
    }

    @Test
    void eachPartIsResolvedOnItsOwn() {
        TooltipEntry gold = background(0xFFFFD700);
        TooltipEntry halfOpacity = new TooltipEntry();
        halfOpacity.backgroundOpacity = 0.5f;
        TooltipEntry rarityBorder = new TooltipEntry();
        rarityBorder.borderColors = List.of(new Color(0xFFAA00FF, true), new Color(0xFF5500AA, true));

        TooltipLook look = TooltipLook.of(List.of(gold, halfOpacity, rarityBorder));

        assertSame(gold, look.background());
        assertEquals(0.5f, look.backgroundOpacity());
        assertSame(rarityBorder, look.border());
        assertEquals(UNCHANGED_OPACITY, look.borderOpacity());
        assertNull(TooltipLook.of(List.of(halfOpacity)).background());
    }

    private static TooltipEntry background(int argb) {
        TooltipEntry entry = new TooltipEntry();
        entry.backgroundColors = List.of(new Color(argb, true));
        return entry;
    }
}
