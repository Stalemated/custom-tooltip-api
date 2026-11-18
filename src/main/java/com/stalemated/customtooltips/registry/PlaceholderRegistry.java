package com.stalemated.customtooltips.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import net.minecraft.world.item.ItemStack;

public class PlaceholderRegistry {
    private static final Map<String, Function<ItemStack, String>> PLACEHOLDERS = new ConcurrentHashMap<>();

    static {
        DefaultPlaceholders.registerDefaults();
    }

    public static void register(String key, Function<ItemStack, String> provider) {
        PLACEHOLDERS.put(key, provider);
    }

    public static Function<ItemStack, String> getProvider(String key) {
        return PLACEHOLDERS.get(key);
    }
}
