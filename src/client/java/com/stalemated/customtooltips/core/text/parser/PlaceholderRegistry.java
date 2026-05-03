package com.stalemated.customtooltips.core.text.parser;

import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderRegistry {
    private static final Map<String, Function<ItemStack, String>> PLACEHOLDERS = new HashMap<>();
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("%([a-zA-Z_]+)%");

    static {
        register("durability", stack -> stack.isDamageable() ? String.valueOf(stack.getMaxDamage() - stack.getDamage()) : "0");
        register("max_durability", stack -> stack.isDamageable() ? String.valueOf(stack.getMaxDamage()) : "0");
        register("damage", stack -> stack.isDamageable() ? String.valueOf(stack.getDamage()) : "0");
        register("count", stack -> String.valueOf(stack.getCount()));
        register("max_count", stack -> String.valueOf(stack.getMaxCount()));
        register("item_name", stack -> stack.getName().getString());
        register("namespace", stack -> Registries.ITEM.getId(stack.getItem()).getNamespace());
        register("id", stack -> Registries.ITEM.getId(stack.getItem()).getPath());
    }

    public static void register(String key, Function<ItemStack, String> provider) {
        PLACEHOLDERS.put(key, provider);
    }

    public static String parse(String text, ItemStack stack) {
        if (!text.contains("%")) return text;

        Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            String key = matcher.group(1);
            Function<ItemStack, String> replacer = PLACEHOLDERS.get(key);
            
            if (replacer != null) matcher.appendReplacement(sb, Matcher.quoteReplacement(replacer.apply(stack)));
            else matcher.appendReplacement(sb, Matcher.quoteReplacement(matcher.group(0)));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static boolean containsDynamicPlaceholders(String text) {
        if (text == null || !text.contains("%")) return false;
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
        while (matcher.find()) {
            if (PLACEHOLDERS.containsKey(matcher.group(1))) return true;
        }
        return false;
    }
}