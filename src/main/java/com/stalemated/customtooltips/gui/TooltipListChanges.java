package com.stalemated.customtooltips.gui;

import com.stalemated.customtooltips.config.CustomTooltipsConfigRegistry;
import com.stalemated.customtooltips.config.TooltipConfig;
import java.io.UncheckedIOException;
import java.util.function.Consumer;

import static com.stalemated.customtooltips.CustomTooltips.LOGGER;

public final class TooltipListChanges {
    private TooltipListChanges() {
    }

    public static void save(Consumer<TooltipConfig> change) {
        try {
            CustomTooltipsConfigRegistry.CONFIG.modify(change);
        } catch (UncheckedIOException e) {
            LOGGER.error("Kept a tooltip list change in memory: {}", e.getMessage());
            TooltipListToasts.showSaveFailedToast();
        }
    }
}
