package com.stalemated.customtooltips.gui;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.core.TooltipEntryUpdater;
import com.stalemated.customtooltips.core.TooltipOpacity;
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
import dev.isxander.yacl3.api.controller.*;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TooltipEditScreen {

    public static TooltipEntry previewEntry = null;

    static {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (previewEntry != null && screen.getTitle().getString().contains("Edit Tooltip")) {
                ScreenEvents.afterRender(screen).register((screen1, context, mouseX, mouseY, tickDelta) -> {
                    if (Screen.hasControlDown()) {
                        List<Text> previewLines = new ArrayList<>(previewEntry.getTextComponents(ItemStack.EMPTY));

                        TooltipOpacity.setCurrentOpacity(previewEntry.opacity);
                        TooltipOpacity.setCurrentEntry(previewEntry);
                        context.drawTooltip(client.textRenderer, previewLines, mouseX, mouseY);
                        TooltipOpacity.setCurrentOpacity(-1);
                        TooltipOpacity.setCurrentEntry(null);
                    }
                });
            }
        });
    }

    public static Screen create(Screen parent, TooltipEntry entry, boolean isNew) {
        previewEntry = entry.copy();

        final WeakReference<Boolean> isNewRef = new WeakReference<>(isNew);

        String rawColor1 = entry.colors != null && !entry.colors.isEmpty() ? entry.colors.get(0) : "white";
        String rawColor2 = entry.colors != null && entry.colors.size() > 1 ? entry.colors.get(1) : "white";
        String[] boundColors = new String[] { rawColor1, rawColor2 };

        String rawBorderColor1 = entry.borderColors != null && !entry.borderColors.isEmpty() ? entry.borderColors.get(0) : "";
        String rawBorderColor2 = entry.borderColors != null && entry.borderColors.size() > 1 ? entry.borderColors.get(1) : "";
        String[] boundBorderColors = new String[] { rawBorderColor1, rawBorderColor2 };

        return YetAnotherConfigLib.createBuilder()
                .title(Text.translatable("customtooltips.tooltip_edit_screen.title"))
                .save(() -> {
                    Boolean isNewEntry = isNewRef.get();
                    if (isNewEntry != null) {
                        TooltipEntryUpdater.updateAndSave(entry, boundColors, boundBorderColors, isNewEntry, parent);
                        if (isNewEntry) {
                            // Prevent re-adding on subsequent saves within the same screen session
                            isNewRef.clear();
                        }
                    } else {
                        TooltipEntryUpdater.updateAndSave(entry, boundColors, boundBorderColors, false, parent);
                    }
                })
                .category(ConfigCategory.createBuilder()
                        .name(Text.translatable("customtooltips.tooltip_edit_screen.title"))
                        .group(createTargetGroup(entry))
                        .group(createCustomTextGroup(entry))
                        .group(createStyleAndColorsGroup(entry, boundColors, boundBorderColors))
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
        var customText = ListOption.<String>createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.custom_text"))
                .description(OptionDescription.of(
                        Text.translatable("customtooltips.tooltip_edit_screen.custom_text.description"),
                        Text.translatable("customtooltips.tooltip_edit_screen.custom_text.note")
                ))
                .binding(new ArrayList<>(Arrays.asList("Default text")), () -> new ArrayList<>(entry.text), val -> entry.text = val)
                .controller(StringControllerBuilder::create)
                .initial("")
                .build();

        customText.addEventListener((opt, event) -> {
            if (previewEntry != null) {
                previewEntry.text = new ArrayList<>(opt.pendingValue());
                previewEntry.invalidateCaches();
            }
        });

        return customText;
    }

    private static OptionGroup createStyleAndColorsGroup(TooltipEntry entry, String[] boundColors, String[] boundBorderColors) {
        var style = Option.<TooltipEntry.TooltipStyle>createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.style"))
                .description(OptionDescription.of(Text.translatable("customtooltips.tooltip_edit_screen.style.description")))
                .binding(TooltipEntry.TooltipStyle.SOLID, () -> entry.style, val -> entry.style = val)
                .controller(opt -> SimpleEnumDropdownControllerBuilder.create(opt)
                        .formatValue(styleFormat -> Text.translatable("customtooltips.tooltip_edit_screen.style." + styleFormat.name().toLowerCase())))
                .build();
        style.addEventListener((opt, event) -> {
            if (previewEntry != null) {
                previewEntry.style = opt.pendingValue();
                previewEntry.invalidateCaches();
            }
        });

        var color1 = Option.<String>createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.colors.primary_color"))
                .description(OptionDescription.of(
                        Text.translatable("customtooltips.tooltip_edit_screen.colors.primary_color.description"),
                        Text.translatable("customtooltips.tooltip_edit_screen.colors.color_override.description")
                ))
                .binding("white", () -> boundColors[0], val -> boundColors[0] = val.trim())
                .controller(AdvancedColorControllerBuilder::create)
                .build();
        color1.addEventListener((opt, event) -> {
            if (previewEntry != null) {
                if (previewEntry.colors.isEmpty()) previewEntry.colors.add(opt.pendingValue().trim());
                else previewEntry.colors.set(0, opt.pendingValue().trim());
                previewEntry.invalidateCaches();
            }
        });

        var color2 = Option.<String>createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.colors.secondary_color"))
                .description(OptionDescription.of(
                        Text.translatable("customtooltips.tooltip_edit_screen.colors.secondary_color.description"),
                        Text.translatable("customtooltips.tooltip_edit_screen.colors.color_override.description")
                ))
                .binding("white", () -> boundColors[1], val -> boundColors[1] = val.trim())
                .controller(AdvancedColorControllerBuilder::create)
                .build();
        color2.addEventListener((opt, event) -> {
            if (previewEntry != null) {
                while (previewEntry.colors.size() < 2) previewEntry.colors.add("white");
                previewEntry.colors.set(1, opt.pendingValue().trim());
                previewEntry.invalidateCaches();
            }
        });

        var borderColor1 = Option.<String>createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.colors.border_top_color"))
                .description(OptionDescription.of(
                        Text.translatable("customtooltips.tooltip_edit_screen.colors.border_top_color.description")
                ))
                .binding("#505000FF", () -> boundBorderColors[0], val -> boundBorderColors[0] = val.trim())
                .controller(opt -> AdvancedColorControllerBuilder.create(opt)
                        .alpha(true))
                .build();
        borderColor1.addEventListener((opt, event) -> {
            if (previewEntry != null) {
                if (previewEntry.borderColors.isEmpty()) previewEntry.borderColors.add(opt.pendingValue().trim());
                else previewEntry.borderColors.set(0, opt.pendingValue().trim());
                previewEntry.invalidateCaches();
            }
        });

        var borderColor2 = Option.<String>createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.colors.border_bottom_color"))
                .description(OptionDescription.of(
                        Text.translatable("customtooltips.tooltip_edit_screen.colors.border_bottom_color.description")
                ))
                .binding("#5028007F", () -> boundBorderColors[1], val -> boundBorderColors[1] = val.trim())
                .controller(opt -> AdvancedColorControllerBuilder.create(opt)
                        .alpha(true))
                .build();
        borderColor2.addEventListener((opt, event) -> {
            if (previewEntry != null) {
                while (previewEntry.borderColors.size() < 2) previewEntry.borderColors.add("#5028007F");
                previewEntry.borderColors.set(1, opt.pendingValue().trim());
                previewEntry.invalidateCaches();
            }
        });

        var tooltipOpacity = Option.<Integer>createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.opacity"))
                .description(OptionDescription.of(Text.translatable("customtooltips.tooltip_edit_screen.opacity.description")))
                .binding(TooltipEntry.DEFAULT_OPACITY, () -> entry.opacity, val -> entry.opacity = val)
                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                        .range(0, 255)
                        .step(1)
                )
                .build();
        tooltipOpacity.addEventListener((opt, event) -> {
            if (previewEntry != null) {
                previewEntry.opacity = opt.pendingValue();
                previewEntry.invalidateCaches();
            }
        });

        return OptionGroup.createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.category.style_colors"))
                .option(style)
                .option(color1)
                .option(color2)
                .option(tooltipOpacity)
                .option(borderColor1)
                .option(borderColor2)
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
                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                        .range(-100, 100)
                        .step(1)
                )
                .build();
        animOffset.addEventListener((opt, event) -> {
            if (previewEntry != null) {
                previewEntry.animation_offset = opt.pendingValue();
                previewEntry.invalidateCaches();
            }
        });

        var rate = Option.<Integer>createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.tickrate"))
                .description(OptionDescription.of(Text.translatable("customtooltips.tooltip_edit_screen.tickrate.description")))
                .binding(100, () -> entry.tickrate, val -> entry.tickrate = val)
                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                        .range(1, 500)
                        .step(1)
                )
                .build();
        rate.addEventListener((opt, event) -> {
            if (previewEntry != null) {
                previewEntry.tickrate = opt.pendingValue();
                previewEntry.invalidateCaches();
            }
        });

        var reverseAnim = Option.<Boolean>createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.reverse_animation"))
                .description(OptionDescription.of(Text.translatable("customtooltips.tooltip_edit_screen.reverse_animation.description")))
                .binding(false, () -> entry.reverse_animation, val -> entry.reverse_animation = val)
                .controller(TickBoxControllerBuilder::create)
                .build();
        reverseAnim.addEventListener((opt, event) -> {
            if (previewEntry != null) {
                previewEntry.reverse_animation = opt.pendingValue();
                previewEntry.invalidateCaches();
            }
        });

        return OptionGroup.createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.category.position_animation"))
                .option(position)
                .option(offset)
                .option(rate)
                .option(animOffset)
                .option(reverseAnim)
                .build();
    }

    private static OptionGroup createFormattingGroup(TooltipEntry entry) {
        var bold = Option.<Boolean>createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.bold"))
                .description(OptionDescription.of(Text.translatable("customtooltips.tooltip_edit_screen.bold.description")))
                .binding(false, () -> entry.bold, val -> entry.bold = val)
                .controller(TickBoxControllerBuilder::create)
                .build();
        bold.addEventListener((opt, event) -> {
            if (previewEntry != null) {
                previewEntry.bold = opt.pendingValue();
                previewEntry.invalidateCaches();
            }
        });

        var italic = Option.<Boolean>createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.italic"))
                .description(OptionDescription.of(Text.translatable("customtooltips.tooltip_edit_screen.italic.description")))
                .binding(false, () -> entry.italic, val -> entry.italic = val)
                .controller(TickBoxControllerBuilder::create)
                .build();
        italic.addEventListener((opt, event) -> {
            if (previewEntry != null) {
                previewEntry.italic = opt.pendingValue();
                previewEntry.invalidateCaches();
            }
        });

        var underlined = Option.<Boolean>createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.underlined"))
                .description(OptionDescription.of(Text.translatable("customtooltips.tooltip_edit_screen.underlined.description")))
                .binding(false, () -> entry.underlined, val -> entry.underlined = val)
                .controller(TickBoxControllerBuilder::create)
                .build();
        underlined.addEventListener((opt, event) -> {
            if (previewEntry != null) {
                previewEntry.underlined = opt.pendingValue();
                previewEntry.invalidateCaches();
            }
        });

        var strikethrough = Option.<Boolean>createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.strikethrough"))
                .description(OptionDescription.of(Text.translatable("customtooltips.tooltip_edit_screen.strikethrough.description")))
                .binding(false, () -> entry.strikethrough, val -> entry.strikethrough = val)
                .controller(TickBoxControllerBuilder::create)
                .build();
        strikethrough.addEventListener((opt, event) -> {
            if (previewEntry != null) {
                previewEntry.strikethrough = opt.pendingValue();
                previewEntry.invalidateCaches();
            }
        });

        var obfuscated = Option.<Boolean>createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.obfuscated"))
                .description(OptionDescription.of(Text.translatable("customtooltips.tooltip_edit_screen.obfuscated.description")))
                .binding(false, () -> entry.obfuscated, val -> entry.obfuscated = val)
                .controller(TickBoxControllerBuilder::create)
                .build();
        obfuscated.addEventListener((opt, event) -> {
            if (previewEntry != null) {
                previewEntry.obfuscated = opt.pendingValue();
                previewEntry.invalidateCaches();
            }
        });

        var fontOption = Option.<String>createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.font"))
                .description(OptionDescription.of(Text.translatable("customtooltips.tooltip_edit_screen.font.description")))
                .binding("minecraft:default", () -> entry.font, val -> entry.font = val)
                .controller(opt -> SimpleStringDropdownControllerBuilder.create(opt)
                        .values(com.stalemated.customtooltips.util.CustomFontManager.availableFonts)
                        .formatValue(Text::literal)
                )
                .build();
        fontOption.addEventListener((opt, event) -> {
            if (previewEntry != null) {
                previewEntry.font = opt.pendingValue();
                previewEntry.invalidateCaches();
            }
        });

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
                .name(Text.translatable("customtooltips.tooltip_edit_screen.require_keybind"))
                .description(OptionDescription.of(Text.translatable("customtooltips.tooltip_edit_screen.require_keybind.description")))
                .binding(false, () -> entry.require_keybind, val -> entry.require_keybind = val)
                .controller(TickBoxControllerBuilder::create)
                .build();

        var emptyLineBefore = Option.<Boolean>createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.empty_line_before"))
                .description(OptionDescription.of(Text.translatable("customtooltips.tooltip_edit_screen.empty_line_before.description")))
                .binding(false, () -> entry.empty_line_before, val -> entry.empty_line_before = val)
                .controller(TickBoxControllerBuilder::create)
                .build();

        var hideVanillaLines = Option.<Boolean>createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.hide_vanilla_lines"))
                .description(OptionDescription.of(Text.translatable("customtooltips.tooltip_edit_screen.hide_vanilla_lines.description")))
                .binding(false, () -> entry.hide_vanilla_lines, val -> entry.hide_vanilla_lines = val)
                .controller(TickBoxControllerBuilder::create)
                .build();

        var showOnlyIfDamaged = Option.<Boolean>createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.show_only_if_damaged"))
                .description(OptionDescription.of(Text.translatable("customtooltips.tooltip_edit_screen.show_only_if_damaged.description")))
                .binding(false, () -> entry.show_only_if_damaged, val -> entry.show_only_if_damaged = val)
                .controller(TickBoxControllerBuilder::create)
                .build();

        var showOnlyIfEnchanted = Option.<Boolean>createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.show_only_if_enchanted"))
                .description(OptionDescription.of(Text.translatable("customtooltips.tooltip_edit_screen.show_only_if_enchanted.description")))
                .binding(false, () -> entry.show_only_if_enchanted, val -> entry.show_only_if_enchanted = val)
                .controller(TickBoxControllerBuilder::create)
                .build();

        var showOnlyIfUnbreakable = Option.<Boolean>createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.show_only_if_unbreakable"))
                .description(OptionDescription.of(Text.translatable("customtooltips.tooltip_edit_screen.show_only_if_unbreakable.description")))
                .binding(false, () -> entry.show_only_if_unbreakable, val -> entry.show_only_if_unbreakable = val)
                .controller(TickBoxControllerBuilder::create)
                .build();

        return OptionGroup.createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.category.conditions"))
                .option(requireShift)
                .option(emptyLineBefore)
                .option(hideVanillaLines)
                .option(showOnlyIfDamaged)
                .option(showOnlyIfEnchanted)
                .option(showOnlyIfUnbreakable)
                .build();
    }
}