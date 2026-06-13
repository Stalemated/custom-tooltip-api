package com.stalemated.customtooltips.fabric.compat;

import com.anthonyhilyard.legendarytooltips.config.LegendaryTooltipsConfig;
import com.anthonyhilyard.legendarytooltips.tooltip.PaddingComponent;
import com.anthonyhilyard.legendarytooltips.tooltip.TooltipDecor;
import com.stalemated.customtooltips.ConfigManager;
import com.stalemated.customtooltips.core.dimensions.TooltipDimensionManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TierifyLegendaryBridge {
    public static List<TooltipComponent> wrapComponents(List<TooltipComponent> components) {
        if (components == null || components.isEmpty()) return components;

        ItemStack currentStack = TooltipDimensionManager.getCurrentStack();
        if (currentStack == null || currentStack.isEmpty()) return components;

        if (LegendaryTooltipsConfig.showModelForItem(currentStack)) {
            List<TooltipComponent> newList = new ArrayList<>(components);
            LegendaryTieredWrapper wrapper = getLegendaryTieredWrapper(components);

            int titleSize = !TooltipDimensionManager.processedTitleComponentList.isEmpty() 
                            ? TooltipDimensionManager.processedTitleComponentList.size() 
                            : 1;

            for (int i = 0; i < titleSize; i++) {
                if (!newList.isEmpty()) {
                    newList.remove(0);
                }
            }
            newList.add(0, wrapper);
            if (TooltipDimensionManager.processedTitleComponentList.size() > 1) newList.add(1, new PaddingComponent(2));

            return newList;
        }
        return components;
    }

    public static @NotNull LegendaryTieredWrapper getLegendaryTieredWrapper(List<TooltipComponent> components) {
        List<TooltipComponent> titleComponents;

        if (ConfigManager.getConfig().custom_tooltip_dimensions && !TooltipDimensionManager.processedTitleComponentList.isEmpty()) {
            titleComponents = new ArrayList<>(TooltipDimensionManager.processedTitleComponentList);
        } else {
            titleComponents = Collections.singletonList(components.get(0));
        }

        return new LegendaryTieredWrapper(titleComponents);
    }

    private static int lastTooltipX = 0;
    private static int lastTooltipY = 0;
    private static int lastTooltipWidth = 0;

    public static void setTooltipPosition(int x, int y, int width) {
        lastTooltipX = x;
        lastTooltipY = y;
        lastTooltipWidth = width;
    }

    public static void drawSeparator(DrawContext context, List<TooltipComponent> components) {
        if (components.isEmpty()) return;
        if (TooltipDimensionManager.bodyComponentList.isEmpty()) return;
        ItemStack currentStack = TooltipDimensionManager.getCurrentStack();
        if (currentStack == null || currentStack.isEmpty()) return;

        if (LegendaryTooltipsConfig.INSTANCE.nameSeparator.get()) {
            int color = 0xFF996922;
            int offsetY = TooltipDimensionManager.processedTitleComponentList.size() > 1 ? 0 : 2;
            TooltipDecor.drawSeparator(context.getMatrices(), lastTooltipX, lastTooltipY + components.get(0).getHeight() - offsetY, lastTooltipWidth, color);
        }
    }
}
