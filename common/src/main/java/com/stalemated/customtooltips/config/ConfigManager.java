package com.stalemated.customtooltips.config;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stalemated.customtooltips.core.TooltipRegistry;
import com.stalemated.lib.config.io.json5.SLibGsonDefaults;
import com.stalemated.lib.config.manager.LocalConfigManager;
import com.stalemated.lib.config.manager.builder.LocalConfigBuilder;
import com.stalemated.lib.util.io.PathUtils;

public class ConfigManager {

    public static final Gson GSON;
    static {
        GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
        SLibGsonDefaults.apply(builder);
        builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        GSON = builder.create();
    }

    public static final LocalConfigManager<TooltipConfig> TOOLTIP_CONFIG = new LocalConfigBuilder<>(TooltipConfig.class)
            .modId("custom_tooltip_api")
            .configPath(PathUtils.buildPath("custom_tooltip_api", "config.json5"))
            .gsonCustomizer(b -> b.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES))
            .build();

    public static final LocalConfigManager<ExternalBackgroundsConfig> EXTERNAL_BG_CONFIG = new LocalConfigBuilder<>(ExternalBackgroundsConfig.class)
            .modId("custom_tooltip_api")
            .configPath(PathUtils.buildPath("custom_tooltip_api", "external_backgrounds.json5"))
            .gsonCustomizer(b -> b.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES))
            .build();

    public static void register() {
        TOOLTIP_CONFIG.onConfigLoaded(config -> TooltipRegistry.reload());
        TOOLTIP_CONFIG.onConfigSaved(config -> TooltipRegistry.reload());
        
        TOOLTIP_CONFIG.register();
        EXTERNAL_BG_CONFIG.register();
    }

    public static TooltipConfig getConfig() {
        return TOOLTIP_CONFIG.getConfig();
    }

    public static void save() {
        TOOLTIP_CONFIG.save();
    }
}
