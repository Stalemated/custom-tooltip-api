package com.stalemated.customtooltips.registry;

import com.stalemated.customtooltips.core.ItemConditions;
import com.stalemated.lib.helper.attribute.AttributeGetter;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
//? if >=1.20.5 {
/*import net.minecraft.core.component.DataComponents;
*///?}

public class DefaultPlaceholders {

    public static void registerDefaults() {
        PlaceholderRegistry.register("durability", stack -> stack.isDamageableItem() ? String.valueOf(stack.getMaxDamage() - stack.getDamageValue()) : "0");
        PlaceholderRegistry.register("max_durability", stack -> stack.isDamageableItem() ? String.valueOf(stack.getMaxDamage()) : "0");
        PlaceholderRegistry.register("item_damage", stack -> stack.isDamageableItem() ? String.valueOf(stack.getDamageValue()) : "0");

        PlaceholderRegistry.register("count", stack -> String.valueOf(stack.getCount()));
        PlaceholderRegistry.register("max_count", stack -> String.valueOf(stack.getMaxStackSize()));

        PlaceholderRegistry.register("item_name", stack -> stack.getHoverName().getString());
        PlaceholderRegistry.register("item_id", stack -> BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());

        PlaceholderRegistry.register("enchantments", AttributeGetter::getEnchantments);
        //? if >=1.20.5 {
        /*PlaceholderRegistry.register("repair_cost", stack -> String.valueOf(stack.getOrDefault(DataComponents.REPAIR_COST, 0)));
        *///?} else {
        PlaceholderRegistry.register("repair_cost", stack -> String.valueOf(stack.getBaseRepairCost()));
        //?}
        PlaceholderRegistry.register("unbreakable", stack -> ItemConditions.isUnbreakable(stack) ? Component.translatable("customtooltips.unbreakable_item").getString() : "");

        PlaceholderRegistry.register("weapon_damage", AttributeGetter::calculateWeaponDamage);
        PlaceholderRegistry.register("weapon_speed", AttributeGetter::calculateWeaponSpeed);

        PlaceholderRegistry.register("food_hunger", AttributeGetter::getHunger);
        PlaceholderRegistry.register("food_saturation", AttributeGetter::getSaturation);

        //? if >=1.20.5 {
        /*PlaceholderRegistry.register("nbt", stack -> String.valueOf(stack.getComponentsPatch()));
        *///?} else {
        PlaceholderRegistry.register("nbt", stack -> String.valueOf(stack.getTag()));
        //?}
    }
}