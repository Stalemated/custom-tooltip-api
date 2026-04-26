package com.stalemated.customtooltips;

import com.stalemated.customtooltips.config.TooltipConfig;
import com.stalemated.customtooltips.core.TooltipRegistry;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

public class ConfigManager {

    public static final ConfigClassHandler<TooltipConfig> HANDLER = ConfigClassHandler.createBuilder(TooltipConfig.class)
            .id(new Identifier("customtooltips", "config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("custom_tooltip_api").resolve("config.json5"))
                    .setJson5(true)
                    .build())
            .build();

    public static void register() {
        HANDLER.load();
        TooltipRegistry.reload();
    }

    public static TooltipConfig getConfig() {
        return HANDLER.instance();
    }

    public static void save() {
        HANDLER.save();
        TooltipRegistry.reload();
    }
}