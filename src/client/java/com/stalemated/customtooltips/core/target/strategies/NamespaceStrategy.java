package com.stalemated.customtooltips.core.target.strategies;

import com.stalemated.customtooltips.core.target.TargetMatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

public class NamespaceStrategy implements TargetMatcher {
    private final String namespace;
    public NamespaceStrategy(String namespace) { this.namespace = namespace; }

    @Override public boolean matches(ItemStack stack) {
        return Registries.ITEM.getId(stack.getItem()).getNamespace().equals(namespace);
    }
}
