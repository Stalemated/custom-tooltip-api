package com.stalemated.customtooltips.util;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import com.stalemated.lib.helper.PlatformHelper;
import static com.stalemated.customtooltips.CustomTooltipApiClient.LOGGER;

public class ResourcepackManager {
    public static final Path RESOURCE_PACK_DIR = PlatformHelper.INSTANCE.getGameDir().resolve("resourcepacks").resolve("custom_tooltip_api_resources");

    public static void generateResourcePack() {
        try {
            if (!Files.exists(RESOURCE_PACK_DIR)) Files.createDirectories(RESOURCE_PACK_DIR);

            File mcmeta = RESOURCE_PACK_DIR.resolve("pack.mcmeta").toFile();
            try (FileWriter writer = new FileWriter(mcmeta)) {
                writer.write(getPackMcmetaString());
            }

            CustomFontManager.loadAndGenerateFonts();
            CustomBackgroundManager.loadAndGenerateBackgrounds();
        } catch (Exception e) {
            LOGGER.error("Failed to generate resource pack", e);
        }
    }

    private static String getPackMcmetaString() {
        return """
                {
                   "pack": {
                       "pack_format": 34,
                       "description": "Custom Tooltip API - Auto Generated Assets",
                       "supported_formats": [32, 45]
                   }
                }
                """;
    }
}