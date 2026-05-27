package com.stalemated.customtooltips.util;

import dev.architectury.platform.Platform;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import static com.stalemated.customtooltips.CustomTooltipApiClient.LOGGER;

public class CustomFontManager {

    private static final Path FONTS_INPUT_DIR = Platform.getConfigFolder().resolve("custom_tooltip_api").resolve("fonts");

    public static List<String> availableFonts = new ArrayList<>();

    public static void loadAndGenerateFonts() {
        availableFonts.clear();
        availableFonts.add("minecraft:default");

        try {
            if (!Files.exists(FONTS_INPUT_DIR)) {
                Files.createDirectories(FONTS_INPUT_DIR);
            }

            File[] ttfFiles = FONTS_INPUT_DIR.toFile().listFiles((dir, name) -> name.toLowerCase().endsWith(".ttf"));

            if (ttfFiles != null && ttfFiles.length > 0) {
                Path fontDir = ResourcepackManager.RESOURCE_PACK_DIR.resolve("assets").resolve("custom_tooltip_api").resolve("font");

                if (!Files.exists(fontDir)) {
                    Files.createDirectories(fontDir);
                }

                for (File file : ttfFiles) {
                    String rawName = file.getName().replace(".ttf", "").toLowerCase().replaceAll("[^a-z0-9_.-]", "");
                    String fontIdentifier = "custom_tooltip_api:" + rawName;

                    Files.copy(file.toPath(), fontDir.resolve(rawName + ".ttf"), StandardCopyOption.REPLACE_EXISTING);

                    File fontJson = fontDir.resolve(rawName + ".json").toFile();
                    try (FileWriter writer = new FileWriter(fontJson)) {
                        writer.write(getFontJsonString(fontIdentifier));
                    }

                    availableFonts.add(fontIdentifier);
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Could not load fonts: {}", e.getMessage());
        }
    }

    private static String getFontJsonString(String fontIdentifier) {
        return """
                {
                    "providers": [
                        {
                            "type": "ttf",
                            "file": "%s.ttf",
                            "shift": [0, 1],
                            "size": 11.0,
                            "oversample": 4.0
                        }
                    ]
                }
                """.formatted(fontIdentifier);
    }
}
