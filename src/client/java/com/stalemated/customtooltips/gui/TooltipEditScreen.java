package com.stalemated.customtooltips.gui;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.core.TooltipEntryUpdater;
import com.stalemated.customtooltips.gui.controller.builder.SimpleEnumDropdownControllerBuilder;
import com.stalemated.customtooltips.gui.controller.builder.SimpleStringDropdownControllerBuilder;
import com.stalemated.customtooltips.gui.controller.builder.AdvancedColorControllerBuilder;
import com.stalemated.customtooltips.gui.controller.builder.ItemOrTagControllerBuilder;

import dev.isxander.yacl3.api.ListOption;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.api.controller.LongFieldControllerBuilder;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;

public class TooltipEditScreen {

    public static Screen create(Screen parent, TooltipEntry entry, boolean isNew) {
        final WeakReference<Boolean> isNewRef = new WeakReference<>(isNew);

        String rawColor1 = entry.colors != null && !entry.colors.isEmpty() ? entry.colors.get(0) : "white";
        String rawColor2 = entry.colors != null && entry.colors.size() > 1 ? entry.colors.get(1) : "white";
        String[] boundColors = new String[] { rawColor1, rawColor2 };

        return YetAnotherConfigLib.createBuilder()
                .title(Text.translatable("customtooltips.tooltip_edit_screen.title"))
                .save(() -> {
                    Boolean isNewEntry = isNewRef.get();
                    if (isNewEntry != null) {
                        TooltipEntryUpdater.updateAndSave(entry, boundColors, isNewEntry, parent);
                        if (isNewEntry) {
                            // Prevent re-adding on subsequent saves within the same screen session
                            isNewRef.clear();
                        }
                    } else {
                        TooltipEntryUpdater.updateAndSave(entry, boundColors, false, parent);
                    }
                })
                .category(ConfigCategory.createBuilder()
                        .name(Text.translatable("customtooltips.tooltip_edit_screen.title"))
                        .group(createTargetGroup(entry))
                        .group(createCustomTextGroup(entry))
                        .group(createStyleAndColorsGroup(entry, boundColors))
                        .group(createPositionAndAnimationGroup(entry))
                        .group(createFormattingGroup(entry))
                        .group(createConditionsGroup(entry))
                        .build())
                .build()
                .generateScreen(parent);
    }

    private static OptionGroup createTargetGroup(TooltipEntry entry) {
        var target = Option.<String>createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.target_id"))
                .description(OptionDescription.of(Text.translatable("customtooltips.tooltip_edit_screen.target.description")))
                .binding("", () -> entry.target, val -> entry.target = val)
                .controller(ItemOrTagControllerBuilder::create)
                .build();

        return OptionGroup.createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.category.target"))
                .option(target)
                .build();
    }

    private static ListOption<String> createCustomTextGroup(TooltipEntry entry) {
        return ListOption.<String>createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.custom_text"))
                .description(OptionDescription.of(
                        Text.translatable("customtooltips.tooltip_edit_screen.custom_text.description"),
                        Text.translatable("customtooltips.tooltip_edit_screen.custom_text.note")
                ))
                .binding(new ArrayList<>(Arrays.asList("Default text")), () -> new ArrayList<>(entry.text), val -> entry.text = val)
                .controller(StringControllerBuilder::create)
                .initial("")
                .build();
    }

    private static OptionGroup createStyleAndColorsGroup(TooltipEntry entry, String[] boundColors) {
        var style = Option.<TooltipEntry.TooltipStyle>createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.style"))
                .description(OptionDescription.of(Text.translatable("customtooltips.tooltip_edit_screen.style.description")))
                .binding(TooltipEntry.TooltipStyle.SOLID, () -> entry.style, val -> entry.style = val)
                .controller(opt -> SimpleEnumDropdownControllerBuilder.create(opt)
                        .formatValue(styleFormat -> Text.translatable("customtooltips.tooltip_edit_screen.style." + styleFormat.name().toLowerCase())))
                .build();

        var color1 = Option.<String>createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.colors.primary_color"))
                .description(OptionDescription.of(
                        Text.translatable("customtooltips.tooltip_edit_screen.colors.primary_color.description"),
                        Text.translatable("customtooltips.tooltip_edit_screen.colors.color_override.description")
                ))
                .binding("white", () -> boundColors[0], val -> boundColors[0] = val.trim())
                .controller(AdvancedColorControllerBuilder::create)
                .build();

        var color2 = Option.<String>createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.colors.secondary_color"))
                .description(OptionDescription.of(
                        Text.translatable("customtooltips.tooltip_edit_screen.colors.secondary_color.description"),
                        Text.translatable("customtooltips.tooltip_edit_screen.colors.color_override.description")
                ))
                .binding("white", () -> boundColors[1], val -> boundColors[1] = val.trim())
                .controller(AdvancedColorControllerBuilder::create)
                .build();

        return OptionGroup.createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.category.style_colors"))
                .option(style)
                .option(color1)
                .option(color2)
                .build();
    }

    private static OptionGroup createPositionAndAnimationGroup(TooltipEntry entry) {
        var position = Option.<TooltipEntry.TooltipPosition>createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.position"))
                .description(OptionDescription.of(Text.translatable("customtooltips.tooltip_edit_screen.position.description")))
                .binding(TooltipEntry.TooltipPosition.BOTTOM, () -> entry.position, val -> entry.position = val)
                .controller(opt -> SimpleEnumDropdownControllerBuilder.create(opt)
                        .formatValue(pos -> Text.translatable("customtooltips.tooltip_edit_screen.position." + pos.name().toLowerCase())))
                .build();

        var offset = Option.<Integer>createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.line_offset"))
                .description(OptionDescription.of(Text.translatable("customtooltips.tooltip_edit_screen.line_offset.description")))
                .binding(0, () -> entry.lineOffset, val -> entry.lineOffset = val)
                .controller(IntegerFieldControllerBuilder::create)
                .build();

        var animOffset = Option.<Integer>createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.animation_offset"))
                .description(OptionDescription.of(Text.translatable("customtooltips.tooltip_edit_screen.animation_offset.description")))
                .binding(0, () -> entry.animation_offset, val -> entry.animation_offset = val)
                .controller(IntegerFieldControllerBuilder::create)
                .build();

        var rate = Option.<Long>createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.tickrate"))
                .description(OptionDescription.of(Text.translatable("customtooltips.tooltip_edit_screen.tickrate.description")))
                .binding(1L, () -> entry.tickrate, val -> entry.tickrate = val)
                .controller(LongFieldControllerBuilder::create)
                .build();

        return OptionGroup.createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.category.position_animation"))
                .option(position)
                .option(offset)
                .option(animOffset)
                .option(rate)
                .build();
    }

    private static OptionGroup createFormattingGroup(TooltipEntry entry) {
        var bold = Option.<Boolean>createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.bold"))
                .description(OptionDescription.of(Text.translatable("customtooltips.tooltip_edit_screen.bold.description")))
                .binding(false, () -> entry.bold, val -> entry.bold = val)
                .controller(TickBoxControllerBuilder::create)
                .build();

        var italic = Option.<Boolean>createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.italic"))
                .description(OptionDescription.of(Text.translatable("customtooltips.tooltip_edit_screen.italic.description")))
                .binding(false, () -> entry.italic, val -> entry.italic = val)
                .controller(TickBoxControllerBuilder::create)
                .build();

        var underlined = Option.<Boolean>createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.underlined"))
                .description(OptionDescription.of(Text.translatable("customtooltips.tooltip_edit_screen.underlined.description")))
                .binding(false, () -> entry.underlined, val -> entry.underlined = val)
                .controller(TickBoxControllerBuilder::create)
                .build();

        var strikethrough = Option.<Boolean>createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.strikethrough"))
                .description(OptionDescription.of(Text.translatable("customtooltips.tooltip_edit_screen.strikethrough.description")))
                .binding(false, () -> entry.strikethrough, val -> entry.strikethrough = val)
                .controller(TickBoxControllerBuilder::create)
                .build();

        var obfuscated = Option.<Boolean>createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.obfuscated"))
                .description(OptionDescription.of(Text.translatable("customtooltips.tooltip_edit_screen.obfuscated.description")))
                .binding(false, () -> entry.obfuscated, val -> entry.obfuscated = val)
                .controller(TickBoxControllerBuilder::create)
                .build();

        var fontOption = Option.<String>createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.font"))
                .description(OptionDescription.of(Text.translatable("customtooltips.tooltip_edit_screen.font.description")))
                .binding("minecraft:default", () -> entry.font, val -> entry.font = val)
                .controller(opt -> SimpleStringDropdownControllerBuilder.create(opt)
                        .values(com.stalemated.customtooltips.util.CustomFontManager.availableFonts)
                        .formatValue(Text::literal)
                )
                .build();

        return OptionGroup.createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.category.formatting"))
                .collapsed(true)
                .option(fontOption)
                .option(bold)
                .option(italic)
                .option(underlined)
                .option(strikethrough)
                .option(obfuscated)
                .build();
    }

    private static OptionGroup createConditionsGroup(TooltipEntry entry) {
        var requireShift = Option.<Boolean>createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.require_shift"))
                .description(OptionDescription.of(Text.translatable("customtooltips.tooltip_edit_screen.require_shift.description")))
                .binding(false, () -> entry.require_shift, val -> entry.require_shift = val)
                .controller(TickBoxControllerBuilder::create)
                .build();

        var emptyLineBefore = Option.<Boolean>createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.empty_line_before"))
                .description(OptionDescription.of(Text.translatable("customtooltips.tooltip_edit_screen.empty_line_before.description")))
                .binding(false, () -> entry.empty_line_before, val -> entry.empty_line_before = val)
                .controller(TickBoxControllerBuilder::create)
                .build();

        return OptionGroup.createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.category.conditions"))
                .option(requireShift)
                .option(emptyLineBefore)
                .build();
    }
}