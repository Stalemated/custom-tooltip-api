package com.stalemated.customtooltips.api;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.core.TooltipRegistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The main Developer API for Custom Tooltip API.
 * <p>
 * Allows custom tooltips to be registered dynamically from other mods by calling these methods,
 * completely bypassing the JSON5 configuration file and the in-game settings menu.
 */
public class CustomTooltipApi {

    private static final List<TooltipEntry> API_ENTRIES = new ArrayList<>();

    /**
     * Registers a built {@link TooltipEntry} into the active tooltip registry.
     * Tooltips registered this way are volatile and will be applied instantly.
     * <p>
     * <b>Tip:</b> You can use {@code TooltipEntry.builder(...).register()} to build and register in a single chain.
     *
     * @param entry The TooltipEntry object to register.
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
     * Retrieves an unmodifiable list of all tooltips registered dynamically via the API.
     *
     * @return A list containing all API-registered TooltipEntries.
     */
    public static List<TooltipEntry> getApiEntries() {
        return Collections.unmodifiableList(API_ENTRIES);
    }
}