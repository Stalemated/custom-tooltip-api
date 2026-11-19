package com.stalemated.customtooltips.core;

import com.stalemated.customtooltips.TooltipEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.world.item.ItemStack;

public final class ActiveEntries {
    private final List<TooltipEntry> entries;

    ActiveEntries(List<TooltipEntry> entries, Set<String> disabledIds) {
        this.entries = entries.stream()
                .filter(entry -> !disabledIds.contains(entry.getIdentifier()))
                .toList();
    }

    public boolean isEmpty() {
        return this.entries.isEmpty();
    }

    public MatchedEntries match(ItemStack stack, boolean holdKeyDown) {
        List<TooltipEntry> shown = new ArrayList<>();
        boolean waitingForKey = false;

        for (TooltipEntry entry : this.entries) {
            if (!entry.matches(stack) || !entry.areItemConditionsMet(stack)) continue;

            if (entry.requireKeybind && !holdKeyDown) waitingForKey = true;
            else shown.add(entry);
        }

        return new MatchedEntries(List.copyOf(shown), waitingForKey);
    }
}
