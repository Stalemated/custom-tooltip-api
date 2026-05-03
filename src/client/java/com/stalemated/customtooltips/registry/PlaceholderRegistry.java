package com.stalemated.customtooltips.registry;

import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class PlaceholderRegistry {
    private static final Map<String, Function<ItemStack, String>> PLACEHOLDERS = new HashMap<>();

    static {
        register("durability", stack -> stack.isDamageable() ? String.valueOf(stack.getMaxDamage() - stack.getDamage()) : "0");
        register("max_durability", stack -> stack.isDamageable() ? String.valueOf(stack.getMaxDamage()) : "0");
        register("item_damage", stack -> stack.isDamageable() ? String.valueOf(stack.getDamage()) : "0");
        register("count", stack -> String.valueOf(stack.getCount()));
        register("max_count", stack -> String.valueOf(stack.getMaxCount()));
        register("item_name", stack -> stack.getName().getString());
        register("item_id", stack -> Registries.ITEM.getId(stack.getItem()).toString());
        register("enchantments", stack -> {
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(stack);
            if (enchantments.isEmpty()) return "";
            
            List<String> formattedEnchants = new ArrayList<>();
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                formattedEnchants.add(entry.getKey().getName(entry.getValue()).getString());
            }
            
            return String.join("\n", formattedEnchants);
        });
        register("nbt", stack -> String.valueOf(stack.getNbt()));
        register("repair_cost", stack -> String.valueOf(stack.getRepairCost()));
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