package com.stalemated.customtooltips.gui;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.lib.config.InMemoryConfigManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TooltipClipboardTest {
    private final InMemoryConfigManager<TooltipEntry> config = new InMemoryConfigManager<>(new TooltipEntry(), TooltipEntry::new, null);

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "not json",
            "[1, 2]",
            "{\"target\": null}",
            "{\"font\": \"Bad Font\"}",
            "{\"font\": {}}",
            "{\"uuid\": \"not-a-uuid\"}",
            "{\"borderColors\": [\"not a color\"]}"
    })
    void textThatIsNotATooltipEntryIsRejected(String clipboard) {
        assertTrue(TooltipClipboard.read(clipboard, config).isEmpty());
    }

    @Test
    void aCopiedEntryPastesAsANewEntry() {
        TooltipEntry copied = new TooltipEntry();
        copied.target = "minecraft:stick";

        TooltipEntry pasted = TooltipClipboard.read(config.toJson(copied), config).orElseThrow();

        assertEquals("minecraft:stick", pasted.target);
        assertNotEquals(copied.uuid, pasted.uuid);
    }
}
