package com.stalemated.customtooltips.core.sorting;

import com.stalemated.customtooltips.config.TooltipConfig;
import com.stalemated.lib.config.LocalConfigManager;
import com.stalemated.lib.config.SLibConfig;
import com.stalemated.lib.config.io.ConfigLoadFailure;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SortModeTest {
    @TempDir
    Path folder;

    @Test
    void aSavedCreationDateSortLoadsAsManualOrderWithoutALoadFailure() throws IOException {
        Path file = folder.resolve("config.json5");
        Files.writeString(file, "{ \"sort_mode\": \"CREATION_DATE\" }");
        List<ConfigLoadFailure> failures = new ArrayList<>();
        LocalConfigManager<TooltipConfig> config = SLibConfig.local("custom_tooltip_api", TooltipConfig.class)
                .configPath(file)
                .onLoadFailed(failures::add)
                .build();

        config.register();
        config.save();

        assertEquals(SortMode.MANUAL_ORDER, config.get().sort_mode);
        assertEquals(List.of(), failures);
        assertTrue(Files.readString(file).contains("\"MANUAL_ORDER\""));
    }
}
