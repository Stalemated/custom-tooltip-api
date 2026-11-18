package com.stalemated.customtooltips.resource;

import com.stalemated.customtooltips.CustomTooltips;
import com.stalemated.customtooltips.config.CustomTooltipsConfigRegistry;
import com.stalemated.lib.client.resource.PackFiles;
import com.stalemated.lib.client.resource.SLibResourcePacks;
import com.stalemated.lib.helper.PlatformHelper;
import com.stalemated.lib.util.ResourceIds;
import com.stalemated.lib.util.io.PathUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import static com.stalemated.customtooltips.CustomTooltips.LOGGER;

public final class ConfigFolderPack {
    public static final String BACKGROUNDS_FOLDER = "textures/gui/tooltip_backgrounds";
    static final String FONTS_FOLDER = "font";
    private static final String OLD_PACK_NAME = "Custom Tooltip API - Resources";
    private static final Pattern INVALID_NAME_CHARACTERS = Pattern.compile("[^a-z0-9_.-]");

    private ConfigFolderPack() {
    }

    public static void register() {
        Path backgrounds = PathUtils.buildPath(CustomTooltipsConfigRegistry.CONFIG_FOLDER, "backgrounds");
        Path fonts = PathUtils.buildPath(CustomTooltipsConfigRegistry.CONFIG_FOLDER, "fonts");
        createFolder(backgrounds);
        createFolder(fonts);
        warnAboutOldPack();
        SLibResourcePacks.registerClientPack(ResourceIds.of(CustomTooltips.MOD_ID, "resources"),
                Component.translatable("customtooltips.resource_pack.title"), files -> collect(files, backgrounds, fonts));
    }

    static void collect(PackFiles files, Path backgrounds, Path fonts) throws IOException {
        for (Map.Entry<String, Path> background : byCleanName(backgrounds, ".png").entrySet()) {
            String path = BACKGROUNDS_FOLDER + "/" + background.getKey() + ".png";
            Path png = background.getValue();
            files.file(packId(path), png);
            Path mcmeta = png.resolveSibling(png.getFileName() + ".mcmeta");
            if (Files.isRegularFile(mcmeta)) files.file(packId(path + ".mcmeta"), mcmeta);
        }
        for (Map.Entry<String, Path> font : byCleanName(fonts, ".ttf").entrySet()) {
            String name = font.getKey();
            files.file(packId(FONTS_FOLDER + "/" + name + ".ttf"), font.getValue());
            files.bytes(packId(FONTS_FOLDER + "/" + name + ".json"), fontDefinition(name));
        }
    }

    private static Map<String, Path> byCleanName(Path folder, String extension) throws IOException {
        Map<String, Path> byName = new LinkedHashMap<>();
        if (!Files.isDirectory(folder)) return byName;

        for (Path file : filesEndingWith(folder, extension)) {
            String name = cleanName(file, extension);
            if (name.isEmpty()) {
                LOGGER.warn("Skipping {}: nothing is left of its name after keeping only a-z, 0-9, '_', '.' and '-'", file);
                continue;
            }
            Path first = byName.putIfAbsent(name, file);
            if (first != null) LOGGER.warn("Skipping {}: {} already loads as '{}'", file, first.getFileName(), name);
        }
        return byName;
    }

    private static List<Path> filesEndingWith(Path folder, String extension) throws IOException {
        try (Stream<Path> listed = Files.list(folder)) {
            return listed.filter(file -> fileName(file).toLowerCase(Locale.ROOT).endsWith(extension))
                    .filter(Files::isRegularFile)
                    .sorted(Comparator.comparing(ConfigFolderPack::fileName))
                    .toList();
        }
    }

    private static String cleanName(Path file, String extension) {
        String fileName = fileName(file);
        String withoutExtension = fileName.substring(0, fileName.length() - extension.length());
        return INVALID_NAME_CHARACTERS.matcher(withoutExtension.toLowerCase(Locale.ROOT)).replaceAll("");
    }

    private static String fileName(Path file) {
        return file.getFileName().toString();
    }

    private static ResourceLocation packId(String path) {
        return ResourceIds.of(CustomTooltips.PACK_NAMESPACE, path);
    }

    private static byte[] fontDefinition(String name) {
        return """
                {
                    "providers": [
                        {
                            "type": "ttf",
                            "file": "%s:%s.ttf",
                            "shift": [0, 1],
                            "size": 11.0,
                            "oversample": 4.0
                        }
                    ]
                }
                """.formatted(CustomTooltips.PACK_NAMESPACE, name).getBytes(StandardCharsets.UTF_8);
    }

    private static void createFolder(Path folder) {
        try {
            Files.createDirectories(folder);
        } catch (IOException e) {
            LOGGER.warn("Could not create {}: {}", folder, e.getMessage());
        }
    }

    private static void warnAboutOldPack() {
        Path oldPack = PlatformHelper.getGameDir().resolve("resourcepacks").resolve(OLD_PACK_NAME);
        if (!Files.isDirectory(oldPack)) return;

        removeOldBlockAtlas(oldPack);
        LOGGER.warn("{} comes from an older Custom Tooltip API and is no longer needed: backgrounds and fonts now load from {} on their own. You can turn it off or delete it",
                oldPack, PathUtils.buildPath(CustomTooltipsConfigRegistry.CONFIG_FOLDER));
    }

    private static void removeOldBlockAtlas(Path oldPack) {
        Path blocksJson = oldPack.resolve("assets").resolve("minecraft").resolve("atlases").resolve("blocks.json");
        try {
            if (Files.deleteIfExists(blocksJson)) LOGGER.info("Removed {}, tooltip backgrounds have their own atlas now", blocksJson);
        } catch (IOException e) {
            LOGGER.warn("Could not remove {}, so the old pack keeps lowering the block mipmaps while it is on: {}", blocksJson, e.getMessage());
        }
    }
}
