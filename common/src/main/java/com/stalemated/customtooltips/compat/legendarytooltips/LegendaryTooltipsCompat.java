package com.stalemated.customtooltips.compat.legendarytooltips;

import com.stalemated.customtooltips.compat.ICompatColorProvider;
import com.stalemated.customtooltips.core.TooltipBackgroundManager;

public class LegendaryTooltipsCompat implements ICompatColorProvider {
    @Override
    public int overrideBorderColor(int originalColor) {
        return TooltipBackgroundManager.getBorderColorStart(originalColor);
    }
}
