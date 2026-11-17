package com.stalemated.customtooltips.core.text;

import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TooltipTextParserTest {
    //? if fabric {
    @BeforeAll
    static void bootMinecraft() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void aKnownPlaceholderBecomesItsValue() {
        assertEquals(List.of("Durability: 1561"), parse("Durability: %max_durability%"));
    }

    @Test
    void anUnknownPlaceholderStaysAsWritten() {
        assertEquals(List.of("%not_a_placeholder% at 50%"), parse("%not_a_placeholder% at 50%"));
    }

    @Test
    void ampersandCodesBecomeFormattingCodes() {
        String code = String.valueOf(ChatFormatting.PREFIX_CODE);

        assertEquals(List.of(code + "cRed " + code + "Lbold &zplain"), parse("&cRed &Lbold &zplain"));
    }

    @Test
    void aLineBreakSplitsTheLine() {
        assertEquals(List.of("first", "second", "third"), parse("first\nsecond", "third"));
    }

    private static List<String> parse(String... lines) {
        return TooltipTextParser.parseLines(List.of(lines), new ItemStack(Items.DIAMOND_SWORD));
    }
    //?}
}
