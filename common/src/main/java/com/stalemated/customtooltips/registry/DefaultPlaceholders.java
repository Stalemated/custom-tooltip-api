package com.stalemated.customtooltips.registry;

import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.text.Text;

import java.util.*;

public class DefaultPlaceholders {

    public static void registerDefaults() {
        PlaceholderRegistry.register("durability", stack -> stack.isDamageable() ? String.valueOf(stack.getMaxDamage() - stack.getDamage()) : "0");
        PlaceholderRegistry.register("max_durability", stack -> stack.isDamageable() ? String.valueOf(stack.getMaxDamage()) : "0");
        PlaceholderRegistry.register("item_damage", stack -> stack.isDamageable() ? String.valueOf(stack.getDamage()) : "0");

        PlaceholderRegistry.register("count", stack -> String.valueOf(stack.getCount()));
        PlaceholderRegistry.register("max_count", stack -> String.valueOf(stack.getMaxCount()));

        PlaceholderRegistry.register("item_name", stack -> stack.getName().getString());
        PlaceholderRegistry.register("item_id", stack -> Registries.ITEM.getId(stack.getItem()).toString());

        PlaceholderRegistry.register("enchantments", DefaultPlaceholders::getEnchantments);
        PlaceholderRegistry.register("repair_cost", stack -> String.valueOf(stack.getOrDefault(DataComponentTypes.REPAIR_COST, 0)));
        PlaceholderRegistry.register("unbreakable", stack -> stack.contains(DataComponentTypes.UNBREAKABLE) ? Text.translatable("customtooltips.unbreakable_item").getString() : "");

        PlaceholderRegistry.register("weapon_damage", DefaultPlaceholders::calculateWeaponDamage);
        PlaceholderRegistry.register("weapon_speed", DefaultPlaceholders::calculateWeaponSpeed);

        PlaceholderRegistry.register("food_hunger", DefaultPlaceholders::getHunger);
        PlaceholderRegistry.register("food_saturation", DefaultPlaceholders::getSaturation);

        PlaceholderRegistry.register("nbt", stack -> String.valueOf(stack.getComponentChanges()));
    }

    // Helpers

    private static String getEnchantments(ItemStack stack) {
        ItemEnchantmentsComponent enchantments = EnchantmentHelper.getEnchantments(stack);
        if (enchantments.isEmpty()) return "";

        List<String> formattedEnchants = new ArrayList<>();
        for (var entry : enchantments.getEnchantmentEntries()) {
            formattedEnchants.add(Enchantment.getName(entry.getKey(), entry.getIntValue()).getString());
        }

        return String.join("\n", formattedEnchants);
    }

    private static String calculateWeaponDamage(ItemStack stack) {
        AttributeModifiersComponent modifiers = stack.getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);

        double damage = 1.0;
        boolean hasModifiers = false;
        for (AttributeModifiersComponent.Entry entry : modifiers.modifiers()) {
            if (entry.attribute().equals(EntityAttributes.GENERIC_ATTACK_DAMAGE)) {
                damage += entry.modifier().value();
                hasModifiers = true;
            }
        }
        if (!hasModifiers) return "0";
        return formatString((float) damage);
    }

    private static String calculateWeaponSpeed(ItemStack stack) {
        AttributeModifiersComponent modifiers = stack.getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);

        double speed = 4.0;
        boolean hasModifiers = false;
        for (AttributeModifiersComponent.Entry entry : modifiers.modifiers()) {
            if (entry.attribute().equals(EntityAttributes.GENERIC_ATTACK_SPEED)) {
                speed += entry.modifier().value();
                hasModifiers = true;
            }
        }
        if (!hasModifiers) return "0";
        return formatString((float) speed);
    }

    private static String getSaturation(ItemStack stack) {
        FoodComponent food = stack.get(DataComponentTypes.FOOD);
        if (food != null) {
            return formatString(food.saturation());
        }
        return "";
    }

    private static String getHunger(ItemStack stack) {
        FoodComponent food = stack.get(DataComponentTypes.FOOD);
        if (food != null) {
            return String.valueOf(food.nutrition());
        }
        return "";
    }

    private static String formatString(float unformatted) {
        return String.format(Locale.US, "%.1f", unformatted);
    }
}