package com.stalemated.customtooltips.gui;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.config.TooltipConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import me.shedaniel.math.Color;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.Locale;

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
                .setExpanded(true)
                .setTooltip(
                        Text.translatable("customtooltips.tooltip_edit_screen.custom_text.description"),
                        Text.translatable("customtooltips.tooltip_edit_screen.custom_text.note")
                )
                .setSaveConsumer(newValue -> entry.text = newValue)
                .build());

        category.addEntry(entryBuilder.startEnumSelector(Text.translatable("customtooltips.tooltip_edit_screen.style"), TooltipEntry.TooltipStyle.class, entry.style)
                .setDefaultValue(TooltipEntry.TooltipStyle.SOLID)
                .setEnumNameProvider(e -> Text.translatable("customtooltips.tooltip_edit_screen.style." + e.name().toLowerCase()))
                .setTooltip(Text.translatable("customtooltips.tooltip_edit_screen.style.description"))
                .setSaveConsumer(newValue -> entry.style = newValue)
                .build());

        String[] tempColors = new String[] {
                entry.colors != null && !entry.colors.isEmpty() ? entry.colors.get(0) : "gray",
                entry.colors != null && entry.colors.size() > 1 ? entry.colors.get(1) : ""
        };

        var colorsSubCategory = entryBuilder.startSubCategory(Text.translatable("customtooltips.tooltip_edit_screen.colors"))
                .setTooltip(
                        Text.translatable("customtooltips.tooltip_edit_screen.colors.description"),
                        Text.translatable("customtooltips.tooltip_edit_screen.colors.example")
                )
                .setExpanded(true);

        colorsSubCategory.add(entryBuilder.startStrField(Text.translatable("customtooltips.tooltip_edit_screen.color1"), tempColors[0])
                .setDefaultValue("gray")
                .setSaveConsumer(newValue -> tempColors[0] = newValue)
                .setErrorSupplier(val -> {
                    if (!val.isEmpty() && !isValidColorCode(val)) {
                        return Optional.of(Text.translatable("customtooltips.tooltip_edit_screen.colors.error.invalid", val));
                    }
                    return Optional.empty();
                })
                .build());

        colorsSubCategory.add(entryBuilder.startStrField(Text.translatable("customtooltips.tooltip_edit_screen.color2"), tempColors[1])
                .setDefaultValue("")
                .setSaveConsumer(newValue -> tempColors[1] = newValue)
                .setErrorSupplier(val -> {
                    if (!val.isEmpty() && !isValidColorCode(val)) {
                        return Optional.of(Text.translatable("customtooltips.tooltip_edit_screen.colors.error.invalid", val));
                    }
                    return Optional.empty();
                })
                .build());

        category.addEntry(colorsSubCategory.build());

        category.addEntry(entryBuilder.startEnumSelector(Text.translatable("customtooltips.tooltip_edit_screen.position"), TooltipEntry.TooltipPosition.class, entry.position)
                .setDefaultValue(TooltipEntry.TooltipPosition.BOTTOM)
                .setEnumNameProvider(e -> Text.translatable("customtooltips.tooltip_edit_screen.position." + e.name().toLowerCase()))
                .setTooltip(Text.translatable("customtooltips.tooltip_edit_screen.position.description"))
                .setSaveConsumer(newValue -> entry.position = newValue)
                .build());

        category.addEntry(entryBuilder.startIntField(Text.translatable("customtooltips.tooltip_edit_screen.line_offset"), entry.lineOffset)
                .setDefaultValue(0)
                .setTooltip(Text.translatable("customtooltips.tooltip_edit_screen.line_offset.description"))
                .setSaveConsumer(newValue -> entry.lineOffset = newValue)
                .build());

        var formattingSubCategory = entryBuilder.startSubCategory(Text.translatable("customtooltips.tooltip_edit_screen.formatting"))
                .setTooltip(Text.translatable("customtooltips.tooltip_edit_screen.formatting.description"))
                .setExpanded(false);

        formattingSubCategory.add(entryBuilder.startBooleanToggle(Text.translatable("customtooltips.tooltip_edit_screen.bold"), entry.bold)
                .setDefaultValue(false)
                .setTooltip(Text.translatable("customtooltips.tooltip_edit_screen.bold.description"))
                .setSaveConsumer(newValue -> entry.bold = newValue)
                .build());

        formattingSubCategory.add(entryBuilder.startBooleanToggle(Text.translatable("customtooltips.tooltip_edit_screen.italic"), entry.italic)
                .setDefaultValue(false)
                .setTooltip(Text.translatable("customtooltips.tooltip_edit_screen.italic.description"))
                .setSaveConsumer(newValue -> entry.italic = newValue)
                .build());

        formattingSubCategory.add(entryBuilder.startBooleanToggle(Text.translatable("customtooltips.tooltip_edit_screen.underlined"), entry.underlined)
                .setDefaultValue(false)
                .setTooltip(Text.translatable("customtooltips.tooltip_edit_screen.underlined.description"))
                .setSaveConsumer(newValue -> entry.underlined = newValue)
                .build());

        formattingSubCategory.add(entryBuilder.startBooleanToggle(Text.translatable("customtooltips.tooltip_edit_screen.strikethrough"), entry.strikethrough)
                .setDefaultValue(false)
                .setTooltip(Text.translatable("customtooltips.tooltip_edit_screen.strikethrough.description"))
                .setSaveConsumer(newValue -> entry.strikethrough = newValue)
                .build());

        formattingSubCategory.add(entryBuilder.startBooleanToggle(Text.translatable("customtooltips.tooltip_edit_screen.obfuscated"), entry.obfuscated)
                .setDefaultValue(false)
                .setTooltip(Text.translatable("customtooltips.tooltip_edit_screen.obfuscated.description"))
                .setSaveConsumer(newValue -> entry.obfuscated = newValue)
                .build());

        category.addEntry(formattingSubCategory.build());

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

        category.addEntry(entryBuilder.startIntField(Text.translatable("customtooltips.tooltip_edit_screen.animation_offset"), entry.animation_offset)
                .setDefaultValue(0)
                .setTooltip(Text.translatable("customtooltips.tooltip_edit_screen.animation_offset.description"))
                .setSaveConsumer(newValue -> entry.animation_offset = newValue)
                .build());

        category.addEntry(entryBuilder.startLongField(Text.translatable("customtooltips.tooltip_edit_screen.tickrate"), entry.tickrate)
                .setDefaultValue(1L)
                .setMin(1)
                .setTooltip(Text.translatable("customtooltips.tooltip_edit_screen.tickrate.description"))
                .setSaveConsumer(newValue -> entry.tickrate = newValue)
                .build());

        builder.setSavingRunnable(() -> {
            entry.colors = new ArrayList<>();
            if (tempColors[0] != null && !tempColors[0].isBlank()) entry.colors.add(tempColors[0].trim());
            if (tempColors[1] != null && !tempColors[1].isBlank()) entry.colors.add(tempColors[1].trim());
            if (entry.colors.isEmpty()) entry.colors.add("gray");

            if (isNew) {
                TooltipConfig config = AutoConfig.getConfigHolder(TooltipConfig.class).getConfig();
                config.entries.add(entry);
            } else {
                entry.invalidateCaches();
            }
            AutoConfig.getConfigHolder(TooltipConfig.class).save();
            if (parent instanceof TooltipListScreen) {
                ((TooltipListScreen) parent).listWidget.updateEntries(((TooltipListScreen) parent).searchBox.getText());
            }
        });

        return builder.build();
    }

    private static boolean isValidColorCode(String color) {
        if (color == null || color.isEmpty()) return false;

        if (color.matches("^(#|0x|x|0X|X)?([0-9a-fA-F]{6})$")) return true;
        if (color.matches("^(&)([0-9a-fA-F])$")) return true;

        return Formatting.byName(color.toUpperCase(Locale.ROOT)) != null;
    }
}