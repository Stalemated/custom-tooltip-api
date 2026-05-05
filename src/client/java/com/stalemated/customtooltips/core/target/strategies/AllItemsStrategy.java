package com.stalemated.customtooltips.core.target.strategies;

import com.stalemated.customtooltips.core.target.TargetMatcher;
import net.minecraft.item.ItemStack;

public class AllItemsStrategy implements TargetMatcher {
    @Override public boolean matches(ItemStack stack) {
        return true;
    }
}