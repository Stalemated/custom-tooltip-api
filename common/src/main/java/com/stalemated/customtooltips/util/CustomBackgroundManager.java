package com.stalemated.customtooltips.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import com.stalemated.customtooltips.config.ExternalBackgroundsConfig;
import com.stalemated.customtooltips.ConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

import com.stalemated.lib.helper.PlatformHelper;
import static com.stalemated.customtooltips.CustomTooltipApiClient.LOGGER;

public class CustomBackgroundManager {
    private static final Path BACKGROUNDS_INPUT_DIR = PlatformHelper.INSTANCE.getConfigDir().resolve("custom_tooltip_api").resolve("backgrounds");

    public static List<String> availableBackgrounds = new ArrayList<>();

    private static final List<String> localBackgrounds = new ArrayList<>();

    public static void loadAndGenerateBackgrounds() {
        localBackgrounds.clear();
        availableBackgrounds.clear();

        try {
            if (!Files.exists(BACKGROUNDS_INPUT_DIR)) Files.createDirectories(BACKGROUNDS_INPUT_DIR);

            File[] pngFiles = BACKGROUNDS_INPUT_DIR.toFile().listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));

            if (pngFiles != null && pngFiles.length > 0) {
                Path texturesDir = ResourcepackManager.RESOURCE_PACK_DIR.resolve("assets").resolve("custom_tooltip_api").resolve("textures").resolve("gui").resolve("tooltip_backgrounds");

                if (!Files.exists(texturesDir)) Files.createDirectories(texturesDir);

                for (File file : pngFiles) {
                    String rawName = file.getName().replace(".png", "").toLowerCase().replaceAll("[^a-z0-9_.-]", "");
                    String textureIdentifier = "custom_tooltip_api:textures/gui/tooltip_backgrounds/" + rawName + ".png";

                    Path targetFile = texturesDir.resolve(rawName + ".png");
                    if (!Files.exists(targetFile)) {
                        Files.copy(file.toPath(), targetFile, StandardCopyOption.REPLACE_EXISTING);
                    }
                    localBackgrounds.add(textureIdentifier);
                    availableBackgrounds.add(textureIdentifier);

                    File mcmetaFile = new File(file.getParentFile(), file.getName() + ".mcmeta");
                    if (mcmetaFile.exists()) {
                        Path targetMcmeta = texturesDir.resolve(rawName + ".png.mcmeta");
                        if (!Files.exists(targetMcmeta)) {
                            Files.copy(mcmetaFile.toPath(), targetMcmeta, StandardCopyOption.REPLACE_EXISTING);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Could not load backgrounds: {}", e.getMessage());
        }
    }

    public static void reloadExternalBackgrounds() {
        try {
            ConfigManager.EXTERNAL_BG_HANDLER.load();
            ExternalBackgroundsConfig config = ConfigManager.EXTERNAL_BG_HANDLER.instance();

            availableBackgrounds.clear();
            availableBackgrounds.addAll(localBackgrounds);

            for (String tex : config.textures) {
                if (!availableBackgrounds.contains(tex)) {
                    availableBackgrounds.add(tex);
                }
            }

            if (MinecraftClient.getInstance().getResourceManager() != null) {
                for (String dir : config.directories) {
                    try {
                        String[] parts = dir.split(":");
                        if (parts.length != 2) continue;
                        String namespace = parts[0];
                        String path = parts[1];

                        var resources = MinecraftClient.getInstance().getResourceManager().findResources(path, id -> id.getNamespace().equals(namespace) && id.getPath().endsWith(".png"));
                        
                        for (Identifier id : resources.keySet()) {
                            String textureIdentifier = id.getNamespace() + ":" + id.getPath();
                            if (!availableBackgrounds.contains(textureIdentifier)) {
                                availableBackgrounds.add(textureIdentifier);
                            }
                        }
                    } catch (Exception e) {
                        LOGGER.warn("Error scanning directory {}: {}", dir, e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Could not reload external backgrounds: {}", e.getMessage());
        }
    }
}