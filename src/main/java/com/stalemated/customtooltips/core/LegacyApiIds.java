package com.stalemated.customtooltips.core;

import com.stalemated.customtooltips.TooltipEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class LegacyApiIds {

    private LegacyApiIds() {
    }

    public static List<String> replaceIn(List<String> disabledIds, List<TooltipEntry> apiEntries) {
        Map<String, List<String>> idsByLegacyId = new HashMap<>();
        for (TooltipEntry entry : apiEntries) {
            if (entry.apiId == null) continue;
            idsByLegacyId.computeIfAbsent(entry.legacyApiIdentifier(), legacyId -> new ArrayList<>()).add(entry.apiId);
        }

        Set<String> replaced = new LinkedHashSet<>();
        for (String id : disabledIds) {
            if (id == null) continue;
            replaced.addAll(idsByLegacyId.getOrDefault(id, List.of(id)));
        }
        return new ArrayList<>(replaced);
    }
}
