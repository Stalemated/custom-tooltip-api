package com.stalemated.customtooltips.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import static com.stalemated.customtooltips.CustomTooltipApiClient.LOGGER;

public class CustomBackgroundManager {
    private static final Path BACKGROUNDS_INPUT_DIR = PlatformHelper.INSTANCE.getConfigDir().resolve("custom_tooltip_api").resolve("backgrounds");

    public static List<String> availableBackgrounds = new ArrayList<>();

    public static void loadAndGenerateBackgrounds() {
        availableBackgrounds.clear();

        try {
            if (!Files.exists(BACKGROUNDS_INPUT_DIR)) Files.createDirectories(BACKGROUNDS_INPUT_DIR);

            File[] pngFiles = BACKGROUNDS_INPUT_DIR.toFile().listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));

            if (pngFiles != null && pngFiles.length > 0) {
                Path texturesDir = ResourcepackManager.RESOURCE_PACK_DIR.resolve("assets").resolve("custom_tooltip_api").resolve("textures").resolve("gui").resolve("tooltip_backgrounds");

                if (!Files.exists(texturesDir)) Files.createDirectories(texturesDir);

                for (File file : pngFiles) {
                    String rawName = file.getName().toLowerCase().replace(".png", "").replaceAll("[^a-z0-9_.-]", "");
                    String textureIdentifier = "custom_tooltip_api:textures/gui/tooltip_backgrounds/" + rawName + ".png";

                    Files.copy(file.toPath(), texturesDir.resolve(rawName + ".png"), StandardCopyOption.REPLACE_EXISTING);
                    availableBackgrounds.add(textureIdentifier);
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Could not load backgrounds: {}", e.getMessage());
        }
    }
}