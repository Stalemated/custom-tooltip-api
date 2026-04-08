package com.stalemated.customtooltips.gui;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.config.TooltipConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Arrays;

public class TooltipEditScreen {

    public static Screen create(Screen parent, TooltipEntry entry, boolean isNew) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("customtooltips.tooltip_edit_screen.title"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory category = builder.getOrCreateCategory(Text.literal("General"));

        category.addEntry(entryBuilder.startStrField(Text.translatable("customtooltips.tooltip_edit_screen.target"), entry.target)
                .setDefaultValue("")
                .setTooltip(Text.translatable("customtooltips.tooltip_edit_screen.target.description"))
                .setSaveConsumer(newValue -> entry.target = newValue)
                .build());

        category.addEntry(entryBuilder.startStrList(Text.translatable("customtooltips.tooltip_edit_screen.custom_text"), entry.text)
                .setDefaultValue(new ArrayList<>(Arrays.asList("Default text")))
                .setTooltip(Text.translatable("customtooltips.tooltip_edit_screen.custom_text.description"))
                .setSaveConsumer(newValue -> entry.text = newValue)
                .build());

        category.addEntry(entryBuilder.startEnumSelector(Text.translatable("customtooltips.tooltip_edit_screen.style"), TooltipEntry.TooltipStyle.class, entry.style)
                .setDefaultValue(TooltipEntry.TooltipStyle.SOLID)
                .setTooltip(Text.translatable("customtooltips.tooltip_edit_screen.style.description"))
                .setSaveConsumer(newValue -> entry.style = newValue)
                .build());

        category.addEntry(entryBuilder.startStrList(Text.translatable("customtooltips.tooltip_edit_screen.colors"), entry.colors)
                .setDefaultValue(new ArrayList<>(Arrays.asList("gray")))
                .setTooltip(Text.translatable("customtooltips.tooltip_edit_screen.colors.description"))
                .setSaveConsumer(newValue -> entry.colors = newValue)
                .build());

        category.addEntry(entryBuilder.startEnumSelector(Text.translatable("customtooltips.tooltip_edit_screen.position"), TooltipEntry.TooltipPosition.class, entry.position)
                .setDefaultValue(TooltipEntry.TooltipPosition.BOTTOM)
                .setTooltip(Text.translatable("customtooltips.tooltip_edit_screen.position.description"))
                .setSaveConsumer(newValue -> entry.position = newValue)
                .build());

        category.addEntry(entryBuilder.startBooleanToggle(Text.translatable("customtooltips.tooltip_edit_screen.bold"), entry.bold)
                .setDefaultValue(false)
                .setTooltip(Text.translatable("customtooltips.tooltip_edit_screen.bold.description"))
                .setSaveConsumer(newValue -> entry.bold = newValue)
                .build());

        category.addEntry(entryBuilder.startBooleanToggle(Text.translatable("customtooltips.tooltip_edit_screen.italic"), entry.italic)
                .setDefaultValue(false)
                .setTooltip(Text.translatable("customtooltips.tooltip_edit_screen.italic.description"))
                .setSaveConsumer(newValue -> entry.italic = newValue)
                .build());

        category.addEntry(entryBuilder.startBooleanToggle(Text.translatable("customtooltips.tooltip_edit_screen.underlined"), entry.underlined)
                .setDefaultValue(false)
                .setTooltip(Text.translatable("customtooltips.tooltip_edit_screen.underlined.description"))
                .setSaveConsumer(newValue -> entry.underlined = newValue)
                .build());

        category.addEntry(entryBuilder.startBooleanToggle(Text.translatable("customtooltips.tooltip_edit_screen.strikethrough"), entry.strikethrough)
                .setDefaultValue(false)
                .setTooltip(Text.translatable("customtooltips.tooltip_edit_screen.strikethrough.description"))
                .setSaveConsumer(newValue -> entry.strikethrough = newValue)
                .build());

        category.addEntry(entryBuilder.startBooleanToggle(Text.translatable("customtooltips.tooltip_edit_screen.obfuscated"), entry.obfuscated)
                .setDefaultValue(false)
                .setTooltip(Text.translatable("customtooltips.tooltip_edit_screen.obfuscated.description"))
                .setSaveConsumer(newValue -> entry.obfuscated = newValue)
                .build());

        category.addEntry(entryBuilder.startBooleanToggle(Text.translatable("customtooltips.tooltip_edit_screen.require_shift"), entry.require_shift)
                .setDefaultValue(false)
                .setTooltip(Text.translatable("customtooltips.tooltip_edit_screen.require_shift.description"))
                .setSaveConsumer(newValue -> entry.require_shift = newValue)
                .build());

        category.addEntry(entryBuilder.startBooleanToggle(Text.translatable("customtooltips.tooltip_edit_screen.empty_line_before"), entry.empty_line_before)
                .setDefaultValue(false)
                .setTooltip(Text.translatable("customtooltips.tooltip_edit_screen.empty_line_before.description"))
                .setSaveConsumer(newValue -> entry.empty_line_before = newValue)
                .build());

        category.addEntry(entryBuilder.startIntField(Text.translatable("customtooltips.tooltip_edit_screen.offset"), entry.offset)
                .setDefaultValue(0)
                .setTooltip(Text.translatable("customtooltips.tooltip_edit_screen.offset.description"))
                .setSaveConsumer(newValue -> entry.offset = newValue)
                .build());

        category.addEntry(entryBuilder.startLongField(Text.translatable("customtooltips.tooltip_edit_screen.tickrate"), entry.tickrate)
                .setDefaultValue(1L)
                .setTooltip(Text.translatable("customtooltips.tooltip_edit_screen.tickrate.description"))
                .setSaveConsumer(newValue -> entry.tickrate = newValue)
                .build());

        builder.setSavingRunnable(() -> {
            if (isNew) {
                TooltipConfig config = AutoConfig.getConfigHolder(TooltipConfig.class).getConfig();
                config.entries.add(entry);
            } else {
                entry.invalidateCaches();
            }
            AutoConfig.getConfigHolder(TooltipConfig.class).save();
            if (parent instanceof TooltipListScreen) {
                ((TooltipListScreen) parent).listWidget.updateEntries();
            }
        });

        return builder.build();
    }
}