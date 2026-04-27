package com.stalemated.customtooltips.api;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.core.TooltipRegistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Developer API
 * Allows custom tooltips to be registered directly by just calling the function,
 * without ever touching the JSON5 file or the in-game menu.
 */
public class CustomTooltipApi {

    private static final List<TooltipEntry> API_ENTRIES = new ArrayList<>();

    /**
     * Registers a new TooltipEntry with the Custom Tooltip API.
     * This tooltip will be added dynamically to the active tooltip list.
     *
     * @param entry The TooltipEntry to register.
     */
    public static void registerTooltip(TooltipEntry entry) {
        if (entry != null) {
            entry.apiEntry = true;
            entry.apiEntryId = entry.target;
            API_ENTRIES.add(entry);
            TooltipRegistry.reload();
        }
    }

    /**
     * @return All the entries added through the API
     */
    public static List<TooltipEntry> getApiEntries() {
        return Collections.unmodifiableList(API_ENTRIES);
    }
}