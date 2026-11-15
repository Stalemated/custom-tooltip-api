package com.stalemated.customtooltips.compat;

import java.util.ArrayList;
import java.util.List;

public class CompatManager {
    private static final List<ICompatColorProvider> colorProviders = new ArrayList<>();

    public static void registerColorProvider(ICompatColorProvider provider) {
        colorProviders.add(provider);
    }

    public static int getBorderColorOverride(int originalColor) {
        int color = originalColor;
        for (ICompatColorProvider provider : colorProviders) {
            color = provider.overrideBorderColor(color);
        }
        return color;
    }
}
