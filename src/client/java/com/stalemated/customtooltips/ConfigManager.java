package com.stalemated.customtooltips;

import com.stalemated.customtooltips.config.TooltipConfig;
import com.stalemated.customtooltips.core.TooltipRegistry;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static com.stalemated.customtooltips.CustomTooltipApiClient.LOGGER;

public class ConfigManager {

    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("custom_tooltip_api").resolve("config.json5");
    public static boolean configLoadFailed = false;

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

        boolean loaded = HANDLER.load();

        if (!loaded && !isNewOrEmpty) {
            configLoadFailed = true;
            File configBackup = CONFIG_PATH.getParent().resolve("config_backup.json5").toFile();
            try {
                Files.copy(configFile.toPath(), configBackup.toPath(), StandardCopyOption.REPLACE_EXISTING);
                LOGGER.error("A backup of your broken config was saved to: {}", configBackup.getName());
            } catch (Exception e) {
                LOGGER.error("Failed to create backup of the broken config!", e);
            }
        }

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