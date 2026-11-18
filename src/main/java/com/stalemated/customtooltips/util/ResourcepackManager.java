package com.stalemated.customtooltips.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import com.stalemated.lib.helper.PlatformHelper;

import static com.stalemated.customtooltips.CustomTooltips.LOGGER;

public class ResourcepackManager {
    public static final Path RESOURCE_PACK_DIR = PlatformHelper.getGameDir().resolve("resourcepacks").resolve("Custom Tooltip API - Resources");

    public static void generateResourcePack() {
        try {
            if (!Files.exists(RESOURCE_PACK_DIR)) Files.createDirectories(RESOURCE_PACK_DIR);

            generatePackMcmetaFile();

            CustomFontManager.loadAndGenerateFonts();
            CustomBackgroundManager.loadAndGenerateBackgrounds();

            removeBlockAtlasJson();
        } catch (Exception e) {
            LOGGER.error("Failed to generate resource pack", e);
        }
    }

    private static void generatePackMcmetaFile() throws IOException {
        File mcmeta = RESOURCE_PACK_DIR.resolve("pack.mcmeta").toFile();
        if (!mcmeta.exists()) {
            try (FileWriter writer = new FileWriter(mcmeta)) {
                writer.write("""
                    {
                        "pack": {
                           "pack_format": 15,
                           "description": "Custom Tooltip API - Auto Generated Assets"
                        }
                    }
                    """);
            }
        }
    }

    private static void removeBlockAtlasJson() throws IOException {
        Path blocksJson = RESOURCE_PACK_DIR.resolve("assets").resolve("minecraft").resolve("atlases").resolve("blocks.json");
        if (Files.deleteIfExists(blocksJson)) LOGGER.info("Removed {}, tooltip backgrounds have their own atlas now", blocksJson);
    }
}