package com.stalemated.customtooltips.core.target.strategies;

import com.stalemated.customtooltips.core.target.TargetMatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

import java.util.regex.Pattern;

public class RegexStrategy implements TargetMatcher {
    private final Pattern pattern;

    public RegexStrategy(String regex) { this.pattern = Pattern.compile(regex); }

    @Override public boolean matches(ItemStack stack) {
        return pattern.matcher(Registries.ITEM.getId(stack.getItem()).toString()).matches();
    }
}