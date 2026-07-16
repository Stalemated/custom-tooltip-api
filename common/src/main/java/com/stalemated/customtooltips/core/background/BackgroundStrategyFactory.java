package com.stalemated.customtooltips.core.background;

import com.stalemated.customtooltips.api.enums.BackgroundType;
import com.stalemated.customtooltips.core.background.strategies.*;

import java.util.EnumMap;
import java.util.Map;

public class BackgroundStrategyFactory {
    private static final Map<BackgroundType, BackgroundRenderStrategy> STRATEGIES = new EnumMap<>(BackgroundType.class);

    static {
        STRATEGIES.put(BackgroundType.SOLID, new SolidBackgroundStrategy());
        STRATEGIES.put(BackgroundType.GRADIENT, new GradientBackgroundStrategy());
        STRATEGIES.put(BackgroundType.TEXTURE, new NineSliceTextureBackgroundStrategy());
        STRATEGIES.put(BackgroundType.SIMPLE_TEXTURE, new TextureBackgroundStrategy());
        STRATEGIES.put(BackgroundType.REPEATING_TEXTURE, new RepeatingTextureBackgroundStrategy());
    }

    public static BackgroundRenderStrategy getStrategy(BackgroundType type) {
        return STRATEGIES.getOrDefault(type, STRATEGIES.get(BackgroundType.SOLID));
    }
}