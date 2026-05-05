package com.stalemated.customtooltips.registry;

import net.minecraft.entity.EntityGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
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
        PlaceholderRegistry.register("repair_cost", stack -> String.valueOf(stack.getRepairCost()));
        PlaceholderRegistry.register("unbreakable", stack -> (stack.hasNbt() && Objects.requireNonNull(stack.getNbt()).getBoolean("Unbreakable")) ? Text.translatable("customtooltips.unbreakable_item").toString() : "");

        PlaceholderRegistry.register("weapon_damage", DefaultPlaceholders::calculateWeaponDamage);
        PlaceholderRegistry.register("weapon_speed", DefaultPlaceholders::calculateWeaponSpeed);

        PlaceholderRegistry.register("food_hunger", DefaultPlaceholders::getHunger);
        PlaceholderRegistry.register("food_saturation", DefaultPlaceholders::getSaturation);

        PlaceholderRegistry.register("nbt", stack -> String.valueOf(stack.getNbt()));
    }

    // Helpers

    private static String getEnchantments(ItemStack stack) {
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(stack);
        if (enchantments.isEmpty()) return "";

        List<String> formattedEnchants = new ArrayList<>();
        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            formattedEnchants.add(entry.getKey().getName(entry.getValue()).getString());
        }

        return String.join("\n", formattedEnchants);
    }

    private static String calculateWeaponDamage(ItemStack stack) {
        Collection<EntityAttributeModifier> modifiers = stack.getAttributeModifiers(EquipmentSlot.MAINHAND).get(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        double enchantDamage = EnchantmentHelper.getAttackDamage(stack, EntityGroup.DEFAULT);
        if (modifiers.isEmpty() && enchantDamage == 0) return "0";

        double damage = 1.0;
        for (EntityAttributeModifier modifier : modifiers) {
            damage += modifier.getValue();
        }
        damage += enchantDamage;
        return formatString((float) damage);
    }

    private static String calculateWeaponSpeed(ItemStack stack) {
        Collection<EntityAttributeModifier> modifiers = stack.getAttributeModifiers(EquipmentSlot.MAINHAND).get(EntityAttributes.GENERIC_ATTACK_SPEED);
        if (modifiers.isEmpty()) return "0";

        double speed = 4.0;
        for (EntityAttributeModifier modifier : modifiers) {
            speed += modifier.getValue();
        }
        return formatString((float) speed);
    }

    private static String getSaturation(ItemStack stack) {
        if (stack.getItem().isFood()) {
            assert stack.getItem().getFoodComponent() != null;
            return formatString(stack.getItem().getFoodComponent().getSaturationModifier());
        }
        return "";
    }

    private static String getHunger(ItemStack stack) {
        if (stack.getItem().isFood()) {
            assert stack.getItem().getFoodComponent() != null;
            return String.valueOf(stack.getItem().getFoodComponent().getHunger());
        }
        return "";
    }

    private static String formatString(float unformatted) {
        return String.format(Locale.US, "%.1f", unformatted);
    }
}