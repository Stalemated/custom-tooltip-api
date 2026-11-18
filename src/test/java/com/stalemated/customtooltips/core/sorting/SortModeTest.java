package com.stalemated.customtooltips.core.sorting;

import com.stalemated.customtooltips.config.TooltipConfig;
import com.stalemated.lib.config.InMemoryConfigManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SortModeTest {

    @Test
    void aSavedCreationDateSortLoadsAsManualOrder() {
        InMemoryConfigManager<TooltipConfig> config = new InMemoryConfigManager<>(new TooltipConfig(), TooltipConfig::new, null);

        TooltipConfig loaded = config.fromJson("{\"sort_mode\": \"CREATION_DATE\"}", TooltipConfig.class);

        assertEquals(SortMode.MANUAL_ORDER, loaded.sort_mode);
        assertEquals("\"MANUAL_ORDER\"", config.toJson(SortMode.MANUAL_ORDER));
    }
}
