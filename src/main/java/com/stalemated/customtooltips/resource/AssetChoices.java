package com.stalemated.customtooltips.resource;

import com.stalemated.customtooltips.CustomTooltips;
import com.stalemated.customtooltips.config.CustomTooltipsConfigRegistry;
import com.stalemated.customtooltips.config.ExternalBackgroundsConfig;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import static com.stalemated.customtooltips.CustomTooltips.LOGGER;

public final class AssetChoices {
    public static final String BACKGROUND_PREFIX = CustomTooltips.PACK_NAMESPACE + ":" + ConfigFolderPack.BACKGROUNDS_FOLDER + "/";
    private static final String DEFAULT_FONT = "minecraft:default";

    private static volatile List<String> backgrounds = List.of();
    private static volatile List<String> fonts = List.of(DEFAULT_FONT);

    private AssetChoices() {
    }

    public static List<String> backgrounds() {
        return backgrounds;
    }

    public static List<String> fonts() {
        return fonts;
    }

    public static void reload(ResourceManager resources) {
        backgrounds = backgroundsIn(resources, CustomTooltipsConfigRegistry.EXTERNAL_BACKGROUNDS.get());
        fonts = fontsIn(resources);
    }

    private static List<String> backgroundsIn(ResourceManager resources, ExternalBackgroundsConfig external) {
        Set<String> found = new LinkedHashSet<>(pngsIn(resources, CustomTooltips.PACK_NAMESPACE, ConfigFolderPack.BACKGROUNDS_FOLDER));
        found.addAll(external.textures);
        for (String folder : external.directories) {
            String[] parts = folder.split(":");
            if (parts.length != 2) {
                LOGGER.warn("Skipping '{}' in the directories of external_backgrounds.json5, it has to look like namespace:path", folder);
                continue;
            }
            found.addAll(pngsIn(resources, parts[0], parts[1]));
        }
        return List.copyOf(found);
    }

    private static List<String> pngsIn(ResourceManager resources, String namespace, String folder) {
        return resources.listResources(folder, id -> id.getNamespace().equals(namespace) && id.getPath().endsWith(".png"))
                .keySet().stream()
                .sorted()
                .map(ResourceLocation::toString)
                .toList();
    }

    private static List<String> fontsIn(ResourceManager resources) {
        Set<String> found = new LinkedHashSet<>(List.of(DEFAULT_FONT));
        resources.listResources(ConfigFolderPack.FONTS_FOLDER, id -> id.getNamespace().equals(CustomTooltips.PACK_NAMESPACE) && id.getPath().endsWith(".json"))
                .keySet().stream()
                .sorted()
                .map(AssetChoices::fontId)
                .forEach(found::add);
        return List.copyOf(found);
    }

    private static String fontId(ResourceLocation definition) {
        String path = definition.getPath();
        return definition.getNamespace() + ":" + path.substring(ConfigFolderPack.FONTS_FOLDER.length() + 1, path.length() - ".json".length());
    }
}
