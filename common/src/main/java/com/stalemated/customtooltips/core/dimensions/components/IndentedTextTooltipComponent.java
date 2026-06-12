package com.stalemated.customtooltips.core.dimensions.components;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.OrderedTextTooltipComponent;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.OrderedText;
import org.joml.Matrix4f;

/**
 * A tooltip component that acts exactly like a vanilla OrderedTextTooltipComponent
 * but applies a custom X-offset when rendering.
 * Used to ensure the title doesn't overlap with Left-aligned decor like Legendary Tooltips 3D models
 * when the WRAP strategy creates multiple text components.
 */
public class IndentedTextTooltipComponent extends OrderedTextTooltipComponent {

    private final int xOffset;
    public IndentedTextTooltipComponent(OrderedText text, int xOffset) {
        super(text);
        this.xOffset = xOffset;
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        return super.getWidth(textRenderer) + this.xOffset;
    }

    @Override
    public void drawText(TextRenderer textRenderer, int x, int y, Matrix4f matrix, VertexConsumerProvider.Immediate vertexConsumers) {
        super.drawText(textRenderer, x + this.xOffset, y, matrix, vertexConsumers);
    }
}
