package com.stalemated.customtooltips.core.target.strategies;

import com.stalemated.customtooltips.core.target.TargetMatcher;
import net.minecraft.item.ItemStack;

public class NoneStrategy implements TargetMatcher {
    @Override public boolean matches(ItemStack stack) { return false; }
}
