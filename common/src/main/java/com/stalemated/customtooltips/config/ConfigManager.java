package com.stalemated.customtooltips.config;

import com.stalemated.customtooltips.core.TooltipRegistry;
import com.stalemated.lib.config.BaseConfigManager;
import com.stalemated.lib.config.ConfigProvider;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.minecraft.util.Identifier;

import java.nio.file.Path;

import static com.stalemated.customtooltips.CustomTooltipApiClient.LOGGER;

public class ConfigManager {

    private static final Path CONFIG_PATH = BaseConfigManager.buildPath("custom_tooltip_api", "config.json5");
    private static final Path EXTERNAL_CONFIG_PATH = BaseConfigManager.buildPath("custom_tooltip_api", "external_backgrounds.json5");
    public static boolean configLoadFailed = false;

    public static final ConfigClassHandler<TooltipConfig> HANDLER = ConfigClassHandler.createBuilder(TooltipConfig.class)
            .id(Identifier.of("customtooltips", "config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(CONFIG_PATH)
                    .setJson5(true)
                    .build())
            .build();

    public static final ConfigClassHandler<ExternalBackgroundsConfig> EXTERNAL_BG_HANDLER = ConfigClassHandler.createBuilder(ExternalBackgroundsConfig.class)
            .id(Identifier.of("customtooltips", "external_backgrounds"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(EXTERNAL_CONFIG_PATH)
                    .setJson5(true)
                    .build())
            .build();

    private static final BaseConfigManager<TooltipConfig> CONFIG = new BaseConfigManager<TooltipConfig>(
            new ConfigProvider<TooltipConfig>() {
                @Override
                public boolean load() {
                    return HANDLER.load();
                }

                @Override
                public void save() {
                    HANDLER.save();
                }

                @Override
                public TooltipConfig instance() {
                    return HANDLER.instance();
                }
            },
            CONFIG_PATH,
            LOGGER
    ) {
        @Override
        protected void onRegisterSuccess(boolean isNewOrEmpty) {
            if (!isNewOrEmpty) {
                TooltipRegistry.reload();
            }
            if (!EXTERNAL_CONFIG_PATH.toFile().exists()) {
                EXTERNAL_BG_HANDLER.save();
            } else {
                EXTERNAL_BG_HANDLER.load();
            }
        }

        @Override
        protected void onSaveSuccess() {
            TooltipRegistry.reload();
        }
    };

    public static void register() {
        CONFIG.register();
        configLoadFailed = CONFIG.configLoadFailed;
    }

    public static TooltipConfig getConfig() {
        return CONFIG.getConfig();
    }

    public static void save() {
        CONFIG.save();
    }
}
