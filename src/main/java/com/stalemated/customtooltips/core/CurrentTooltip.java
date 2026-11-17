package com.stalemated.customtooltips.core;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.lib.util.input.KeyBindingUtil;
import java.util.List;
import net.minecraft.world.item.ItemStack;

import static com.stalemated.customtooltips.registry.KeybindRegistry.holdKeyKeybind;

public final class CurrentTooltip {
    private static TooltipEntry entry;

    private CurrentTooltip() {
    }

    public static TooltipEntry entry() {
        return entry;
    }

    public static void showFor(ItemStack stack) {
        List<TooltipEntry> shown = TooltipRegistry.activeEntries().match(stack, KeyBindingUtil.isKeyDownInGui(holdKeyKeybind)).shown();
        entry = shown.isEmpty() ? null : shown.get(0);
    }

    public static void render(TooltipEntry shownEntry, Runnable render) {
        entry = shownEntry;
        try {
            render.run();
        } finally {
            clear();
        }
    }

    public static void clear() {
        entry = null;
    }
}
