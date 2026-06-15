package com.stalemated.customtooltips.registry;

import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import com.stalemated.lib.helper.attribute.AttributeGetter;

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

        PlaceholderRegistry.register("enchantments", AttributeGetter::getEnchantments);
        PlaceholderRegistry.register("repair_cost", stack -> String.valueOf(stack.getRepairCost()));
        PlaceholderRegistry.register("unbreakable", stack -> (stack.hasNbt() && Objects.requireNonNull(stack.getNbt()).getBoolean("Unbreakable")) ? Text.translatable("customtooltips.unbreakable_item").getString() : "");

        PlaceholderRegistry.register("weapon_damage", AttributeGetter::calculateWeaponDamage);
        PlaceholderRegistry.register("weapon_speed", AttributeGetter::calculateWeaponSpeed);

        PlaceholderRegistry.register("food_hunger", AttributeGetter::getHunger);
        PlaceholderRegistry.register("food_saturation", AttributeGetter::getSaturation);

        PlaceholderRegistry.register("nbt", stack -> String.valueOf(stack.getNbt()));
    }
}