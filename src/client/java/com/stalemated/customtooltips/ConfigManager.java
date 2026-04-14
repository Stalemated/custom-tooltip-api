package com.stalemated.customtooltips;

import com.stalemated.customtooltips.config.TooltipConfig;
import com.stalemated.customtooltips.core.TooltipRegistry;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.ActionResult;

import java.io.File;

import static com.stalemated.customtooltips.CustomTooltipApiClient.LOGGER;

public class ConfigManager {

    public static void register() {

        //boolean deleted = false;

        File configFile = FabricLoader.getInstance().getConfigDir().resolve("custom_tooltips.json5").toFile();
        if (configFile.exists() && configFile.length() == 0) {
            try {
                /*deleted = */configFile.delete();
            } catch (Exception e) {
                LOGGER.warn("Failed to delete empty config file: ", e);
            }
        }

        /*boolean isNewFile = !configFile.exists();*/

        AutoConfig.register(TooltipConfig.class, JanksonConfigSerializer::new);

        /*if (isNewFile && !deleted) {
            TooltipConfig config = getConfig();
            config.addDefaultEntries();
            AutoConfig.getConfigHolder(TooltipConfig.class).save();
        }*/

        AutoConfig.getConfigHolder(TooltipConfig.class).registerSaveListener((holder, config) -> {
            TooltipRegistry.reload();
            return ActionResult.PASS;
        });
    }

    public static TooltipConfig getConfig() {
        return AutoConfig.getConfigHolder(TooltipConfig.class).getConfig();
    }
}