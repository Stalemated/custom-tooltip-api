package com.stalemated.customtooltips.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import com.stalemated.lib.helper.PlatformHelper;
import static com.stalemated.customtooltips.CustomTooltipApiClient.LOGGER;

public class ResourcepackManager {
    public static final Path RESOURCE_PACK_DIR = PlatformHelper.INSTANCE.getGameDir().resolve("resourcepacks").resolve("Custom Tooltip API - Resources");

    public static void generateResourcePack() {
        try {
            if (!Files.exists(RESOURCE_PACK_DIR)) Files.createDirectories(RESOURCE_PACK_DIR);

            generatePackMcmetaFile();

            CustomFontManager.loadAndGenerateFonts();
            CustomBackgroundManager.loadAndGenerateBackgrounds();

            generateAtlasJson();
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

    private static void generateAtlasJson() throws Exception {
        Path atlasesDir = RESOURCE_PACK_DIR.resolve("assets").resolve("minecraft").resolve("atlases");
        if (!Files.exists(atlasesDir)) Files.createDirectories(atlasesDir);

        File blocksJson = atlasesDir.resolve("blocks.json").toFile();
        if (!blocksJson.exists()) {
            try (FileWriter writer = new FileWriter(blocksJson)) {
                writer.write("""
                    {
                        "sources": [
                            {
                                "type": "directory",
                                "source": "gui/tooltip_backgrounds",
                                "prefix": "gui/tooltip_backgrounds/"
                            }
                        ]
                    }
                    """);
            }
        }
    }
}