package com.stalemated.customtooltips.core.builder;

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
    void anIdNeedsANamespaceAndAPath() {
        TooltipEntryBuilderImpl builder = new TooltipEntryBuilderImpl("minecraft:stick");

        IllegalArgumentException spaces = assertThrows(IllegalArgumentException.class, () -> builder.id("skull multishot"));
        assertEquals("tooltip id must be namespace:path, got 'skull multishot'", spaces.getMessage());
        assertThrows(IllegalArgumentException.class, () -> builder.id("skull_multishot"));
        assertThrows(IllegalArgumentException.class, () -> builder.id(":skull_multishot"));

        assertEquals("mutantskeletweaks:skull_multishot", builder.id("mutantskeletweaks:skull_multishot").build().apiId);
    }
}
