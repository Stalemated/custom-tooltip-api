package com.stalemated.customtooltips;

import com.stalemated.customtooltips.config.TooltipConfig;
import com.stalemated.customtooltips.core.TooltipRegistry;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import java.io.File;
import java.nio.file.Path;

import static com.stalemated.customtooltips.CustomTooltipApiClient.LOGGER;

public class ConfigManager {

    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("custom_tooltip_api").resolve("config.json5");

    public static final ConfigClassHandler<TooltipConfig> HANDLER = ConfigClassHandler.createBuilder(TooltipConfig.class)
            .id(new Identifier("customtooltips", "config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(CONFIG_PATH)
                    .setJson5(true)
                    .build())
            .build();

    public static void register() {
        File configFile = CONFIG_PATH.toFile();
        boolean isNewOrEmpty = !configFile.exists() || configFile.length() == 0;

        if (configFile.exists() && configFile.length() == 0) {
            try {
                configFile.delete();
            } catch (Exception e) {
                LOGGER.warn("Failed to delete empty config file: ", e);
            }
        }

        HANDLER.load();

        if (isNewOrEmpty) save();
        else TooltipRegistry.reload();
    }

    public static TooltipConfig getConfig() {
        return HANDLER.instance();
    }

    public static void save() {
        HANDLER.save();
        TooltipRegistry.reload();
    }
}