package com.stalemated.customtooltips.core;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.config.TooltipConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.stalemated.customtooltips.CustomTooltips.LOGGER;

public final class TooltipRegistry {

    private static final List<TooltipEntry> API_ENTRIES = new CopyOnWriteArrayList<>();
    private static List<TooltipEntry> userEntries = List.of();
    private static List<String> savedDisabledIds = List.of();
    private static volatile Set<String> disabledIds = Set.of();
    private static volatile ActiveEntries activeEntries = new ActiveEntries(List.of(), Set.of());

    private TooltipRegistry() {
    }

    public static synchronized void registerApiEntry(TooltipEntry entry) {
        if (entry.apiId == null) {
            LOGGER.warn("API tooltip for '{}' has no id, so players who disable it get it back if it changes. Give it one with TooltipBuilder.id(\"modid:name\")", entry.target);
        } else if (API_ENTRIES.stream().anyMatch(registered -> entry.apiId.equals(registered.apiId))) {
            throw new IllegalArgumentException("tooltip id " + entry.apiId + " is already registered");
        }

        entry.apiEntry = true;
        API_ENTRIES.add(entry);
        reload();
    }

    public static List<TooltipEntry> apiEntries() {
        return Collections.unmodifiableList(API_ENTRIES);
    }

    public static boolean isDisabled(TooltipEntry entry) {
        return disabledIds.contains(entry.getIdentifier());
    }

    public static synchronized void reload(TooltipConfig config) {
        userEntries = config.entries != null ? config.entries : List.of();
        savedDisabledIds = config.disabled_entries != null ? new ArrayList<>(config.disabled_entries) : List.of();
        reload();
    }

    private static synchronized void reload() {
        IconAligner.clearCache();
        List<TooltipEntry> entries = new ArrayList<>(API_ENTRIES);
        entries.addAll(userEntries);
        entries.removeIf(Objects::isNull);
        entries.forEach(TooltipEntry::initCaches);

        disabledIds = Set.copyOf(LegacyApiIds.replaceIn(savedDisabledIds, API_ENTRIES));
        activeEntries = new ActiveEntries(entries, disabledIds);
    }

    public static ActiveEntries activeEntries() {
        return activeEntries;
    }
}
