package com.stalemated.customtooltips.core.target;

import net.minecraft.item.ItemStack;

public interface TargetMatcher {
    boolean matches(ItemStack stack);
}