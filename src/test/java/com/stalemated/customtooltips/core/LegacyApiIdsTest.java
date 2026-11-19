package com.stalemated.customtooltips.core;

import com.stalemated.customtooltips.TooltipEntry;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LegacyApiIdsTest {
    private static final String USER_ENTRY = "2f1c6e0a-5d4b-4c1e-9a7f-3b8d2e6c1a90";

    @Test
    void theOldIdOfAnApiTooltipKeepsItsFormat() {
        assertEquals("minecraft:stick:1115202248", stickLore().legacyApiIdentifier());
    }

    @Test
    void aDisabledApiTooltipStaysDisabledAfterItGetsAnId() {
        TooltipEntry first = stickLore();
        first.apiId = "mymod:first_lore";
        TooltipEntry second = stickLore();
        second.apiId = "mymod:second_lore";
        String oldId = first.legacyApiIdentifier();

        List<String> disabled = LegacyApiIds.replaceIn(List.of(USER_ENTRY, oldId), List.of(first, second));

        assertEquals(List.of(USER_ENTRY, "mymod:first_lore", "mymod:second_lore"), disabled);
    }

    private static TooltipEntry stickLore() {
        TooltipEntry entry = new TooltipEntry();
        entry.target = "minecraft:stick";
        entry.text.add("Old lore");
        entry.apiEntry = true;
        return entry;
    }
}
