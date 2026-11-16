package com.stalemated.customtooltips.core;

import com.stalemated.customtooltips.TooltipEntry;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ActiveEntriesTest {
    //? if fabric {
    @BeforeAll
    static void bootMinecraft() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void entriesApplyInListOrderOnlyToTheirTarget() {
        TooltipEntry first = entryFor("minecraft:diamond_sword");
        TooltipEntry pickaxe = entryFor("minecraft:diamond_pickaxe");
        TooltipEntry last = entryFor("minecraft:diamond_sword");
        ActiveEntries active = new ActiveEntries(List.of(first, pickaxe, last), Set.of());

        assertEquals(List.of(first, last), active.match(new ItemStack(Items.DIAMOND_SWORD), false).shown());
    }

    @Test
    void aDisabledEntryIsSkipped() {
        TooltipEntry enabled = entryFor("minecraft:diamond_sword");
        TooltipEntry disabled = entryFor("minecraft:diamond_sword");
        ActiveEntries active = new ActiveEntries(List.of(enabled, disabled), Set.of(disabled.getIdentifier()));

        assertEquals(List.of(enabled), active.match(new ItemStack(Items.DIAMOND_SWORD), false).shown());
    }

    @Test
    void anEntryThatNeedsTheKeyWaitsUntilItIsHeld() {
        TooltipEntry entry = entryFor("minecraft:diamond_sword");
        entry.requireKeybind = true;
        ActiveEntries active = new ActiveEntries(List.of(entry), Set.of());
        ItemStack sword = new ItemStack(Items.DIAMOND_SWORD);

        MatchedEntries keyUp = active.match(sword, false);
        assertEquals(List.of(), keyUp.shown());
        assertTrue(keyUp.waitingForKey());

        MatchedEntries keyDown = active.match(sword, true);
        assertEquals(List.of(entry), keyDown.shown());
        assertFalse(keyDown.waitingForKey());
    }

    @Test
    void eachItemConditionNeedsItsOwnItemState() {
        TooltipEntry damagedOnly = entryFor("minecraft:diamond_sword");
        damagedOnly.showOnlyIfDamaged = true;
        TooltipEntry enchantedOnly = entryFor("minecraft:diamond_sword");
        enchantedOnly.showOnlyIfEnchanted = true;
        TooltipEntry unbreakableOnly = entryFor("minecraft:diamond_sword");
        unbreakableOnly.showOnlyIfUnbreakable = true;
        ActiveEntries active = new ActiveEntries(List.of(damagedOnly, enchantedOnly, unbreakableOnly), Set.of());

        ItemStack damaged = new ItemStack(Items.DIAMOND_SWORD);
        damaged.setDamageValue(10);
        ItemStack enchanted = new ItemStack(Items.DIAMOND_SWORD);
        enchanted.enchant(Enchantments.SHARPNESS, 1);
        ItemStack unbreakable = new ItemStack(Items.DIAMOND_SWORD);
        unbreakable.getOrCreateTag().putBoolean("Unbreakable", true);

        assertEquals(List.of(), active.match(new ItemStack(Items.DIAMOND_SWORD), false).shown());
        assertEquals(List.of(damagedOnly), active.match(damaged, false).shown());
        assertEquals(List.of(enchantedOnly), active.match(enchanted, false).shown());
        assertEquals(List.of(unbreakableOnly), active.match(unbreakable, false).shown());
    }

    private static TooltipEntry entryFor(String target) {
        TooltipEntry entry = new TooltipEntry();
        entry.target = target;
        return entry;
    }
    //?}
}
