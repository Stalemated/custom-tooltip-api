package com.stalemated.customtooltips.core.dimensions.components;

import com.stalemated.customtooltips.compat.LegendaryTooltipsCompat;
import com.stalemated.customtooltips.core.dimensions.TooltipDimensionManager;
import com.stalemated.customtooltips.core.dimensions.TooltipScrollManager;
import com.stalemated.customtooltips.util.MathUtils;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.VertexConsumerProvider;
import org.joml.Matrix4f;

import java.util.List;

import static com.stalemated.customtooltips.core.dimensions.TooltipDimensionManager.TITLE_BODY_VERTICAL_GAP;

public class ScrollableTooltipComponent implements TooltipComponent {
    private final List<TooltipComponent> components;
    private final int maxHeight;
    private final int totalHeight;
    private final int maxWidth;
    private final int maxTextWidth;
    public static final int SCROLLBAR_WIDTH = 6;
    private final int scrollbarHeight;

    public ScrollableTooltipComponent(List<TooltipComponent> components, List<TooltipComponent> pinned, int maxHeight, int maxWidth, TextRenderer textRenderer) {
        this.components = components;
        this.maxHeight = maxHeight;
        this.maxWidth = maxWidth;

        int height = 0;
        int maxComponentWidth = 0;
        for (TooltipComponent component : components) {
            int width = component.getWidth(textRenderer);
            if (width > maxComponentWidth) maxComponentWidth = width;
            height += component.getHeight();
        }
        
        int maxPinnedWidth = 0;
        if (pinned != null) {
            for (TooltipComponent pin : pinned) {
                int pinWidth = pin.getWidth(textRenderer);
                if (pinWidth > maxPinnedWidth) maxPinnedWidth = pinWidth;
            }
        }
        int offset = LegendaryTooltipsCompat.getItemModelComponentWidth(TooltipDimensionManager.getCurrentStack());

        this.maxTextWidth = MathUtils.clamp(Math.max(maxComponentWidth, maxPinnedWidth + offset), TooltipDimensionManager.MIN_TOOLTIP_WIDTH, maxWidth - SCROLLBAR_WIDTH);

        this.totalHeight = height;
        this.scrollbarHeight = this.maxHeight - 4;
        TooltipScrollManager.updateMaxScroll(this.totalHeight - this.scrollbarHeight);
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        return this.maxWidth;
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
        context.enableScissor(x, y - TITLE_BODY_VERTICAL_GAP, x + this.maxTextWidth, y + this.maxHeight);
        int currentY = y - scroll;

        for (TooltipComponent component : components) {
            component.drawText(textRenderer, x, currentY, matrix, vertexConsumers);
            currentY += component.getHeight();
        }

        vertexConsumers.draw();
        context.disableScissor();
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
        int scroll = TooltipScrollManager.getScrollOffset();

        context.enableScissor(x, y - TITLE_BODY_VERTICAL_GAP, x + this.maxTextWidth, y + this.maxHeight);
        int currentY = y - scroll;

        for (TooltipComponent component : components) {
            component.drawItems(textRenderer, x, currentY, context);
            currentY += component.getHeight();
        }

        context.disableScissor();
        
        drawScrollbar(context, x, y);
    }

    private void drawScrollbar(DrawContext context, int x, int y) {
        int maxScroll = this.totalHeight - this.scrollbarHeight;
        if (maxScroll <= 0) return;

        int scroll = TooltipScrollManager.getScrollOffset();
        int scrollbarX = x + this.maxWidth - SCROLLBAR_WIDTH / 2;

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