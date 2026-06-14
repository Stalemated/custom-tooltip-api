package com.stalemated.customtooltips.core.dimensions.components;

import com.stalemated.customtooltips.compat.LegendaryTooltipsCompat;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.VertexConsumerProvider;
import org.joml.Matrix4f;

import java.util.List;

public class WrappedTitleTooltipComponent implements TooltipComponent {

    private final List<TooltipComponent> wrappedLines;

    public WrappedTitleTooltipComponent(List<TooltipComponent> wrappedLines) {
        this.wrappedLines = wrappedLines;
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        int maxWidth = 0;
        for (TooltipComponent line : this.wrappedLines) {
            maxWidth = Math.max(maxWidth, line.getWidth(textRenderer));
        }
        return maxWidth;
    }

    @Override
    public int getHeight() {
        int totalHeight = 0;
        for (int i = 0; i < this.wrappedLines.size(); i++) {
            totalHeight += this.wrappedLines.get(i).getHeight() + LegendaryTooltipsCompat.getLTOffset(i, this.wrappedLines.size());
        }
        return totalHeight;
    }

    @Override
    public void drawText(TextRenderer textRenderer, int x, int y, Matrix4f matrix, VertexConsumerProvider.Immediate vertexConsumers) {
        int currentY = y;
        for (int i = 0; i < this.wrappedLines.size(); i++) {
            TooltipComponent line = this.wrappedLines.get(i);

            line.drawText(textRenderer, x, currentY, matrix, vertexConsumers);
            currentY += line.getHeight() + LegendaryTooltipsCompat.getLTOffset(i, this.wrappedLines.size());
        }
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, net.minecraft.client.gui.DrawContext context) {
        int currentY = y;
        for (int i = 0; i < this.wrappedLines.size(); i++) {
            TooltipComponent line = this.wrappedLines.get(i);

            line.drawItems(textRenderer, x, currentY, context);
            currentY += line.getHeight() + LegendaryTooltipsCompat.getLTOffset(i, this.wrappedLines.size());
        }
    }
}
