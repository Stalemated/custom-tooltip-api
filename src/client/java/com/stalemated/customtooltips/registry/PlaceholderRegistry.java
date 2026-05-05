package com.stalemated.customtooltips.registry;

import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class PlaceholderRegistry {
    private static final Map<String, Function<ItemStack, String>> PLACEHOLDERS = new HashMap<>();

    static {
        DefaultPlaceholders.registerDefaults();
    }

    public static void register(String key, Function<ItemStack, String> provider) {
        PLACEHOLDERS.put(key, provider);
    }

    public static Function<ItemStack, String> getProvider(String key) {
        return PLACEHOLDERS.get(key);
    }

    public static boolean contains(String key) {
        return PLACEHOLDERS.containsKey(key);
    }
}