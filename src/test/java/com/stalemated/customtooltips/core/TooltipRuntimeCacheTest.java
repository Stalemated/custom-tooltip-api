package com.stalemated.customtooltips.core;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.api.enums.BackgroundType;
import com.stalemated.lib.util.ResourceIds;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TooltipRuntimeCacheTest {
    @Test
    void anInvalidBackgroundTextureIsDropped() {
        TooltipRuntimeCache cache = TooltipRuntimeCache.of(texturedEntry("custom_tooltip_api:textures/Bad Name.png"));

        assertNull(cache.backgroundTexture());
        assertNull(cache.backgroundSprite());
    }

    @Test
    void aBackgroundTextureKeepsItsFileAndAtlasSpriteIds() {
        TooltipRuntimeCache cache = TooltipRuntimeCache.of(texturedEntry("custom_tooltip_api:textures/tooltip/frame.png"));

        assertEquals(ResourceIds.of("custom_tooltip_api", "textures/tooltip/frame.png"), cache.backgroundTexture());
        assertEquals(ResourceIds.of("custom_tooltip_api", "tooltip/frame"), cache.backgroundSprite());
    }

    private static TooltipEntry texturedEntry(String backgroundTexture) {
        TooltipEntry entry = new TooltipEntry();
        entry.backgroundType = BackgroundType.TEXTURE;
        entry.backgroundTexture = backgroundTexture;
        return entry;
    }
}
