package com.stalemated.customtooltips;

import com.stalemated.customtooltips.api.*;
import com.stalemated.customtooltips.api.enums.BackgroundType;
import com.stalemated.customtooltips.api.enums.TooltipPosition;
import com.stalemated.customtooltips.api.enums.TooltipStyle;
import com.stalemated.customtooltips.core.text.StyleApplier;
import com.stalemated.customtooltips.core.text.TextFormatter;
import com.stalemated.customtooltips.core.text.parser.PlaceholderParser;
import com.stalemated.customtooltips.core.text.parser.TranslationParser;
import com.stalemated.lib.predicate.target.TargetMatcher;
import com.stalemated.lib.predicate.target.TargetMatcherFactory;
import com.stalemated.lib.util.color.ColorUtils;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static com.stalemated.lib.util.color.ColorUtils.*;

public class TooltipEntry {

    public String target = "";
    public List<String> text = new ArrayList<>();

    public TooltipStyle style = TooltipStyle.SOLID;

    public List<String> colors = new ArrayList<>();
    public List<String> borderColors = new ArrayList<>();
    public List<String> backgroundColors = new ArrayList<>();
    public BackgroundType backgroundType = BackgroundType.SOLID;
    public String backgroundTexture = "";

    public int backgroundOpacity = DEFAULT_OPACITY;
    public int borderOpacity = DEFAULT_OPACITY;

    public TooltipPosition position = TooltipPosition.BOTTOM;

    public int lineOffset = 0;

    public boolean bold = false;
    public boolean italic = false;
    public boolean underlined = false;
    public boolean strikethrough = false;
    public boolean obfuscated = false;

    public boolean require_keybind = false;
    public boolean empty_line_before = false;
    public boolean hide_vanilla_lines = false;
    
    public boolean show_only_if_damaged = false;
    public boolean show_only_if_enchanted = false;
    public boolean show_only_if_unbreakable = false;

    public String font = "minecraft:default";

    public int animation_offset = 0;
    public int tickrate = 100;
    public boolean reverse_animation = false;

    public String uuid;

    // Ignored caches
    private transient boolean cachesInitialized = false;
    private transient TargetMatcher targetMatcher = null;
    private transient int parsedColor1 = DEFAULT_COLOR;
    private transient int parsedColor2 = DEFAULT_COLOR;
    private transient boolean isGradient = false;
    private transient List<Text> cachedStaticText = null;
    private transient Style cachedStyleModifier = null;
    private transient int parsedBorderColorStart = DEFAULT_BORDER_COLORS.getFirst();
    private transient int parsedBorderColorEnd = DEFAULT_BORDER_COLORS.get(1);
    private transient int parsedBackgroundColorStart = DEFAULT_BACKGROUND_COLORS.getFirst();
    private transient int parsedBackgroundColorEnd = DEFAULT_BACKGROUND_COLORS.get(1);

    public transient boolean apiEntry = false;
    public transient String apiEntryId = "";
    public transient Function<ItemStack, List<String>> dynamicTextProvider = null;
    public transient boolean hasDynamicText = false;

    public TooltipEntry() {
        this.uuid = UUID.randomUUID().toString();
    }

    public TooltipEntry(String target, List<String> text, TooltipStyle style, List<String> colors, List<String> borderColors, List<String> backgroundColors, int backgroundOpacity, int borderOpacity, boolean bold, boolean italic, boolean underlined, boolean strikethrough, boolean obfuscated, boolean require_keybind, boolean empty_line_before, boolean hide_vanilla_lines, boolean show_only_if_damaged, boolean show_only_if_enchanted, boolean show_only_if_unbreakable, TooltipPosition position, int lineOffset, int animation_offset, int tickrate, boolean reverse_animation, String font) {
        this.target = target;
        this.text = text != null ? text : new ArrayList<>();
        this.style = style;
        this.colors = colors != null ? colors : new ArrayList<>();
        this.borderColors = borderColors != null ? borderColors : new ArrayList<>();
        this.backgroundColors = backgroundColors != null ? backgroundColors : new ArrayList<>();
        this.backgroundOpacity = backgroundOpacity;
        this.borderOpacity = borderOpacity;
        this.bold = bold;
        this.italic = italic;
        this.underlined = underlined;
        this.strikethrough = strikethrough;
        this.obfuscated = obfuscated;
        this.require_keybind = require_keybind;
        this.empty_line_before = empty_line_before;
        this.hide_vanilla_lines = hide_vanilla_lines;
        this.show_only_if_damaged = show_only_if_damaged;
        this.show_only_if_enchanted = show_only_if_enchanted;
        this.show_only_if_unbreakable = show_only_if_unbreakable;
        this.position = position;
        this.lineOffset = lineOffset;
        this.animation_offset = animation_offset;
        this.tickrate = tickrate;
        this.reverse_animation = reverse_animation;
        this.font = font != null && !font.isEmpty() ? font : "minecraft:default";
        this.uuid = UUID.randomUUID().toString();
    }

    public String getIdentifier() {
        if (this.apiEntry && this.apiEntryId != null && !this.apiEntryId.isEmpty()) {
            // Creates a deterministic hash based on the entry's content to differentiate multiple entries with the same apiEntryId
            long hash = this.target.hashCode() + this.text.hashCode() + this.position.toString().hashCode() + this.style.toString().hashCode() + this.colors.hashCode();
            return this.apiEntryId + ":" + hash;
        }
        return this.uuid;
    }

    public boolean isGradient() { return this.isGradient; }
    public int getParsedColor1() { return this.parsedColor1; }
    public int getParsedColor2() { return this.parsedColor2; }

    public Style getCachedStyleModifier() { return this.cachedStyleModifier; }
    public List<Text> getCachedStaticText() { return this.cachedStaticText; }
    public void setCachedStaticText(List<Text> text) { this.cachedStaticText = text; }

    public boolean hasCustomBorder() {
        return !this.borderColors.isEmpty() && !List.of(this.parsedBorderColorStart, this.parsedBorderColorEnd).equals(DEFAULT_BORDER_COLORS) ||
                this.borderOpacity != DEFAULT_OPACITY;
    }
    public int getParsedBorderColorStart() { return this.parsedBorderColorStart; }
    public int getParsedBorderColorEnd() { return this.parsedBorderColorEnd; }

    public boolean hasCustomBackground() {
        return this.backgroundOpacity != DEFAULT_OPACITY ||
                !this.backgroundColors.isEmpty() && this.parsedBackgroundColorStart != DEFAULT_BACKGROUND_COLORS.getFirst() && this.backgroundType == BackgroundType.SOLID ||
                !this.backgroundColors.isEmpty() && !List.of(this.parsedBackgroundColorStart, this.parsedBackgroundColorEnd).equals(DEFAULT_BACKGROUND_COLORS) && this.backgroundType == BackgroundType.GRADIENT ||
                this.backgroundType == BackgroundType.TEXTURE && !this.backgroundTexture.isEmpty() ||
                this.backgroundType == BackgroundType.SIMPLE_TEXTURE && !this.backgroundTexture.isEmpty() ||
                this.backgroundType == BackgroundType.REPEATING_TEXTURE && !this.backgroundTexture.isEmpty();
    }
    public int getParsedBackgroundColorStart() { return this.parsedBackgroundColorStart; }
    public int getParsedBackgroundColorEnd() { return this.parsedBackgroundColorEnd; }

    public void validateColors() {
        if (this.colors != null) {
            if (!this.colors.isEmpty() && (this.colors.get(0) == null || this.colors.get(0).trim().isEmpty())) this.colors.set(0, DEFAULT_COLOR_STRING);
            if (this.colors.size() > 1 && (this.colors.get(1) == null || this.colors.get(1).trim().isEmpty())) this.colors.set(1, DEFAULT_COLOR_STRING);
        }

        if (this.borderColors != null) {
            if (!this.borderColors.isEmpty() && (this.borderColors.get(0) == null || this.borderColors.get(0).trim().isEmpty())) this.borderColors.set(0, DEFAULT_BORDER_COLORS_STRING.get(0));
            if (this.borderColors.size() > 1 && (this.borderColors.get(1) == null || this.borderColors.get(1).trim().isEmpty())) this.borderColors.set(1, DEFAULT_BORDER_COLORS_STRING.get(1));
        }

        if (this.backgroundColors != null) {
            if (!this.backgroundColors.isEmpty() && (this.backgroundColors.get(0) == null || this.backgroundColors.get(0).trim().isEmpty())) this.backgroundColors.set(0, DEFAULT_BACKGROUND_COLORS_STRING.get(0));
            if (this.backgroundColors.size() > 1 && (this.backgroundColors.get(1) == null || this.backgroundColors.get(1).trim().isEmpty())) this.backgroundColors.set(1, DEFAULT_BACKGROUND_COLORS_STRING.get(1));
        }
    }

    public void invalidateCaches() {
        this.cachesInitialized = false;
        this.cachedStaticText = null;
        this.cachedStyleModifier = null;
        this.targetMatcher = null;
    }

    public void initCaches() {
        if (cachesInitialized) return;

        validateColors();

        this.targetMatcher = TargetMatcherFactory.create(this.target);

        this.isGradient = this.colors != null && this.colors.size() >= 2;
        this.parsedColor1 = (this.colors != null && !this.colors.isEmpty()) ? ColorUtils.parseColor(this.colors.get(0)) : DEFAULT_COLOR;
        this.parsedColor2 = this.isGradient ? ColorUtils.parseColor(this.colors.get(1)) : DEFAULT_COLOR;
        if (this.tickrate <= 0) this.tickrate = 100;

        this.cachedStyleModifier = StyleApplier.buildStyleModifier(this);
        
        this.hasDynamicText = false;
        for (String line : this.text) {
            if (PlaceholderParser.containsDynamicPlaceholders(line) || TranslationParser.containsTranslation(line)) {
                this.hasDynamicText = true;
                break;
            }
        }

        if (!this.borderColors.isEmpty()) {
            this.parsedBorderColorStart = ColorUtils.parseARGBColor(this.borderColors.get(0), 0);
            this.parsedBorderColorEnd = ColorUtils.parseARGBColor(this.borderColors.get(1), 1);
        }

        if (!this.backgroundColors.isEmpty()) {
            this.parsedBackgroundColorStart = ColorUtils.parseARGBColor(this.backgroundColors.get(0), 0);
            if (this.backgroundType != BackgroundType.SOLID) this.parsedBackgroundColorEnd = ColorUtils.parseARGBColor(this.backgroundColors.get(1), 1);
        }

        this.cachesInitialized = true;
    }

    public boolean matches(ItemStack stack) {
        if (!cachesInitialized) initCaches();

        return this.targetMatcher != null && this.targetMatcher.matches(stack);
    }

    public boolean areItemConditionsMet(ItemStack stack) {
        if (this.show_only_if_damaged && !stack.isDamaged()) return false;
        if (this.show_only_if_enchanted && !stack.hasEnchantments()) return false;
        return !this.show_only_if_unbreakable || stack.contains(DataComponentTypes.UNBREAKABLE);
    }

    public TooltipEntry copy() {
        return CustomTooltipApi.builder(this.target)
                .text(this.text)
                .dynamicText(this.dynamicTextProvider)
                .style(this.style)
                .colors(this.colors)
                .borderColors(this.borderColors)
                .backgroundColors(this.backgroundColors)
                .backgroundType(this.backgroundType)
                .backgroundTexture(this.backgroundTexture)
                .backgroundOpacity(this.backgroundOpacity)
                .borderOpacity(this.borderOpacity)
                .position(this.position)
                .lineOffset(this.lineOffset)
                .bold(this.bold)
                .italic(this.italic)
                .underlined(this.underlined)
                .strikethrough(this.strikethrough)
                .obfuscated(this.obfuscated)
                .requireKeybind(this.require_keybind)
                .emptyLineBefore(this.empty_line_before)
                .hideVanillaLines(this.hide_vanilla_lines)
                .showOnlyIfDamaged(this.show_only_if_damaged)
                .showOnlyIfEnchanted(this.show_only_if_enchanted)
                .showOnlyIfUnbreakable(this.show_only_if_unbreakable)
                .font(this.font)
                .animationOffset(this.animation_offset)
                .tickrate(this.tickrate)
                .reverseAnimation(this.reverse_animation)
                .build();
    }

    public List<Text> getTextComponents(ItemStack stack) {
        if (!cachesInitialized) initCaches();
        return TextFormatter.getOrGenerateComponents(this, stack);
    }

    public int getLineOffset(int size) {
        if (size == 0) return 0;
        if (acceptsPositiveOffset()) {
            return Math.clamp(this.lineOffset, 0, size - 1);
        } else {
            return Math.clamp(this.lineOffset, -(size - 1), 0);
        }
    }

    private boolean acceptsPositiveOffset() {
        return this.position == TooltipPosition.TOP ||
                this.position == TooltipPosition.REPLACE_NAME ||
                this.position == TooltipPosition.APPEND ||
                this.position == TooltipPosition.PREPEND ||
                this.position == TooltipPosition.REPLACE_LINE;
    }
}