package com.stalemated.customtooltips.core.dimensions.overflow;

import com.stalemated.customtooltips.ConfigManager;
import com.stalemated.customtooltips.core.dimensions.TitleOverflowMode;
import com.stalemated.customtooltips.core.dimensions.overflow.strategies.TruncateOverflowStrategy;
import com.stalemated.customtooltips.core.dimensions.overflow.strategies.WrapOverflowStrategy;

public class TitleOverflowStrategyFactory {

    private static final TitleOverflowStrategy TRUNCATE_STRATEGY = new TruncateOverflowStrategy();
    private static final TitleOverflowStrategy WRAP_STRATEGY = new WrapOverflowStrategy();

    public static TitleOverflowStrategy getStrategy() {
        TitleOverflowMode mode = ConfigManager.getConfig().title_overflow_mode;
        
        if (mode == null) {
            return TRUNCATE_STRATEGY; // Fallback
        }

        return switch (mode) {
            case WRAP -> WRAP_STRATEGY;
            default -> TRUNCATE_STRATEGY;
        };
    }
}
