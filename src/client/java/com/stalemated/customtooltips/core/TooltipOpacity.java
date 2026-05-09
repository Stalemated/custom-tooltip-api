package com.stalemated.customtooltips.core;

import com.stalemated.customtooltips.ConfigManager;
import com.stalemated.customtooltips.TooltipEntry;
import net.minecraft.item.ItemStack;

public class TooltipOpacity {
    private static int currentOpacity = -1;

    public static void setMixinTooltipOpacity(ItemStack stack) {
        int targetOpacity = -1;

        for (TooltipEntry entry : ConfigManager.getConfig().entries) {
            if (ConfigManager.getConfig().disabled_entries.contains(entry.getIdentifier())) continue;

            if (entry.matches(stack) && entry.areItemConditionsMet(stack)) {
                if (entry.require_keybind && !TooltipProcessor.isHoldKeyPressed()) continue;
                targetOpacity = entry.opacity;
                break;
            }
        }

        currentOpacity = targetOpacity;
    }

    public static void setCurrentOpacity(int opacity) {
        currentOpacity = opacity;
    }

    public static int getCurrentOpacity() {
        return currentOpacity;
    }
}