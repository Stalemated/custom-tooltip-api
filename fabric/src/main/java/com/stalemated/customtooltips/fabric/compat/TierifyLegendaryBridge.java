package com.stalemated.customtooltips.fabric.compat;

import com.anthonyhilyard.legendarytooltips.config.LegendaryTooltipsConfig;
import com.anthonyhilyard.legendarytooltips.tooltip.PaddingComponent;
import com.anthonyhilyard.legendarytooltips.tooltip.TooltipDecor;
import com.stalemated.customtooltips.ConfigManager;
import com.stalemated.customtooltips.compat.LegendaryTooltipsCompat;
import com.stalemated.customtooltips.core.dimensions.TooltipDimensionManager;
import com.stalemated.customtooltips.core.dimensions.components.WrappedTitleTooltipComponent;
import com.stalemated.customtooltips.fabric.compat.component.LegendaryTieredWrapper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.stalemated.customtooltips.core.dimensions.TooltipDimensionManager.TITLE_BODY_VERTICAL_GAP;

public class TierifyLegendaryBridge {
    public static List<TooltipComponent> wrapComponents(List<TooltipComponent> components) {
        if (components == null || components.isEmpty()) return components;

        ItemStack currentStack = TooltipDimensionManager.getCurrentStack();
        if (currentStack == null || currentStack.isEmpty()) return components;

        int titleSize = !TooltipDimensionManager.processedTitleComponentList.isEmpty()
                ? TooltipDimensionManager.processedTitleComponentList.size()
                : 1;

        List<TooltipComponent> newList = new ArrayList<>(components);

        for (int i = 0; i < titleSize; i++) {
            if (!newList.isEmpty()) {
                newList.remove(0);
            }
        }

        if (LegendaryTooltipsConfig.showModelForItem(currentStack)) {

            LegendaryTieredWrapper wrapper = getLegendaryTieredWrapper(components);

            newList.add(0, wrapper);
            if (TooltipDimensionManager.processedTitleComponentList.size() > 1) newList.add(1, new PaddingComponent(2));

            return newList;
        }
        List<TooltipComponent> titleComponentList = new ArrayList<>(components.subList(0, titleSize));
        newList.add(0, new WrappedTitleTooltipComponent(titleComponentList));

        if (TooltipDimensionManager.processedTitleComponentList.size() > 1) newList.add(1, new PaddingComponent(2));
        return newList;
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
        if (ConfigManager.getConfig().custom_tooltip_dimensions && TooltipDimensionManager.bodyComponentList.isEmpty()) return;
        ItemStack currentStack = TooltipDimensionManager.getCurrentStack();
        if (currentStack == null || currentStack.isEmpty()) return;

        if (LegendaryTooltipsConfig.INSTANCE.nameSeparator.get()) {
            int color = 0xFF996922;
            int offsetY = TooltipDimensionManager.processedTitleComponentList.size() > 1 ? 0 : TITLE_BODY_VERTICAL_GAP;
            if (!LegendaryTooltipsConfig.showModelForItem(currentStack)) {
                offsetY = TooltipDimensionManager.processedTitleComponentList.size() > 1 ? 0 : TITLE_BODY_VERTICAL_GAP / 2;
            }
            TooltipDecor.drawSeparator(context.getMatrices(), lastTooltipX, lastTooltipY + components.get(0).getHeight() - offsetY, lastTooltipWidth, color);
        }
    }

    public static int getPredictedPinnedHeight(List<TooltipComponent> pinned) {
        int extraWidth = LegendaryTooltipsCompat.getItemModelComponentWidth(TooltipDimensionManager.getCurrentStack());
        
        if (extraWidth > 0 && !pinned.isEmpty()) {
            int titleHeight = 0;
            for (TooltipComponent component : pinned) {
                titleHeight += component.getHeight();
            }
            
            int firstLineHeight = pinned.get(0).getHeight();
            int yOffset = Math.max(0, (extraWidth - firstLineHeight) / 2);
            int wrapperHeight = Math.max(extraWidth, yOffset * 2 + titleHeight - TITLE_BODY_VERTICAL_GAP);
            int paddingHeight = LegendaryTooltipsCompat.getLTOffset(0, pinned.size());
            
            return wrapperHeight + paddingHeight;
        }

        int rawPinnedHeight = 0;
        for (int i = 0; i < pinned.size(); i++) {
            rawPinnedHeight += pinned.get(i).getHeight() + LegendaryTooltipsCompat.getLTOffset(i, pinned.size());
        }
        return rawPinnedHeight;
    }
}
