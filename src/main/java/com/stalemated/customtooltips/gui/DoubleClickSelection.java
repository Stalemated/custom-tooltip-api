package com.stalemated.customtooltips.gui;

import com.stalemated.customtooltips.config.CustomTooltipsConfigRegistry;

public final class DoubleClickSelection {
    private static final long MAX_INTERVAL_MS = 250;

    private long lastClickMs;

    public boolean selectsAll() {
        boolean doubleClick = isDoubleClick(System.currentTimeMillis());
        return doubleClick && CustomTooltipsConfigRegistry.CONFIG.get().enable_double_click_selection;
    }

    boolean isDoubleClick(long clickMs) {
        boolean doubleClick = clickMs - this.lastClickMs < MAX_INTERVAL_MS;
        this.lastClickMs = clickMs;
        return doubleClick;
    }
}
