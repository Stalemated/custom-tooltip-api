package com.stalemated.customtooltips.api;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.core.TooltipRegistry;
import com.stalemated.customtooltips.core.builder.TooltipEntryBuilderImpl;
import com.stalemated.customtooltips.registry.PlaceholderRegistry;
import java.util.List;
import java.util.function.Function;
import net.minecraft.world.item.ItemStack;


/**
 * The main Developer API for Custom Tooltip API.
 * <p>
 * Allows custom tooltips to be registered dynamically from other mods by calling these methods,
 * completely bypassing the JSON5 configuration file and the in-game settings menu.
 */
public class CustomTooltipApi {

    /**
     * Registers a built {@link TooltipEntry} into the active tooltip registry.
     * Tooltips registered this way are volatile and will be applied instantly.
     * <p>
     * <b>Tip:</b> You can use {@code TooltipEntry.builder(...).register()} to build and register in a single chain.
     *
     * @param entry The TooltipEntry object to register.
     * @throws IllegalArgumentException If a tooltip with the same {@link TooltipBuilder#id(String) id} is already registered.
     */
    public static void registerTooltip(TooltipEntry entry) {
        if (entry != null) {
            TooltipRegistry.registerApiEntry(entry);
        }
    }

    /**
     * Retrieves an unmodifiable list of all tooltips registered dynamically via the API.
     *
     * @return A list containing all API-registered TooltipEntries.
     */
    public static List<TooltipEntry> getApiEntries() {
        return TooltipRegistry.apiEntries();
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

    /**
     * Creates a new Builder instance for configuring a TooltipEntry.
     *
     * @param target The target item ID, tag ("#c:swords"), namespace ("minecraft:*"), regex ("regex:.*sword.*"), or all items ("*").
     * @return A new Builder instance.
     */
    public static TooltipBuilder builder(String target) {
        return new TooltipEntryBuilderImpl(target);
    }

    /**
     * Generates a translation block string to be used in the tooltip.
     * Example: builder.addLine("This is a " + CustomTooltipApi.translate("cta.item.sword"));
     *
     * @param translationKey The translation key.
     * @return The translation block string.
     */
    public static String translate(String translationKey) {
        return "<translate:" + translationKey + ">";
    }
}