package com.stalemated.customtooltips.gui;

import com.stalemated.customtooltips.config.CustomTooltipsConfigRegistry;

public final class DoubleClickSelection {
    private static final long MAX_INTERVAL_MS = 250;

    private long lastClickMs;

    public static boolean isEnabled() {
        return CustomTooltipsConfigRegistry.CONFIG.get().enable_double_click_selection;
    }

    public boolean selectsAll() {
        boolean doubleClick = isDoubleClick(System.currentTimeMillis());
        return doubleClick && isEnabled();
    }

    boolean isDoubleClick(long clickMs) {
        boolean doubleClick = clickMs - this.lastClickMs < MAX_INTERVAL_MS;
        this.lastClickMs = clickMs;
        return doubleClick;
    }
}
