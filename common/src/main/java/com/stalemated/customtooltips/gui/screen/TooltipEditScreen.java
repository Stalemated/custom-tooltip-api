package com.stalemated.customtooltips.gui.screen;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.api.enums.BackgroundType;
import com.stalemated.customtooltips.api.enums.TooltipPosition;
import com.stalemated.customtooltips.api.enums.TooltipStyle;
import com.stalemated.customtooltips.config.ConfigManager;
import com.stalemated.customtooltips.gui.helper.RenderGuiTooltipHelper;
import com.stalemated.customtooltips.util.CustomBackgroundManager;
import com.stalemated.customtooltips.util.CustomFontManager;
import com.stalemated.customtooltips.gui.factories.TooltipEditUIFactory;

import com.stalemated.lib.compat.yacl.controller.builder.ItemOrTagControllerBuilder;
import com.stalemated.lib.compat.yacl.controller.builder.SimpleEnumDropdownControllerBuilder;
import com.stalemated.lib.compat.yacl.controller.builder.SimpleStringDropdownControllerBuilder;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

import static com.stalemated.lib.util.color.ColorUtils.DEFAULT_OPACITY;

public class TooltipEditScreen {

    public static TooltipEntry previewEntry = null;

    public static void renderPreview(Screen screen, DrawContext context, int mouseX, int mouseY, float tickDelta) {
        if (previewEntry != null && screen.getTitle().contains(Text.translatable("customtooltips.tooltip_edit_screen.title"))) {
            if (Screen.hasControlDown()) {
                List<Text> previewLines = new ArrayList<>(previewEntry.getTextComponents(ItemStack.EMPTY));
                RenderGuiTooltipHelper.renderGuiTooltip(previewEntry, previewLines, context, mouseX, mouseY);
            }
        }
    }

    public static Screen create(Screen parent, TooltipEntry entry, boolean isNew) {
        previewEntry = entry.copy();

        return YetAnotherConfigLib.createBuilder()
                .title(Text.translatable("customtooltips.tooltip_edit_screen.title"))
                .save(() -> {
                    if (isNew) {
                        ConfigManager.getConfig().entries.add(entry);
                    } else {
                        entry.invalidateCaches();
                    }
                    ConfigManager.save();
                    if (parent instanceof TooltipListScreen listScreen) {
                        listScreen.listWidget.updateEntries(listScreen.searchBox.getText());
                    }
                })
                .category(ConfigCategory.createBuilder()
                        .name(Text.translatable("customtooltips.tooltip_edit_screen.title"))
                        .group(createTargetGroup(entry))
                        .group(createCustomTextGroup(entry))
                        .group(createStyleAndColorsGroup(entry))
                        .group(createPositionAndAnimationGroup(entry))
                        .group(createFormattingGroup(entry))
                        .group(createConditionsGroup(entry))
                        .build())
                .category(ConfigCategory.createBuilder()
                        .name(Text.translatable("customtooltips.tooltip_edit_screen.title_background"))
                        .group(createBackgroundOptionsGroup(entry))
                        .group(createBorderOptionsGroup(entry))
                        .build())
                .build()
                .generateScreen(parent);
    }

    private static OptionGroup createTargetGroup(TooltipEntry entry) {
        var target = TooltipEditUIFactory.buildOption(entry,
                "customtooltips.tooltip_edit_screen.target_id",
                "customtooltips.tooltip_edit_screen.target.description",
                "",
                e -> e.target, (e, val) -> e.target = val,
                ItemOrTagControllerBuilder::create
        );

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
                .binding(new ArrayList<>(List.of("Default text")), () -> new ArrayList<>(entry.text), val -> entry.text = val)
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

    private static OptionGroup createStyleAndColorsGroup(TooltipEntry entry) {
        var style = TooltipEditUIFactory.buildOption(entry,
                "customtooltips.tooltip_edit_screen.style",
                "customtooltips.tooltip_edit_screen.style.description",
                TooltipStyle.SOLID,
                e -> e.style, (e, val) -> e.style = val,
                opt -> SimpleEnumDropdownControllerBuilder.create(opt).formatValue(styleFormat -> Text.translatable("customtooltips.tooltip_edit_screen.style." + styleFormat.name().toLowerCase()))
        );

        var color1 = TooltipEditUIFactory.buildTextColor(entry,
                "customtooltips.tooltip_edit_screen.colors.primary_color",
                "customtooltips.tooltip_edit_screen.colors.primary_color.description",
                "customtooltips.tooltip_edit_screen.colors.color_override.description",
                0, e -> e.colors
        );

        var color2 = TooltipEditUIFactory.buildTextColor(entry,
                "customtooltips.tooltip_edit_screen.colors.secondary_color",
                "customtooltips.tooltip_edit_screen.colors.secondary_color.description",
                "customtooltips.tooltip_edit_screen.colors.color_override.description",
                1, e -> e.colors
        );

        return OptionGroup.createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.category.style_colors"))
                .option(style)
                .option(color1)
                .option(color2)
                .build();
    }

    private static OptionGroup createBackgroundOptionsGroup(TooltipEntry entry) {
        var bgOpacity = TooltipEditUIFactory.buildOption(entry,
                "customtooltips.tooltip_edit_screen.background_opacity",
                "customtooltips.tooltip_edit_screen.background_opacity.description",
                DEFAULT_OPACITY,
                e -> e.backgroundOpacity, (e, val) -> e.backgroundOpacity = val,
                opt -> IntegerSliderControllerBuilder.create(opt).range(0, 255).step(1)
        );

        var bgColor1 = TooltipEditUIFactory.buildColor(entry,
                "customtooltips.tooltip_edit_screen.colors.background_top_color",
                "customtooltips.tooltip_edit_screen.colors.background_top_color.description",
                0, "#F0100010", e -> e.backgroundColors
        );

        var bgColor2 = TooltipEditUIFactory.buildColor(entry,
                "customtooltips.tooltip_edit_screen.colors.background_bottom_color",
                "customtooltips.tooltip_edit_screen.colors.background_bottom_color.description",
                1, "#F0100010", e -> e.backgroundColors
        );

        var bgType = TooltipEditUIFactory.buildOption(entry,
                "customtooltips.tooltip_edit_screen.background_type",
                "customtooltips.tooltip_edit_screen.background_type.description",
                BackgroundType.SOLID,
                e -> e.backgroundType, (e, val) -> e.backgroundType = val,
                opt -> SimpleEnumDropdownControllerBuilder.create(opt).formatValue(type -> Text.translatable("customtooltips.tooltip_edit_screen.background_type." + type.name().toLowerCase()))
        );

        var bgTexture = TooltipEditUIFactory.buildOption(entry,
                "customtooltips.tooltip_edit_screen.background_texture",
                "customtooltips.tooltip_edit_screen.background_texture.description",
                "",
                e -> e.backgroundTexture, (e, val) -> e.backgroundTexture = val,
                opt -> SimpleStringDropdownControllerBuilder.create(opt)
                        .values(CustomBackgroundManager.availableBackgrounds)
                        .formatValue(s -> Text.literal(s.replace("custom_tooltip_api:textures/gui/tooltip_backgrounds/", "")))
        );

        var bgScale = TooltipEditUIFactory.buildOption(entry,
                "customtooltips.tooltip_edit_screen.background_scale",
                "customtooltips.tooltip_edit_screen.background_scale.description",
                100,
                e -> e.backgroundScale, (e, val) -> e.backgroundScale = val,
                opt -> IntegerSliderControllerBuilder.create(opt).range(10, 300).step(5)
        );

        return OptionGroup.createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.category.background"))
                .option(bgType)
                .option(bgOpacity)
                .option(bgColor1)
                .option(bgColor2)
                .option(bgTexture)
                .option(bgScale)
                .build();
    }

    private static OptionGroup createBorderOptionsGroup(TooltipEntry entry) {
        var borderOpacity = TooltipEditUIFactory.buildOption(entry,
                "customtooltips.tooltip_edit_screen.border_opacity",
                "customtooltips.tooltip_edit_screen.border_opacity.description",
                DEFAULT_OPACITY,
                e -> e.borderOpacity, (e, val) -> e.borderOpacity = val,
                opt -> IntegerSliderControllerBuilder.create(opt).range(0, 255).step(1)
        );

        var borderColor1 = TooltipEditUIFactory.buildColor(entry,
                "customtooltips.tooltip_edit_screen.colors.border_top_color",
                "customtooltips.tooltip_edit_screen.colors.border_top_color.description",
                0, "#505000FF", e -> e.borderColors
        );

        var borderColor2 = TooltipEditUIFactory.buildColor(entry,
                "customtooltips.tooltip_edit_screen.colors.border_bottom_color",
                "customtooltips.tooltip_edit_screen.colors.border_bottom_color.description",
                1, "#5028007F", e -> e.borderColors
        );

        return OptionGroup.createBuilder()
                .name(Text.translatable("customtooltips.tooltip_edit_screen.category.border"))
                .option(borderOpacity)
                .option(borderColor1)
                .option(borderColor2)
                .build();
    }

    private static OptionGroup createPositionAndAnimationGroup(TooltipEntry entry) {
        var position = TooltipEditUIFactory.buildOption(entry,
                "customtooltips.tooltip_edit_screen.position",
                "customtooltips.tooltip_edit_screen.position.description",
                TooltipPosition.BOTTOM,
                e -> e.position, (e, val) -> e.position = val,
                opt -> SimpleEnumDropdownControllerBuilder.create(opt).formatValue(pos -> Text.translatable("customtooltips.tooltip_edit_screen.position." + pos.name().toLowerCase()))
        );

        var offset = TooltipEditUIFactory.buildOption(entry,
                "customtooltips.tooltip_edit_screen.line_offset",
                "customtooltips.tooltip_edit_screen.line_offset.description",
                0,
                e -> e.lineOffset, (e, val) -> e.lineOffset = val,
                IntegerFieldControllerBuilder::create
        );

        var animOffset = TooltipEditUIFactory.buildOption(entry,
                "customtooltips.tooltip_edit_screen.animation_offset",
                "customtooltips.tooltip_edit_screen.animation_offset.description",
                0,
                e -> e.animation_offset, (e, val) -> e.animation_offset = val,
                opt -> IntegerSliderControllerBuilder.create(opt).range(-100, 100).step(1)
        );

        var rate = TooltipEditUIFactory.buildOption(entry,
                "customtooltips.tooltip_edit_screen.tickrate",
                "customtooltips.tooltip_edit_screen.tickrate.description",
                100,
                e -> e.tickrate, (e, val) -> e.tickrate = val,
                opt -> IntegerSliderControllerBuilder.create(opt).range(1, 500).step(1)
        );

        var reverseAnim = TooltipEditUIFactory.buildBoolean(entry,
                "customtooltips.tooltip_edit_screen.reverse_animation",
                "customtooltips.tooltip_edit_screen.reverse_animation.description",
                false,
                e -> e.reverse_animation, (e, val) -> e.reverse_animation = val
        );

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
        var bold = TooltipEditUIFactory.buildBoolean(entry, "customtooltips.tooltip_edit_screen.bold", "customtooltips.tooltip_edit_screen.bold.description", false, e -> e.bold, (e, val) -> e.bold = val);
        var italic = TooltipEditUIFactory.buildBoolean(entry, "customtooltips.tooltip_edit_screen.italic", "customtooltips.tooltip_edit_screen.italic.description", false, e -> e.italic, (e, val) -> e.italic = val);
        var underlined = TooltipEditUIFactory.buildBoolean(entry, "customtooltips.tooltip_edit_screen.underlined", "customtooltips.tooltip_edit_screen.underlined.description", false, e -> e.underlined, (e, val) -> e.underlined = val);
        var strikethrough = TooltipEditUIFactory.buildBoolean(entry, "customtooltips.tooltip_edit_screen.strikethrough", "customtooltips.tooltip_edit_screen.strikethrough.description", false, e -> e.strikethrough, (e, val) -> e.strikethrough = val);
        var obfuscated = TooltipEditUIFactory.buildBoolean(entry, "customtooltips.tooltip_edit_screen.obfuscated", "customtooltips.tooltip_edit_screen.obfuscated.description", false, e -> e.obfuscated, (e, val) -> e.obfuscated = val);

        var fontOption = TooltipEditUIFactory.buildIdentifierDropdown(entry,
                "customtooltips.tooltip_edit_screen.font",
                "customtooltips.tooltip_edit_screen.font.description",
                "minecraft:default",
                CustomFontManager.availableFonts,
                e -> e.font, (e, val) -> e.font = val
        );

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
        var requireShift = TooltipEditUIFactory.buildBoolean(entry, "customtooltips.tooltip_edit_screen.require_keybind", "customtooltips.tooltip_edit_screen.require_keybind.description", false, e -> e.require_keybind, (e, val) -> e.require_keybind = val);
        var emptyLineBefore = TooltipEditUIFactory.buildBoolean(entry, "customtooltips.tooltip_edit_screen.empty_line_before", "customtooltips.tooltip_edit_screen.empty_line_before.description", false, e -> e.empty_line_before, (e, val) -> e.empty_line_before = val);
        var hideVanillaLines = TooltipEditUIFactory.buildBoolean(entry, "customtooltips.tooltip_edit_screen.hide_vanilla_lines", "customtooltips.tooltip_edit_screen.hide_vanilla_lines.description", false, e -> e.hide_vanilla_lines, (e, val) -> e.hide_vanilla_lines = val);
        var showOnlyIfDamaged = TooltipEditUIFactory.buildBoolean(entry, "customtooltips.tooltip_edit_screen.show_only_if_damaged", "customtooltips.tooltip_edit_screen.show_only_if_damaged.description", false, e -> e.show_only_if_damaged, (e, val) -> e.show_only_if_damaged = val);
        var showOnlyIfEnchanted = TooltipEditUIFactory.buildBoolean(entry, "customtooltips.tooltip_edit_screen.show_only_if_enchanted", "customtooltips.tooltip_edit_screen.show_only_if_enchanted.description", false, e -> e.show_only_if_enchanted, (e, val) -> e.show_only_if_enchanted = val);
        var showOnlyIfUnbreakable = TooltipEditUIFactory.buildBoolean(entry, "customtooltips.tooltip_edit_screen.show_only_if_unbreakable", "customtooltips.tooltip_edit_screen.show_only_if_unbreakable.description", false, e -> e.show_only_if_unbreakable, (e, val) -> e.show_only_if_unbreakable = val);

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