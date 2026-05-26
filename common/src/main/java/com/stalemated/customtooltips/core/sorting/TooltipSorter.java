package com.stalemated.customtooltips.core.sorting;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.config.TooltipConfig;

import java.util.List;

public class TooltipSorter {

    public static void sort(List<TooltipEntry> entries, TooltipConfig config) {
        SortMode sortMode = config.sort_mode;

        if (sortMode == SortMode.NAME_AND_TAG) {
            entries.sort((a, b) -> {
                String targetA = a.target == null ? "" : a.target;
                String targetB = b.target == null ? "" : b.target;

                boolean isTagA = targetA.startsWith("#");
                boolean isTagB = targetB.startsWith("#");

                if (isTagA && !isTagB) return -1;
                if (!isTagA && isTagB) return 1;

                return targetA.compareToIgnoreCase(targetB);
            });
        } else if (sortMode == SortMode.DISABLED_FIRST) {
            entries.sort((a, b) -> {
                String targetA = a.target == null ? "" : a.target;
                String targetB = b.target == null ? "" : b.target;

                boolean disabledA = config.disabled_entries.contains(a.getIdentifier());
                boolean disabledB = config.disabled_entries.contains(b.getIdentifier());

                if (disabledA && !disabledB) return -1;
                if (!disabledA && disabledB) return 1;

                return (targetA).compareToIgnoreCase(targetB);
            });
        }
    }
}