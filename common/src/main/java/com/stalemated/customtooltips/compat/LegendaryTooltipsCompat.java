package com.stalemated.customtooltips.compat;

import com.anthonyhilyard.legendarytooltips.config.LegendaryTooltipsConfig;
import com.stalemated.customtooltips.util.PlatformHelper;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LegendaryTooltipsCompat {
    private static final boolean HAS_LEGENDARY_TOOLTIPS = PlatformHelper.INSTANCE.isModLoaded("legendarytooltips");
    private static final boolean HAS_ICEBERG = PlatformHelper.INSTANCE.isModLoaded("iceberg");
    private static final Map<Class<?>, String> COMPONENT_NAME_CACHE = new ConcurrentHashMap<>();

    private static String getCachedClassName(Class<?> clazz) {
        return COMPONENT_NAME_CACHE.computeIfAbsent(clazz, Class::getSimpleName);
    }

    public static int getItemModelComponentWidth(ItemStack stack) {
        if (!HAS_LEGENDARY_TOOLTIPS) return 0;
        return LegendaryTooltipsConfig.showModelForItem(stack) ? 24 : 0;
    }

    public static int getSplitIndex(List<TooltipComponent> components) {
        int splitIndex = 1;

        if (HAS_LEGENDARY_TOOLTIPS || HAS_ICEBERG) {
            for (int i = 0; i < components.size(); i++) {
                TooltipComponent comp = components.get(i);
                String simpleName = getCachedClassName(comp.getClass());

                if ((HAS_LEGENDARY_TOOLTIPS && "PaddingComponent".equals(simpleName)) ||
                        (HAS_ICEBERG && "TitleBreakComponent".equals(simpleName))) {
                    splitIndex = i + 1;
                    break;
                }
            }
            return Math.min(splitIndex, components.size());
        }
        return splitIndex;
    }
}
