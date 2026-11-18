package com.stalemated.customtooltips.core;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.api.enums.BackgroundType;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static com.stalemated.lib.util.color.VanillaTooltipColors.BACKGROUND_GRADIENT;
import static com.stalemated.lib.util.color.VanillaTooltipColors.BACKGROUND_OPACITY;
import static com.stalemated.lib.util.color.VanillaTooltipColors.BORDER;

public record TooltipLook(TooltipEntry background, int backgroundOpacity, TooltipEntry border, int borderOpacity) {
    public static final TooltipLook VANILLA = new TooltipLook(null, BACKGROUND_OPACITY, null, BACKGROUND_OPACITY);

    private static final Set<BackgroundType> TEXTURED = EnumSet.of(BackgroundType.TEXTURE, BackgroundType.SIMPLE_TEXTURE, BackgroundType.REPEATING_TEXTURE);

    public static TooltipLook of(List<TooltipEntry> shown) {
        TooltipEntry background = null;
        int backgroundOpacity = BACKGROUND_OPACITY;
        TooltipEntry border = null;
        int borderOpacity = BACKGROUND_OPACITY;

        for (TooltipEntry entry : shown) {
            if (setsBackground(entry)) background = entry;
            if (entry.backgroundOpacity != BACKGROUND_OPACITY) backgroundOpacity = entry.backgroundOpacity;
            if (setsBorderColors(entry)) border = entry;
            if (entry.borderOpacity != BACKGROUND_OPACITY) borderOpacity = entry.borderOpacity;
        }
        return new TooltipLook(background, backgroundOpacity, border, borderOpacity);
    }

    public static boolean setsBackground(TooltipEntry entry) {
        BackgroundType type = entry.backgroundType;
        if (type == null) return false;
        if (TEXTURED.contains(type)) return !entry.backgroundTexture.isEmpty();
        if (entry.backgroundColors.isEmpty()) return false;

        TooltipRuntimeCache cache = entry.runtimeCache();
        if (type == BackgroundType.SOLID) return cache.backgroundColorStart() != BACKGROUND_GRADIENT.get(0);
        return !List.of(cache.backgroundColorStart(), cache.backgroundColorEnd()).equals(BACKGROUND_GRADIENT);
    }

    public static boolean setsBorderColors(TooltipEntry entry) {
        if (entry.borderColors.isEmpty()) return false;

        TooltipRuntimeCache cache = entry.runtimeCache();
        return !List.of(cache.borderColorStart(), cache.borderColorEnd()).equals(BORDER);
    }

    public boolean changesBackground() {
        return this.background != null || this.backgroundOpacity != BACKGROUND_OPACITY;
    }

    public BackgroundType backgroundType() {
        return this.background != null ? this.background.backgroundType : BackgroundType.SOLID;
    }
}
