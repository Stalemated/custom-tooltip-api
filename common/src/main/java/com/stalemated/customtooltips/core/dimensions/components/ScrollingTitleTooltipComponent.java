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
    private static final int offset = LegendaryTooltipsCompat.getItemModelComponentWidth(TooltipDimensionManager.getCurrentStack());
    public static boolean isTierifyTooltip = false;

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
        return Math.min(textWidth, this.maxTitleWidth + (isTierifyTooltip ? 0 : offset));
    }

    @Override
    public void drawText(TextRenderer textRenderer, int x, int y, Matrix4f matrix, VertexConsumerProvider.Immediate vertexConsumers) {
        int textWidth = textRenderer.getWidth(this.text);
        if (textWidth <= this.maxTitleWidth) {
            textRenderer.draw(this.text, (float) x, (float) y, -1, true, matrix, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, 15728880);
            return;
        }
        int overflowWidth = isTierifyTooltip ? textWidth - this.maxTitleWidth : textWidth - this.maxTitleWidth - offset;
        int identity = getStringFromOrderedText(this.text).hashCode();
        long elapsedTime = ScrollMathUtil.getTooltipElapsedTime(identity);
        int scrollOffset = ScrollMathUtil.calculateScrollOffset(overflowWidth, SCROLL_SPEED, PAUSE_MS, elapsedTime);
        int padding = 2;
        int startX = x + (isTierifyTooltip ? 0 : offset);
        int endX = x + this.maxTitleWidth + (isTierifyTooltip ? 0 : offset);

        vertexConsumers.draw();

        Matrix4f translatedMatrix = new Matrix4f(matrix);
        translatedMatrix.translate(0, 0, 400);

        DrawContext context = TooltipDimensionManager.currentContext;

        if (context != null) {
            context.enableScissor(startX, y, endX, y + getHeight());
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
