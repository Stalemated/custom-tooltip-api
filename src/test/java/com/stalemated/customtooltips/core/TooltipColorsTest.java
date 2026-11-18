package com.stalemated.customtooltips.core;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.api.enums.BackgroundType;
import com.stalemated.lib.config.InMemoryConfigManager;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.util.List;

import static com.stalemated.lib.util.color.VanillaTooltipColors.BACKGROUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TooltipColorsTest {

    @Test
    void theDefaultOpacityLeavesTheColorAsItIs() {
        assertEquals(BACKGROUND, TooltipColors.scaleAlpha(BACKGROUND, 1f));
        assertEquals(BACKGROUND, TooltipColors.backgroundStart(TooltipLook.of(List.of(new TooltipEntry())), BACKGROUND));
        assertEquals(BACKGROUND, TooltipColors.backgroundStart(TooltipLook.VANILLA, BACKGROUND));
    }

    @Test
    void theOpacityMultipliesTheAlphaAndRounds() {
        assertEquals(0x5FFFFFFF, TooltipColors.scaleAlpha(0xAAFFFFFF, 0.56f));
        assertEquals(0x3F000000, TooltipColors.scaleAlpha(0x96000000, 0.42f));
        assertEquals(0x00100010, TooltipColors.scaleAlpha(0xF0100010, 0f));
    }

    @Test
    void aChosenBackgroundColorReplacesTheVanillaOne() {
        TooltipEntry entry = new TooltipEntry();
        entry.backgroundColors = List.of(new Color(0xFFFF0000, true));
        entry.backgroundOpacity = 0.5f;

        assertTrue(entry.hasCustomBackground());
        assertEquals(0x80FF0000, TooltipColors.backgroundStart(TooltipLook.of(List.of(entry)), BACKGROUND));
    }

    @Test
    void aTextureIsACustomBackgroundOnlyOnceOneIsChosen() {
        TooltipEntry entry = new TooltipEntry();
        entry.backgroundType = BackgroundType.TEXTURE;

        assertFalse(entry.hasCustomBackground());

        entry.backgroundTexture = "custom_tooltip_api:textures/gui/tooltip_backgrounds/stone.png";

        assertTrue(entry.hasCustomBackground());
    }

    @Test
    void anOpacitySavedOnTheOldScaleLoadsAsOne() {
        InMemoryConfigManager<TooltipEntry> config = new InMemoryConfigManager<>(new TooltipEntry(), TooltipEntry::new, null);
        TooltipEntry saved = new TooltipEntry();
        saved.backgroundOpacity = 240;
        saved.borderOpacity = 0;

        TooltipEntry loaded = config.cloneConfig(saved);

        assertEquals(1f, loaded.backgroundOpacity);
        assertEquals(0f, loaded.borderOpacity);
    }
}
