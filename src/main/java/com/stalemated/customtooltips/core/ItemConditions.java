package com.stalemated.customtooltips.core;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public final class ItemConditions {
    private static final String UNBREAKABLE_TAG = "Unbreakable";

    private ItemConditions() {
    }

    public static boolean isUnbreakable(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.getBoolean(UNBREAKABLE_TAG);
    }
}
