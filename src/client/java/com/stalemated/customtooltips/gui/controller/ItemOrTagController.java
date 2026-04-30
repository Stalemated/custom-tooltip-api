package com.stalemated.customtooltips.gui.controller;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.dropdown.AbstractDropdownController;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class ItemOrTagController extends AbstractDropdownController<String> {

    public ItemOrTagController(Option<String> option) {
        super(option, getRegistryValues(), true, true);
    }

    private static List<String> getRegistryValues() {
        List<String> values = new ArrayList<>();
        Registries.ITEM.getIds().stream()
                .map(Identifier::toString)
                .forEach(values::add);

        Registries.ITEM.streamTags()
                .map(tagKey -> "#" + tagKey.id().toString())
                .forEach(values::add);
        
        values.sort(String::compareTo);
        return values;
    }

    @Override
    public String getString() {
        return this.option.pendingValue();
    }

    @Override
    public void setFromString(String value) {
        this.option.requestSet(value);
    }

    @Override
    public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
        return new ItemOrTagControllerElement(this, screen, widgetDimension);
    }
}