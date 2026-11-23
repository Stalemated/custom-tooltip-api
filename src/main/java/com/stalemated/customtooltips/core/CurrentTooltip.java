package com.stalemated.customtooltips.core;

import com.stalemated.lib.util.state.SharedTooltipState;
import net.minecraft.world.item.ItemStack;

public final class CurrentTooltip {
    private static TooltipLook look = TooltipLook.VANILLA;

    private CurrentTooltip() {
    }

    public static TooltipLook look() {
        return look;
    }

    public static TooltipLook lookFor(ItemStack stack) {
        return TooltipLook.of(TooltipRegistry.activeEntries().match(stack, HoldKey.isDown()).shown());
    }

    public static void render(TooltipLook shownLook, Runnable render) {
        TooltipLook previous = look;
        look = shownLook;
        try {
            render.run();
        } finally {
            look = previous;
        }
    }

    public static Runnable drawLater(Runnable render) {
        TooltipLook shownLook = look;
        boolean customDimensions = SharedTooltipState.isCustomDimensionsRequested();
        return () -> render(shownLook, customDimensions ? () -> SharedTooltipState.withCustomDimensions(render) : render);
    }
}
