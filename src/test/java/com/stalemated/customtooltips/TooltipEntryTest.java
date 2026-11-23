package com.stalemated.customtooltips;

import com.stalemated.lib.util.ResourceIds;
import net.minecraft.SharedConstants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.Bootstrap;
import net.minecraft.tags.TagKey;
//? if >=1.21.2 {
/*import net.minecraft.tags.TagLoader;
*///?}
//? if >=26.1 {
/*import net.minecraft.core.component.DataComponentInitializers;
import net.minecraft.data.registries.VanillaRegistries;
*///?}
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TooltipEntryTest {
    //? if fabric {
    @BeforeAll
    static void bootMinecraft() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        //? if >=26.3 {
        /*BuiltInRegistries.DATA_COMPONENT_INITIALIZERS.build(VanillaRegistries.createWorldLookup()).forEach(DataComponentInitializers.PendingComponents::apply);
        *///?} elif >=26.1 {
        /*BuiltInRegistries.DATA_COMPONENT_INITIALIZERS.build(VanillaRegistries.createLookup()).forEach(DataComponentInitializers.PendingComponents::apply);
        *///?}
        TagKey<Item> swords = TagKey.create(Registries.ITEM, ResourceIds.of("c", "swords"));
        //? if >=1.21.2 {
        /*BuiltInRegistries.ITEM.prepareTagReload(new TagLoader.LoadResult<Item>(Registries.ITEM, Map.of(swords, List.of(Items.DIAMOND_SWORD.builtInRegistryHolder(), Items.IRON_SWORD.builtInRegistryHolder())))).apply();
        *///?} else {
        BuiltInRegistries.ITEM.bindTags(Map.of(swords, List.of(Items.DIAMOND_SWORD.builtInRegistryHolder(), Items.IRON_SWORD.builtInRegistryHolder())));
        //?}
    }

    @AfterAll
    static void unbindTags() {
        //? if >=1.21.2 {
        /*BuiltInRegistries.ITEM.prepareTagReload(new TagLoader.LoadResult<Item>(Registries.ITEM, Map.of())).apply();
        *///?} else {
        BuiltInRegistries.ITEM.resetTags();
        //?}
    }

    @Test
    void anItemTargetMatchesOnlyThatItem() {
        TooltipEntry entry = entryFor("minecraft:diamond_sword");

        assertTrue(entry.matches(new ItemStack(Items.DIAMOND_SWORD)));
        assertFalse(entry.matches(new ItemStack(Items.IRON_SWORD)));
    }

    @Test
    void aTagTargetMatchesEveryItemInTheTag() {
        TooltipEntry entry = entryFor("#c:swords");

        assertTrue(entry.matches(new ItemStack(Items.DIAMOND_SWORD)));
        assertTrue(entry.matches(new ItemStack(Items.IRON_SWORD)));
        assertFalse(entry.matches(new ItemStack(Items.DIAMOND_PICKAXE)));
    }

    @Test
    void aNamespaceTargetMatchesEveryItemOfThatNamespace() {
        assertTrue(entryFor("minecraft:*").matches(new ItemStack(Items.STICK)));
        assertFalse(entryFor("othermod:*").matches(new ItemStack(Items.STICK)));
    }

    @Test
    void aRegexTargetHasToMatchTheWholeId() {
        TooltipEntry entry = entryFor("regex:minecraft:.*_sword");

        assertTrue(entry.matches(new ItemStack(Items.DIAMOND_SWORD)));
        assertFalse(entry.matches(new ItemStack(Items.DIAMOND_PICKAXE)));
        assertFalse(entryFor("regex:diamond").matches(new ItemStack(Items.DIAMOND_SWORD)));
    }

    @Test
    void aNegatedTargetMatchesEverythingElse() {
        TooltipEntry entry = entryFor("!#c:swords");

        assertFalse(entry.matches(new ItemStack(Items.DIAMOND_SWORD)));
        assertTrue(entry.matches(new ItemStack(Items.STICK)));
    }

    @Test
    void anInvalidTargetMatchesNothing() {
        assertFalse(entryFor("minecraft:Diamond_Sword").matches(new ItemStack(Items.DIAMOND_SWORD)));
    }

    @Test
    void aDisplayConditionCanRejectAMatchingItem() {
        TooltipEntry entry = entryFor("minecraft:diamond_sword");
        entry.displayCondition = stack -> stack.getCount() > 1;

        assertFalse(entry.matches(new ItemStack(Items.DIAMOND_SWORD)));
        assertTrue(entry.matches(new ItemStack(Items.DIAMOND_SWORD, 2)));
    }

    private static TooltipEntry entryFor(String target) {
        TooltipEntry entry = new TooltipEntry();
        entry.target = target;
        return entry;
    }
    //?}
}
