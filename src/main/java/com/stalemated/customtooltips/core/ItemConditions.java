package com.stalemated.customtooltips.core;

import net.minecraft.world.item.ItemStack;
//? if >=1.20.5 {
/*import net.minecraft.core.component.DataComponents;
*///?} else {
import net.minecraft.nbt.CompoundTag;
//?}

public final class ItemConditions {
    //? if <1.20.5
    private static final String UNBREAKABLE_TAG = "Unbreakable";

    private ItemConditions() {
    }

    public static boolean isUnbreakable(ItemStack stack) {
        //? if >=1.20.5 {
        /*return stack.has(DataComponents.UNBREAKABLE);
        *///?} else {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.getBoolean(UNBREAKABLE_TAG);
        //?}
    }
}
