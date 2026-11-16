package com.stalemated.customtooltips.gui.screen;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.lib.compat.yacl.SLibTesting;
import com.stalemated.lib.config.InMemoryConfigManager;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

class TooltipEditScreenTest {
    @Test
    void theEditScreenBuilds() throws Exception {
        SLibTesting.assertScreenBuilds(
                TooltipEditScreen.builder(null, new InMemoryConfigManager<>(new TooltipEntry(), TooltipEntry::new, null), List.of("minecraft:default"), List.of()),
                Path.of(TooltipEditScreenTest.class.getResource("/assets/custom-tooltip-api/lang/en_us.json").toURI())
        );
    }
}
