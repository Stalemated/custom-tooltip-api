package com.stalemated.customtooltips.core;

import com.stalemated.customtooltips.TooltipEntry;
import java.util.List;
import net.minecraft.world.item.ItemStack;

public final class CurrentTooltip {
    private static TooltipEntry entry;

    private CurrentTooltip() {
    }

    public static TooltipEntry entry() {
        return entry;
    }

    public static TooltipEntry entryFor(ItemStack stack) {
        List<TooltipEntry> shown = TooltipRegistry.activeEntries().match(stack, HoldKey.isDown()).shown();
        return shown.isEmpty() ? null : shown.get(0);
    }

    public static void render(TooltipEntry shownEntry, Runnable render) {
        entry = shownEntry;
        try {
            render.run();
        } finally {
            entry = null;
        }
    }
}
