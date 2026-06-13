package com.stalemated.customtooltips.fabric.compat;

import com.anthonyhilyard.legendarytooltips.tooltip.ItemModelComponent;
import com.stalemated.customtooltips.compat.LegendaryTooltipsCompat;
import com.stalemated.customtooltips.core.dimensions.TooltipDimensionManager;
import com.stalemated.customtooltips.core.dimensions.components.IndentedTextTooltipComponent;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.item.ItemStack;
import org.joml.Matrix4f;

import java.util.List;

public class LegendaryTieredWrapper implements TooltipComponent {

    private final ItemModelComponent modelComponent;
    private final List<TooltipComponent> titleComponents;
    private final int extraWidth;

    public LegendaryTieredWrapper(List<TooltipComponent> titleComponents) {
        this.titleComponents = titleComponents;
        ItemStack currentStack = TooltipDimensionManager.getCurrentStack();
        this.modelComponent = new ItemModelComponent(currentStack);
        this.extraWidth = LegendaryTooltipsCompat.getItemModelComponentWidth(currentStack);
    }

    private int getTitleHeight() {
        int totalTitleHeight = 0;
        if (titleComponents != null) {
            for (TooltipComponent component : titleComponents) {
                totalTitleHeight += component.getHeight();
            }
        }
        return totalTitleHeight;
    }

    private int getFirstLineHeight() {
        if (titleComponents != null && !titleComponents.isEmpty()) {
            return titleComponents.get(0).getHeight();
        }
        return 10;
    }

    @Override
    public int getHeight() {
        int yOffset = Math.max(0, (this.extraWidth - getFirstLineHeight()) / 2);
        return Math.max(this.extraWidth, yOffset * 2 + getTitleHeight() - 2);
    }

    private int getCalculatedX(TooltipComponent component, int x, int i) {
        int drawX = x;

        if (i == 0 || !(component instanceof IndentedTextTooltipComponent)) {
            drawX += this.extraWidth;
        }
        return drawX;
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        int maxTitleWidth = 0;
        if (titleComponents != null) {
            for (int i = 0; i < titleComponents.size(); i++) {
                TooltipComponent component = titleComponents.get(i);
                int compWidth = component.getWidth(textRenderer);
                
                if (i == 0 || !(component instanceof IndentedTextTooltipComponent)) {
                    compWidth += this.extraWidth;
                }
                maxTitleWidth = Math.max(maxTitleWidth, compWidth);
            }
        }
        return maxTitleWidth;
    }

    @Override
    public void drawText(TextRenderer textRenderer, int x, int y, Matrix4f matrix, VertexConsumerProvider.Immediate vertexConsumers) {
        int yOffset = Math.max(0, (this.extraWidth - getFirstLineHeight()) / 2);
        int currentY = y + yOffset - 1;

        if (titleComponents != null) {
            for (int i = 0; i < titleComponents.size(); i++) {
                TooltipComponent component = titleComponents.get(i);
                int drawX = getCalculatedX(component, x, i);

                component.drawText(textRenderer, drawX, currentY, matrix, vertexConsumers);
                currentY += component.getHeight();
            }
        }
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
        if (this.extraWidth > 0) {
            modelComponent.drawItems(textRenderer, x, y, context);
        }

        int yOffset = Math.max(0, (this.extraWidth - getFirstLineHeight()) / 2);
        int currentY = y + yOffset;

        if (titleComponents != null) {
            for (int i = 0; i < titleComponents.size(); i++) {
                TooltipComponent component = titleComponents.get(i);
                int drawX = getCalculatedX(component, x, i);

                component.drawItems(textRenderer, drawX, currentY, context);
                currentY += component.getHeight();
            }
        }
    }
}
