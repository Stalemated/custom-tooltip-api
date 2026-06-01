package com.stalemated.customtooltips.core.dimensions;

public class TooltipScrollManager {
    private static int scrollOffset = 0;
    private static int maxScroll = 0;
    private static long lastRenderTime = 0;
    private static final int maxUnhoveredRenderTimeMs = 50;

    public static void updateMaxScroll(int newMaxScroll) {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastRenderTime > maxUnhoveredRenderTimeMs) {
            scrollOffset = 0;
        }
        lastRenderTime = currentTime;

        maxScroll = Math.max(0, newMaxScroll);
        scrollOffset = Math.clamp(scrollOffset, 0, maxScroll);
    }

    public static boolean scroll(double amount) {
        if (maxScroll <= 0) return false;

        int pixelsPerScroll = 15;
        if (System.currentTimeMillis() - lastRenderTime < maxUnhoveredRenderTimeMs) {
            scrollOffset -= (int) (amount * pixelsPerScroll);
            scrollOffset = Math.clamp(scrollOffset, 0, maxScroll);
            return true;
        }
        return false;
    }

    public static int getScrollOffset() { return scrollOffset; }
}