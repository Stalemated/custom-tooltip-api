package com.stalemated.customtooltips.core.text;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.api.CustomTooltipApi;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.KeybindResolver;
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
    void aDoubledAmpersandWritesTheCodeAsText() {
        assertEquals(List.of("&c is red, & stays"), parse("&&c is red, & stays"));
    }

    @Test
    void aLineBreakSplitsTheLine() {
        assertEquals(List.of("first", "second", "third"), parse("first\nsecond", "third"));
    }

    @Test
    void aPlaceholderRegisteredAfterTheEntryWasDrawnStillUpdates() {
        TooltipEntry entry = entryWith("Mana: %late_mana%");
        assertEquals("Mana: %late_mana%", firstLine(entry, new ItemStack(Items.STICK)));

        CustomTooltipApi.registerPlaceholder("late_mana", stack -> String.valueOf(stack.getCount()));

        assertEquals("Mana: 3", firstLine(entry, new ItemStack(Items.STICK, 3)));
        assertEquals("Mana: 7", firstLine(entry, new ItemStack(Items.STICK, 7)));
    }

    @Test
    void aKeyNameFollowsTheCurrentBinding() {
        TooltipEntry entry = entryWith("Press <key:key.jump>");
        try {
            KeybindResolver.setKeyResolver(name -> () -> Component.literal("Space"));
            assertEquals("Press [Space]", firstLine(entry, new ItemStack(Items.STICK)));

            KeybindResolver.setKeyResolver(name -> () -> Component.literal("W"));
            assertEquals("Press [W]", firstLine(entry, new ItemStack(Items.STICK)));
        } finally {
            KeybindResolver.setKeyResolver(name -> () -> Component.translatable(name));
        }
    }

    private static List<String> parse(String... lines) {
        return TooltipTextParser.parseLines(List.of(lines), new ItemStack(Items.DIAMOND_SWORD));
    }

    private static TooltipEntry entryWith(String line) {
        TooltipEntry entry = new TooltipEntry();
        entry.text = List.of(line);
        return entry;
    }

    private static String firstLine(TooltipEntry entry, ItemStack stack) {
        return entry.getTextComponents(stack).get(0).getString();
    }
    //?}
}
