package com.stalemated.customtooltips.core;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.api.enums.BackgroundType;
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
        assertEquals(BACKGROUND, TooltipColors.scaleAlpha(BACKGROUND, 240));
        assertEquals(BACKGROUND, TooltipColors.backgroundStart(new TooltipEntry(), BACKGROUND));
        assertEquals(BACKGROUND, TooltipColors.backgroundStart(null, BACKGROUND));
    }

    @Test
    void opacityScalesTheAlphaAndStopsAtOpaque() {
        assertEquals(0x78100010, TooltipColors.scaleAlpha(0xF0100010, 120));
        assertEquals(0xFF000000, TooltipColors.scaleAlpha(0xFF000000, 255));
    }

    @Test
    void aChosenBackgroundColorReplacesTheVanillaOne() {
        TooltipEntry entry = new TooltipEntry();
        entry.backgroundColors = List.of(new Color(0xFFFF0000, true));
        entry.backgroundOpacity = 120;

        assertTrue(entry.hasCustomBackground());
        assertEquals(0x7FFF0000, TooltipColors.backgroundStart(entry, BACKGROUND));
    }

    @Test
    void aTextureIsACustomBackgroundOnlyOnceOneIsChosen() {
        TooltipEntry entry = new TooltipEntry();
        entry.backgroundType = BackgroundType.TEXTURE;

        assertFalse(entry.hasCustomBackground());

        entry.backgroundTexture = "custom_tooltip_api:textures/gui/tooltip_backgrounds/stone.png";

        assertTrue(entry.hasCustomBackground());
    }
}
