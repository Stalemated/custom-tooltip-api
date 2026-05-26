package com.stalemated.customtooltips.core;

import com.stalemated.customtooltips.ConfigManager;
import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.api.CustomTooltipApi;
import com.stalemated.customtooltips.config.TooltipConfig;

import java.util.ArrayList;
import java.util.List;

public class TooltipRegistry {

    private static final List<TooltipEntry> ACTIVE_ENTRIES = new ArrayList<>();

    public static void reload() {
        ACTIVE_ENTRIES.clear();
        IconAligner.clearCache();
        List<TooltipEntry> apiEntries = CustomTooltipApi.getApiEntries();

        if (apiEntries != null) {
            ACTIVE_ENTRIES.addAll(apiEntries);
        }

        TooltipConfig config = ConfigManager.getConfig();
        if (config != null && config.entries != null) {
            ACTIVE_ENTRIES.addAll(config.entries);
        }

        for (TooltipEntry entry : ACTIVE_ENTRIES) {
            if (entry != null) {
                entry.initCaches();
            }
        }
    }

    public static List<TooltipEntry> getEntries() {
        return ACTIVE_ENTRIES;
    }
}