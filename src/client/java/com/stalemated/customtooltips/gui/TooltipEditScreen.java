package com.stalemated.customtooltips.gui;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.config.TooltipConfig;
import com.stalemated.customtooltips.ConfigManager;
import com.stalemated.customtooltips.gui.controller.builder.SimpleEnumDropdownControllerBuilder;
import com.stalemated.customtooltips.gui.controller.builder.SimpleStringDropdownControllerBuilder;
import com.stalemated.customtooltips.gui.controller.builder.AdvancedColorControllerBuilder;
import com.stalemated.customtooltips.util.ToastManager;
import com.stalemated.customtooltips.util.CustomFontManager;
import com.stalemated.customtooltips.util.ColorUtils;

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

import java.util.ArrayList;
import java.util.Arrays;

public class TooltipEditScreen {

    public static Screen create(Screen parent, TooltipEntry entry, boolean isNew) {
        
        final boolean[] isNewRef = { isNew };

        // Color handling

        String rawColor1 = entry.colors != null && !entry.colors.isEmpty() ? entry.colors.get(0) : "white";
        String rawColor2 = entry.colors != null && entry.colors.size() > 1 ? entry.colors.get(1) : "white";

        String[] boundColors = new String[] { rawColor1, rawColor2 };

        //region Option builders
        var target = Option.<String>createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.target_id"))
                .description(OptionDescription.of(Text.translatable("customtooltips.tooltip_edit_screen.target.description")))
                .binding("", () -> entry.target, val -> entry.target = val)
                .controller(StringControllerBuilder::create)
                .build();

        var customText = ListOption.<String>createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.custom_text"))
                .description(OptionDescription.of(
                        Text.translatable("customtooltips.tooltip_edit_screen.custom_text.description"),
                        Text.translatable("customtooltips.tooltip_edit_screen.custom_text.note")
                ))
                .binding(new ArrayList<>(Arrays.asList("Default text")), () -> entry.text, val -> entry.text = val)
                .controller(StringControllerBuilder::create)
                .initial("")
                .build();

        var animationType = Option.<TooltipEntry.TooltipStyle>createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.style"))
                .description(OptionDescription.of(Text.translatable("customtooltips.tooltip_edit_screen.style.description")))
                .binding(TooltipEntry.TooltipStyle.SOLID, () -> entry.style, val -> entry.style = val)
                .controller(opt -> SimpleEnumDropdownControllerBuilder.create(opt)
                        .formatValue(style -> Text.translatable("customtooltips.tooltip_edit_screen.style." + style.name().toLowerCase())))
                .build();

        var textColor1 = Option.<String>createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.colors.primary_color"))
                .description(OptionDescription.of(
                        Text.translatable("customtooltips.tooltip_edit_screen.colors.primary_color.description"),
                        Text.translatable("customtooltips.tooltip_edit_screen.colors.color_override.description")
                ))
                .binding("white", () -> boundColors[0], val -> boundColors[0] = val)
                .controller(AdvancedColorControllerBuilder::create)
                .build();

        var textColor2 = Option.<String>createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.colors.secondary_color"))
                .description(OptionDescription.of(
                        Text.translatable("customtooltips.tooltip_edit_screen.colors.secondary_color.description"),
                        Text.translatable("customtooltips.tooltip_edit_screen.colors.color_override.description")
                ))
                .binding("white", () -> boundColors[1], val -> boundColors[1] = val)
                .controller(AdvancedColorControllerBuilder::create)
                .build();

        var tooltipPosition = Option.<TooltipEntry.TooltipPosition>createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.position"))
                .description(OptionDescription.of(Text.translatable("customtooltips.tooltip_edit_screen.position.description")))
                .binding(TooltipEntry.TooltipPosition.BOTTOM, () -> entry.position, val -> entry.position = val)
                .controller(opt -> SimpleEnumDropdownControllerBuilder.create(opt)
                        .formatValue(position -> Text.translatable("customtooltips.tooltip_edit_screen.position." + position.name().toLowerCase())))
                .build();

        var lineOffset = Option.<Integer>createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.line_offset"))
                .description(OptionDescription.of(Text.translatable("customtooltips.tooltip_edit_screen.line_offset.description")))
                .binding(0, () -> entry.lineOffset, val -> entry.lineOffset = val)
                .controller(IntegerFieldControllerBuilder::create)
                .build();

        var animationOffset = Option.<Integer>createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.animation_offset"))
                .description(OptionDescription.of(Text.translatable("customtooltips.tooltip_edit_screen.animation_offset.description")))
                .binding(0, () -> entry.animation_offset, val -> entry.animation_offset = val)
                .controller(IntegerFieldControllerBuilder::create)
                .build();

        var tickrate = Option.<Long>createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.tickrate"))
                .description(OptionDescription.of(Text.translatable("customtooltips.tooltip_edit_screen.tickrate.description")))
                .binding(1L, () -> entry.tickrate, val -> entry.tickrate = val)
                .controller(LongFieldControllerBuilder::create)
                .build();

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

        var fontOption = Option.<String>createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.font"))
                .description(OptionDescription.of(Text.translatable("customtooltips.tooltip_edit_screen.font.description")))
                .binding("minecraft:default", () -> entry.font, val -> entry.font = val)
                .controller(opt -> SimpleStringDropdownControllerBuilder.create(opt)
                        .values(CustomFontManager.availableFonts)
                        .formatValue(Text::literal)
                )
                .build();
        //endregion

        return YetAnotherConfigLib.createBuilder()
                .title(Text.translatable("customtooltips.tooltip_edit_screen.title"))
                .save(() -> {
                    entry.colors = new ArrayList<>();
                    boolean hasError = false;

                    String finalColor1 = boundColors[0].trim();
                    String finalColor2 = boundColors[1].trim();

                    if (!ColorUtils.isValidColorCode(finalColor1)) { hasError = true; finalColor1 = "white"; }
                    if (!ColorUtils.isValidColorCode(finalColor2)) { hasError = true; finalColor2 = "white"; }

                    entry.colors.add(finalColor1);
                    entry.colors.add(finalColor2);

                    if (hasError) {
                        ToastManager.showInvalidColorToast();
                    }

                if (isNewRef[0]) {
                        TooltipConfig config = ConfigManager.getConfig();
                        config.entries.add(entry);
                    isNewRef[0] = false;
                    } else {
                        entry.invalidateCaches();
                    }
                    ConfigManager.save();
                    if (parent instanceof TooltipListScreen) {
                        ((TooltipListScreen) parent).listWidget.updateEntries(((TooltipListScreen) parent).searchBox.getText());
                    }
                })
                .category(ConfigCategory.createBuilder()
                        .name(Text.translatable("customtooltips.tooltip_edit_screen.title"))
                        .group(OptionGroup.createBuilder()
                                .name(Text.translatable("customtooltips.tooltip_edit_screen.category.target"))
                                .option(target)
                                .build())
                        .group(customText)
                        .group(OptionGroup.createBuilder()
                                .name(Text.translatable("customtooltips.tooltip_edit_screen.category.style_colors"))
                                .option(animationType)
                                .option(textColor1)
                                .option(textColor2)
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(Text.translatable("customtooltips.tooltip_edit_screen.category.position_animation"))
                                .option(tooltipPosition)
                                .option(lineOffset)
                                .option(animationOffset)
                                .option(tickrate)
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(Text.translatable("customtooltips.tooltip_edit_screen.category.formatting"))
                                .collapsed(true)
                                .option(fontOption)
                                .option(bold)
                                .option(italic)
                                .option(underlined)
                                .option(strikethrough)
                                .option(obfuscated)
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(Text.translatable("customtooltips.tooltip_edit_screen.category.conditions"))
                                .option(requireShift)
                                .option(emptyLineBefore)
                                .build())
                        .build())
                .build()
                .generateScreen(parent);
    }
}