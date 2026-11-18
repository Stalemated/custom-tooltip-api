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
}
