package com.stalemated.customtooltips.config;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.stalemated.customtooltips.core.TooltipRegistry;
import com.stalemated.lib.config.LocalConfigManager;
import com.stalemated.lib.config.SLibConfig;
import com.stalemated.lib.util.io.PathUtils;

public final class CustomTooltipsConfigRegistry {
    public static final String CONFIG_FOLDER = "custom_tooltip_api";

    private static boolean loadFailed;

    public static final LocalConfigManager<TooltipConfig> CONFIG = SLibConfig.local(CONFIG_FOLDER, TooltipConfig.class)
            .configPath(PathUtils.buildPath(CONFIG_FOLDER, "config.json5"))
            .gsonCustomizer(CustomTooltipsConfigRegistry::useSnakeCaseKeys)
            .onLoad(TooltipRegistry::reload)
            .onChange(TooltipRegistry::reload)
            .onLoadFailed(failure -> loadFailed = true)
            .build();

    public static final LocalConfigManager<ExternalBackgroundsConfig> EXTERNAL_BACKGROUNDS = SLibConfig.local(CONFIG_FOLDER, ExternalBackgroundsConfig.class)
            .configPath(PathUtils.buildPath(CONFIG_FOLDER, "external_backgrounds.json5"))
            .gsonCustomizer(CustomTooltipsConfigRegistry::useSnakeCaseKeys)
            .build();

    private CustomTooltipsConfigRegistry() {
    }

    public static void register() {
        CONFIG.register();
        EXTERNAL_BACKGROUNDS.register();
    }

    public static boolean consumeLoadFailure() {
        boolean failed = loadFailed;
        loadFailed = false;
        return failed;
    }

    private static void useSnakeCaseKeys(GsonBuilder gson) {
        gson.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
    }
}
