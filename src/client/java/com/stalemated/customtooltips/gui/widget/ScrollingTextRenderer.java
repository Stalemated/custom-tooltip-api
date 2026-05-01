package com.stalemated.customtooltips.gui.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public class ScrollingTextRenderer {

    private static final double SCROLL_SPEED_PIXELS_PER_SECOND = 25.0;
    private static final long SCROLL_PAUSE_MS = 1500L;
    private final long startTime;

    public ScrollingTextRenderer() {
        this.startTime = System.currentTimeMillis();
    }

    public void render(DrawContext context, String text, int x, int y, int availableWidth, boolean isDisabled) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int textWidth = textRenderer.getWidth(text);

        if (textWidth > availableWidth) {
            int overflowWidth = textWidth - availableWidth;
            double speed = SCROLL_SPEED_PIXELS_PER_SECOND / 1000.0;
            long travelTime = Math.max(1, (long) (overflowWidth / speed));
            long halfCycle = travelTime + SCROLL_PAUSE_MS;
            long totalCycle = 2 * halfCycle;
            long cycleTime = (System.currentTimeMillis() - this.startTime) % totalCycle;

            double progress = getProgress(cycleTime, halfCycle, travelTime);
            int scrollOffset = (int) (progress * overflowWidth);

            context.drawTextWithShadow(textRenderer, text, x - scrollOffset, y, isDisabled ? 0xAAAAAA : 0xFFFFFF);
        } else {
            context.drawTextWithShadow(textRenderer, text, x, y, isDisabled ? 0xAAAAAA : 0xFFFFFF);
        }
    }

    private double getProgress(long cycleTime, long halfCycle, long travelTime) {
        if (cycleTime < SCROLL_PAUSE_MS) return 0.0; // Start pause
        if (cycleTime < halfCycle) return (double) (cycleTime - SCROLL_PAUSE_MS) / travelTime; // Moving right
        if (cycleTime < halfCycle + SCROLL_PAUSE_MS) return 1.0; // End pause
        return 1.0 - ((double) (cycleTime - (halfCycle + SCROLL_PAUSE_MS)) / travelTime); // Moving left
    }
}