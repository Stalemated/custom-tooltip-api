package com.stalemated.customtooltips.core.builder;

import com.stalemated.customtooltips.TooltipEntry;
import java.awt.Color;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TooltipEntryBuilderImplTest {

    @Test
    void anOpacityOutsideZeroToOneIsRejectedWithItsValue() {
        TooltipEntryBuilderImpl builder = new TooltipEntryBuilderImpl("minecraft:stick");

        IllegalArgumentException oldScale = assertThrows(IllegalArgumentException.class, () -> builder.backgroundOpacity(240));
        assertEquals("background opacity must be 0-1, got 240.0", oldScale.getMessage());
        assertThrows(IllegalArgumentException.class, () -> builder.borderOpacity(Float.NaN));

        assertEquals(1f, builder.backgroundOpacity(1).borderOpacity(0.5f).build().backgroundOpacity);
    }

    @Test
    void aColorThatCannotBeReadIsRejectedWithItsValue() {
        TooltipEntryBuilderImpl builder = new TooltipEntryBuilderImpl("minecraft:stick");

        IllegalArgumentException border = assertThrows(IllegalArgumentException.class, () -> builder.borderColors("#FF0000", "blurple"));
        assertEquals("border color must be AARRGGBB or RRGGBB hex, a color name or an & code, got 'blurple'", border.getMessage());
        assertThrows(IllegalArgumentException.class, () -> builder.backgroundColors(List.of("#FF00")));
        assertThrows(IllegalArgumentException.class, () -> builder.colors("&l"));

        TooltipEntry entry = builder.colors("gold").borderColors("#80FF0000").backgroundColors("&c").build();
        assertEquals(List.of(TextColor.fromLegacyFormat(ChatFormatting.GOLD)), entry.colors);
        assertEquals(List.of(new Color(0xFF, 0, 0, 0x80)), entry.borderColors);
        assertEquals(1, entry.backgroundColors.size());
    }

    @Test
    void anIdNeedsANamespaceAndAPath() {
        TooltipEntryBuilderImpl builder = new TooltipEntryBuilderImpl("minecraft:stick");

        IllegalArgumentException spaces = assertThrows(IllegalArgumentException.class, () -> builder.id("skull multishot"));
        assertEquals("tooltip id must be namespace:path, got 'skull multishot'", spaces.getMessage());
        assertThrows(IllegalArgumentException.class, () -> builder.id("skull_multishot"));
        assertThrows(IllegalArgumentException.class, () -> builder.id(":skull_multishot"));

        assertEquals("mutantskeletweaks:skull_multishot", builder.id("mutantskeletweaks:skull_multishot").build().apiId);
    }
}
