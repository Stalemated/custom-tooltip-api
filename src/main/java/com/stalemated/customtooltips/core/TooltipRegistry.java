package com.stalemated.customtooltips.core;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.config.TooltipConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public final class TooltipRegistry {

    private static final List<TooltipEntry> API_ENTRIES = new CopyOnWriteArrayList<>();
    private static List<TooltipEntry> userEntries = List.of();
    private static Set<String> disabledIds = Set.of();
    private static volatile ActiveEntries activeEntries = new ActiveEntries(List.of(), Set.of());

    private TooltipRegistry() {
    }

    public static void registerApiEntry(TooltipEntry entry) {
        entry.apiEntry = true;
        entry.apiEntryId = entry.target;
        API_ENTRIES.add(entry);
        reload();
    }

    public static List<TooltipEntry> apiEntries() {
        return Collections.unmodifiableList(API_ENTRIES);
    }

    public static synchronized void reload(TooltipConfig config) {
        userEntries = config.entries != null ? config.entries : List.of();
        disabledIds = config.disabled_entries != null ? new HashSet<>(config.disabled_entries) : Set.of();
        reload();
    }

    private static synchronized void reload() {
        IconAligner.clearCache();
        List<TooltipEntry> entries = new ArrayList<>(API_ENTRIES);
        entries.addAll(userEntries);
        entries.removeIf(Objects::isNull);
        entries.forEach(TooltipEntry::initCaches);

        activeEntries = new ActiveEntries(entries, disabledIds);
    }

    public static ActiveEntries activeEntries() {
        return activeEntries;
    }
}
