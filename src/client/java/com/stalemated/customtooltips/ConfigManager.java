package com.stalemated.customtooltips;

import com.stalemated.customtooltips.config.TooltipConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;

public class ConfigManager {

    public static void register() {
        AutoConfig.register(TooltipConfig.class, JanksonConfigSerializer::new);
    }

    public static TooltipConfig getConfig() {
        return AutoConfig.getConfigHolder(TooltipConfig.class).getConfig();
    }
}