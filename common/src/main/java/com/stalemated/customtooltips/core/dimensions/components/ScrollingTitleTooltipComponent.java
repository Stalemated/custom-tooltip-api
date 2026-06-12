package com.stalemated.customtooltips.core.dimensions.components;

import com.stalemated.customtooltips.compat.LegendaryTooltipsCompat;
import com.stalemated.customtooltips.core.dimensions.TooltipDimensionManager;
import com.stalemated.customtooltips.gui.widget.ScrollMathUtil;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.OrderedTextTooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.OrderedText;
import org.joml.Matrix4f;

public class ScrollingTitleTooltipComponent extends OrderedTextTooltipComponent implements TooltipComponent {
    private final OrderedText text;
    private final int maxTitleWidth;
    private static final double SCROLL_SPEED = 25.0;
    private static final long PAUSE_MS = 2000L;

    public ScrollingTitleTooltipComponent(OrderedText text, int maxTitleWidth) {
        super(text);
        this.text = text;
        this.maxTitleWidth = maxTitleWidth;
    }

    @Override
    public int getHeight() {
        return 10;
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        int textWidth = textRenderer.getWidth(this.text);
        return Math.min(textWidth, this.maxTitleWidth + LegendaryTooltipsCompat.getItemModelComponentWidth(TooltipDimensionManager.getCurrentStack()));
    }

    @Override
    public void drawText(TextRenderer textRenderer, int x, int y, Matrix4f matrix, VertexConsumerProvider.Immediate vertexConsumers) {
        int textWidth = textRenderer.getWidth(this.text);
        if (textWidth <= this.maxTitleWidth) {
            textRenderer.draw(this.text, (float) x, (float) y, -1, true, matrix, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, 15728880);
            return;
        }
        int offset = LegendaryTooltipsCompat.getItemModelComponentWidth(TooltipDimensionManager.getCurrentStack());
        int overflowWidth = textWidth - this.maxTitleWidth - offset;
        int identity = getStringFromOrderedText(this.text).hashCode();
        long elapsedTime = ScrollMathUtil.getTooltipElapsedTime(identity);
        int scrollOffset = ScrollMathUtil.calculateScrollOffset(overflowWidth, SCROLL_SPEED, PAUSE_MS, elapsedTime);

        vertexConsumers.draw();

        Matrix4f translatedMatrix = new Matrix4f(matrix);
        translatedMatrix.translate(0, 0, 400);

        DrawContext context = TooltipDimensionManager.currentContext;

        if (context != null) {
            context.enableScissor(x + offset, y, x + this.maxTitleWidth + offset, y + 10);
        }

        textRenderer.draw(this.text, (float) (x - scrollOffset), (float) y, -1, true, translatedMatrix, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, 15728880);
        vertexConsumers.draw();

        if (context != null) {
            context.disableScissor();
        }
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
    }

    //TODO remove this method maybe
    private String getStringFromOrderedText(OrderedText text) {
        StringBuilder builder = new StringBuilder();
        text.accept((index, style, codePoint) -> {
            builder.appendCodePoint(codePoint);
            return true;
        });
        return builder.toString();
    }
}
