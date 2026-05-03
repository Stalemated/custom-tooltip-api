package com.stalemated.customtooltips.api;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.core.TooltipRegistry;
import com.stalemated.customtooltips.registry.PlaceholderRegistry;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;


/**
 * The main Developer API for Custom Tooltip API.
 * <p>
 * Allows custom tooltips to be registered dynamically from other mods by calling these methods,
 * completely bypassing the JSON5 configuration file and the in-game settings menu.
 */
public class CustomTooltipApi {

    private static final List<TooltipEntry> API_ENTRIES = new ArrayList<>();

    /**
     * Registers a built {@link TooltipEntry} into the active tooltip registry.
     * Tooltips registered this way are volatile and will be applied instantly.
     * <p>
     * <b>Tip:</b> You can use {@code TooltipEntry.builder(...).register()} to build and register in a single chain.
     *
     * @param entry The TooltipEntry object to register.
     */
    public static void registerTooltip(TooltipEntry entry) {
        if (entry != null) {
            entry.apiEntry = true;
            entry.apiEntryId = entry.target;
            API_ENTRIES.add(entry);
            TooltipRegistry.reload();
        }
    }

    /**
     * Retrieves an unmodifiable list of all tooltips registered dynamically via the API.
     *
     * @return A list containing all API-registered TooltipEntries.
     */
    public static List<TooltipEntry> getApiEntries() {
        return Collections.unmodifiableList(API_ENTRIES);
    }

    /**
     * Registers a custom placeholder variable to be used in tooltips.
     * <p>
     * Example: {@code CustomTooltipApi.registerPlaceholder("mana", stack -> getMana(stack));}
     * Allows users to write {@code %mana%} in their tooltips.
     *
     * @param key      The string key without percentage signs (e.g., "energy", "durability").
     * @param provider A function providing the string replacement based on the item stack.
     */
    public static void registerPlaceholder(String key, Function<ItemStack, String> provider) {
        PlaceholderRegistry.register(key, provider);
    }
}