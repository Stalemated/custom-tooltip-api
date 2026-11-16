package com.stalemated.customtooltips.core;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.api.CustomTooltipApi;
import com.stalemated.customtooltips.config.TooltipConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TooltipRegistry {

    private static List<TooltipEntry> ACTIVE_ENTRIES = new CopyOnWriteArrayList<>();
    private static List<TooltipEntry> userEntries = List.of();

    public static synchronized void reload(TooltipConfig config) {
        userEntries = config.entries != null ? config.entries : List.of();
        reload();
    }

    public static synchronized void reload() {
        IconAligner.clearCache();
        List<TooltipEntry> newEntries = new ArrayList<>(CustomTooltipApi.getApiEntries());
        newEntries.addAll(userEntries);

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
