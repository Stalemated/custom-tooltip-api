package com.stalemated.customtooltips.core;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.api.CustomTooltipApi;
import com.stalemated.customtooltips.config.TooltipConfig;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TooltipRegistryTest {
    //? if fabric {
    @BeforeAll
    static void bootMinecraft() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void anIdCanBeRegisteredOnlyOnce() {
        CustomTooltipApi.builder("minecraft:stick").id("cta_test:only_once").register();

        IllegalArgumentException twice = assertThrows(IllegalArgumentException.class,
                () -> CustomTooltipApi.builder("minecraft:apple").id("cta_test:only_once").register());
        assertEquals("tooltip id cta_test:only_once is already registered", twice.getMessage());
    }

    @Test
    void aTooltipDisabledUnderItsOldIdStaysHidden() {
        TooltipEntry lore = CustomTooltipApi.builder("minecraft:blaze_rod").addLine("Old lore").id("cta_test:blaze_lore").build();
        TooltipConfig config = new TooltipConfig();
        config.disabled_entries.add(lore.legacyApiIdentifier());

        try {
            CustomTooltipApi.registerTooltip(lore);
            TooltipRegistry.reload(config);

            assertTrue(TooltipRegistry.isDisabled(lore));
            assertEquals(List.of(), TooltipRegistry.activeEntries().match(new ItemStack(Items.BLAZE_ROD), false).shown());
        } finally {
            TooltipRegistry.reload(new TooltipConfig());
        }
    }
    //?}
}
