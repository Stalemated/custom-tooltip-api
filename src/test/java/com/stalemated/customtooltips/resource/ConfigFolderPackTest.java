package com.stalemated.customtooltips.resource;

import com.google.gson.JsonParser;
import com.stalemated.lib.client.resource.PackFiles;
import com.stalemated.lib.util.ResourceIds;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfigFolderPackTest {
    @TempDir
    Path config;

    private final ServedFiles served = new ServedFiles();

    @Test
    void aBackgroundIsServedWithItsMcmeta() throws IOException {
        Path png = create("backgrounds/frame.png");
        Path mcmeta = create("backgrounds/frame.png.mcmeta");

        collect();

        assertEquals(Map.of(
                background("frame.png"), png,
                background("frame.png.mcmeta"), mcmeta
        ), served.files);
    }

    @Test
    void anUppercaseExtensionStillCounts() throws IOException {
        Path png = create("backgrounds/Fondo.PNG");

        collect();

        assertEquals(Map.of(background("fondo.png"), png), served.files);
    }

    @Test
    void twoFilesWithTheSameCleanNameKeepTheFirst() throws IOException {
        Path first = create("backgrounds/My Frame.png");
        create("backgrounds/myframe.png");

        collect();

        assertEquals(Map.of(background("myframe.png"), first), served.files);
    }

    @Test
    void eachFontGetsItsJson() throws IOException {
        Path ttf = create("fonts/Pixel.ttf");

        collect();

        assertEquals(Map.of(ResourceIds.of("custom_tooltip_api", "font/pixel.ttf"), ttf), served.files);
        String definition = new String(served.generated.get(ResourceIds.of("custom_tooltip_api", "font/pixel.json")), StandardCharsets.UTF_8);
        String providerFile = JsonParser.parseString(definition).getAsJsonObject()
                .getAsJsonArray("providers").get(0).getAsJsonObject()
                .get("file").getAsString();
        assertEquals("custom_tooltip_api:pixel.ttf", providerFile);
    }

    @Test
    void aMissingFolderIsSkipped() throws IOException {
        Path ttf = create("fonts/pixel.ttf");

        collect();

        assertEquals(Map.of(ResourceIds.of("custom_tooltip_api", "font/pixel.ttf"), ttf), served.files);
    }

    private void collect() throws IOException {
        ConfigFolderPack.collect(served, config.resolve("backgrounds"), config.resolve("fonts"));
    }

    private Path create(String relativePath) throws IOException {
        Path file = config.resolve(relativePath);
        Files.createDirectories(file.getParent());
        return Files.createFile(file);
    }

    private static ResourceLocation background(String fileName) {
        return ResourceIds.of("custom_tooltip_api", "textures/gui/tooltip_backgrounds/" + fileName);
    }

    private static final class ServedFiles implements PackFiles {
        private final Map<ResourceLocation, Path> files = new LinkedHashMap<>();
        private final Map<ResourceLocation, byte[]> generated = new LinkedHashMap<>();

        @Override
        public void file(ResourceLocation location, Path file) {
            if (files.putIfAbsent(location, file) != null) throw new IllegalArgumentException("Pack file '" + location + "' was listed twice");
        }

        @Override
        public void bytes(ResourceLocation location, byte[] content) {
            if (generated.putIfAbsent(location, content) != null) throw new IllegalArgumentException("Pack file '" + location + "' was listed twice");
        }

        @Override
        public void assetsFolder(Path root) {
            throw new UnsupportedOperationException("ConfigFolderPack lists its files one by one");
        }
    }
}
