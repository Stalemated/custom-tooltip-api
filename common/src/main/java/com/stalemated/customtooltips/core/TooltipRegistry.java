package com.stalemated.customtooltips.core;

import com.stalemated.customtooltips.config.ConfigManager;
import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.api.CustomTooltipApi;
import com.stalemated.customtooltips.config.TooltipConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TooltipRegistry {

    private static List<TooltipEntry> ACTIVE_ENTRIES = new CopyOnWriteArrayList<>();

    public static synchronized void reload() {
        IconAligner.clearCache();
        List<TooltipEntry> apiEntries = CustomTooltipApi.getApiEntries();
        List<TooltipEntry> newEntries = new ArrayList<>(apiEntries);

        TooltipConfig config = ConfigManager.getConfig();
        if (config != null && config.entries != null) {
            newEntries.addAll(config.entries);
        }

        for (TooltipEntry entry : newEntries) {
            if (entry != null) {
                entry.initCaches();
            }
        }
        
        ACTIVE_ENTRIES = new CopyOnWriteArrayList<>(newEntries);
    }

    public static List<TooltipEntry> getEntries() {
        return ACTIVE_ENTRIES;
    }
}