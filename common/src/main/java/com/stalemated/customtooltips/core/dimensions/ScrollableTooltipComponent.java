package com.stalemated.customtooltips.core.dimensions;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.VertexConsumerProvider;
import org.joml.Matrix4f;

import java.util.List;

public class ScrollableTooltipComponent implements TooltipComponent {
    private final List<TooltipComponent> components;
    private final int maxHeight;
    private final int totalHeight;
    private int maxWidth = 0;
    public static final int SCROLLBAR_WIDTH = 6;
    private final int scrollbarHeight;

    public ScrollableTooltipComponent(List<TooltipComponent> components, List<TooltipComponent> pinned, int maxHeight, TextRenderer textRenderer) {
        this.components = components;
        this.maxHeight = maxHeight;

        int height = components.size() == 1 ? -2 : 0;
        for (int i = 0; i < components.size(); i++) {
            TooltipComponent component = components.get(i);
            int width = component.getWidth(textRenderer);
            if (width > this.maxWidth) this.maxWidth = width;
            height += component.getHeight() + (i == 0 && components.size() > 1 ? 2 : 0);
        }
        
        int pinnedWidth = 0;
        if (pinned != null) {
            for (TooltipComponent p : pinned) {
                int w = p.getWidth(textRenderer);
                if (w > pinnedWidth) pinnedWidth = w;
            }
        }
        
        this.maxWidth = Math.max(this.maxWidth, pinnedWidth);
        
        this.totalHeight = height;
        this.scrollbarHeight = this.maxHeight - 4;
        TooltipScrollManager.updateMaxScroll(this.totalHeight - this.scrollbarHeight);
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        return this.maxWidth + SCROLLBAR_WIDTH;
    }

    @Override
    public int getHeight() {
        return this.maxHeight;
    }

    @Override
    public void drawText(TextRenderer textRenderer, int x, int y, Matrix4f matrix, VertexConsumerProvider.Immediate vertexConsumers) {
        DrawContext context = TooltipDimensionManager.currentContext;
        if (context == null) return;

        int scroll = TooltipScrollManager.getScrollOffset();
        vertexConsumers.draw();
        context.enableScissor(x, y - 2, x + getWidth(textRenderer), y + this.maxHeight);
        int currentY = y - scroll;

        for (int i = 0; i < components.size(); i++) {
            TooltipComponent component = components.get(i);
            component.drawText(textRenderer, x, currentY, matrix, vertexConsumers);
            currentY += component.getHeight() + (i == 0 && components.size() > 1 ? 2 : 0);
        }

        vertexConsumers.draw();
        context.disableScissor();
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
        int scroll = TooltipScrollManager.getScrollOffset();

        context.enableScissor(x, y - 2, x + getWidth(textRenderer), y + this.maxHeight);
        int currentY = y - scroll;

        for (int i = 0; i < components.size(); i++) {
            TooltipComponent component = components.get(i);
            component.drawItems(textRenderer, x, currentY, context);
            currentY += component.getHeight() + (i == 0 && components.size() > 1 ? 2 : 0);
        }

        context.disableScissor();
        
        drawScrollbar(context, x, y);
    }

    private void drawScrollbar(DrawContext context, int x, int y) {
        int maxScroll = this.totalHeight - this.scrollbarHeight;
        if (maxScroll <= 0) return;

        int scroll = TooltipScrollManager.getScrollOffset();
        int scrollbarX = x + this.maxWidth + SCROLLBAR_WIDTH / 2;

        int minThumbHeight = 2;
        float visibleRatio = (float) this.scrollbarHeight / this.totalHeight;

        int thumbHeight = Math.max((int) (this.scrollbarHeight * visibleRatio), minThumbHeight);
        float scrollRatio = (float) scroll / maxScroll;
        int thumbY = y + (int) ((this.scrollbarHeight - thumbHeight) * scrollRatio);

        // Background
        context.fill(scrollbarX, y, scrollbarX + 2, y + this.scrollbarHeight, 0xFF444444);
        // Scroll Bar
        context.fill(scrollbarX, thumbY, scrollbarX + 2, thumbY + thumbHeight, 0xFFBBBBBB);
    }
}