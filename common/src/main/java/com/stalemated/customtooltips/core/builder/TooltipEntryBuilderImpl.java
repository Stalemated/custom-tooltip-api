package com.stalemated.customtooltips.core.builder;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.api.*;
import com.stalemated.customtooltips.api.enums.BackgroundType;
import com.stalemated.customtooltips.api.enums.TooltipPosition;
import com.stalemated.customtooltips.api.enums.TooltipStyle;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.function.Function;

public class TooltipEntryBuilderImpl implements TooltipBuilder {
    private final TooltipEntry entry;

    public TooltipEntryBuilderImpl(String target) {
        this.entry = new TooltipEntry();
        this.entry.target = target;
    }

    @Override
    public TooltipBuilder addLine(String line) {
        this.entry.text.add(line);
        return this;
    }

    @Override
    public TooltipBuilder text(List<String> lines) {
        this.entry.text.addAll(lines);
        return this;
    }

    @Override
    public TooltipBuilder dynamicText(Function<ItemStack, List<String>> provider) {
        this.entry.dynamicTextProvider = provider;
        return this;
    }

    @Override
    public TooltipBuilder style(TooltipStyle style) {
        this.entry.style = style;
        return this;
    }

    @Override
    public TooltipBuilder colors(String... colors) {
        this.entry.colors.addAll(List.of(colors));
        return this;
    }

    @Override
    public TooltipBuilder colors(List<String> colors) {
        this.entry.colors.addAll(colors);
        return this;
    }

    @Override
    public TooltipBuilder borderColors(String... colors) {
        this.entry.borderColors.addAll(List.of(colors));
        return this;
    }

    @Override
    public TooltipBuilder borderColors(List<String> colors) {
        this.entry.borderColors.addAll(colors);
        return this;
    }

    @Override
    public TooltipBuilder backgroundColors(String... colors) {
        this.entry.backgroundColors.addAll(List.of(colors));
        return this;
    }

    @Override
    public TooltipBuilder backgroundColors(List<String> colors) {
        this.entry.backgroundColors.addAll(colors);
        return this;
    }

    @Override
    public TooltipBuilder backgroundType(BackgroundType backgroundType) {
        this.entry.backgroundType = backgroundType;
        return this;
    }

    @Override
    public TooltipBuilder backgroundTexture(String backgroundTexture) {
        this.entry.backgroundTexture = backgroundTexture;
        return this;
    }

    @Override
    public TooltipBuilder backgroundOpacity(int backgroundOpacity) {
        this.entry.backgroundOpacity = backgroundOpacity;
        return this;
    }

    @Override
    public TooltipBuilder backgroundScale(int backgroundScale) {
        this.entry.backgroundScale = backgroundScale;
        return this;
    }

    @Override
    public TooltipBuilder borderOpacity(int borderOpacity) {
        this.entry.borderOpacity = borderOpacity;
        return this;
    }

    @Override
    public TooltipBuilder position(TooltipPosition position) {
        this.entry.position = position;
        return this;
    }

    @Override
    public TooltipBuilder lineOffset(int lineOffset) {
        this.entry.lineOffset = lineOffset;
        return this;
    }

    @Override
    public TooltipBuilder bold(boolean bold) {
        this.entry.bold = bold;
        return this;
    }

    @Override
    public TooltipBuilder italic(boolean italic) {
        this.entry.italic = italic;
        return this;
    }

    @Override
    public TooltipBuilder underlined(boolean underlined) {
        this.entry.underlined = underlined;
        return this;
    }

    @Override
    public TooltipBuilder strikethrough(boolean strikethrough) {
        this.entry.strikethrough = strikethrough;
        return this;
    }

    @Override
    public TooltipBuilder obfuscated(boolean obfuscated) {
        this.entry.obfuscated = obfuscated;
        return this;
    }

    @Override
    public TooltipBuilder requireKeybind(boolean requireKeybind) {
        this.entry.require_keybind = requireKeybind;
        return this;
    }

    @Override
    public TooltipBuilder emptyLineBefore(boolean emptyLineBefore) {
        this.entry.empty_line_before = emptyLineBefore;
        return this;
    }

    @Override
    public TooltipBuilder hideVanillaLines(boolean hideVanillaLines) {
        this.entry.hide_vanilla_lines = hideVanillaLines;
        return this;
    }

    @Override
    public TooltipBuilder showOnlyIfDamaged(boolean showOnlyIfDamaged) {
        this.entry.show_only_if_damaged = showOnlyIfDamaged;
        return this;
    }

    @Override
    public TooltipBuilder showOnlyIfEnchanted(boolean showOnlyIfEnchanted) {
        this.entry.show_only_if_enchanted = showOnlyIfEnchanted;
        return this;
    }

    @Override
    public TooltipBuilder showOnlyIfUnbreakable(boolean showOnlyIfUnbreakable) {
        this.entry.show_only_if_unbreakable = showOnlyIfUnbreakable;
        return this;
    }

    @Override
    public TooltipBuilder font(String fontIdentifier) {
        this.entry.font = fontIdentifier;
        return this;
    }

    @Override
    public TooltipBuilder animationOffset(int offset) {
        this.entry.animation_offset = offset;
        return this;
    }

    @Override
    public TooltipBuilder tickrate(int tickrate) {
        this.entry.tickrate = tickrate;
        return this;
    }

    @Override
    public TooltipBuilder reverseAnimation(boolean reverse) {
        this.entry.reverse_animation = reverse;
        return this;
    }

    @Override
    public TooltipEntry build() {
        return this.entry;
    }

    @Override
    public TooltipEntry register() {
        CustomTooltipApi.registerTooltip(this.entry);
        return this.entry;
    }
}
