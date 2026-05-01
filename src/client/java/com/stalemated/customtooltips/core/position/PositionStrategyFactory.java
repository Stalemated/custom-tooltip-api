package com.stalemated.customtooltips.core.position;

import com.stalemated.customtooltips.TooltipEntry.TooltipPosition;
import com.stalemated.customtooltips.core.position.strategies.*;
import java.util.EnumMap;
import java.util.Map;

public class PositionStrategyFactory {
    private static final Map<TooltipPosition, TooltipPositionStrategy> STRATEGIES = new EnumMap<>(TooltipPosition.class);

    static {
        STRATEGIES.put(TooltipPosition.REPLACE_NAME, new ReplaceNameStrategy());
        STRATEGIES.put(TooltipPosition.REPLACE_ALL, new ReplaceAllStrategy());
        STRATEGIES.put(TooltipPosition.APPEND, new AppendPrependStrategy(true));
        STRATEGIES.put(TooltipPosition.PREPEND, new AppendPrependStrategy(false));
        STRATEGIES.put(TooltipPosition.TOP, new InsertStrategy(true));
        STRATEGIES.put(TooltipPosition.BOTTOM, new InsertStrategy(false));
    }

    public static TooltipPositionStrategy get(TooltipPosition position) {
        return STRATEGIES.getOrDefault(position, STRATEGIES.get(TooltipPosition.BOTTOM));
    }
}