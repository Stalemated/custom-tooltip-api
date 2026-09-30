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
import com.stalemated.lib.util.math.MathUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.stalemated.lib.util.color.ColorUtils.*;

public class TooltipEntry {

    public String target = "";
    public List<String> text = new ArrayList<>();

    public TooltipStyle style = TooltipStyle.SOLID;

    public List<TextColor> colors = new ArrayList<>();
    public List<Color> borderColors = new ArrayList<>();
    public List<Color> backgroundColors = new ArrayList<>();
    public BackgroundType backgroundType = BackgroundType.SOLID;
    public String backgroundTexture = "";

    public int backgroundOpacity = DEFAULT_OPACITY;
    public int borderOpacity = DEFAULT_OPACITY;
    public int backgroundScale = 100;

    public TooltipPosition position = TooltipPosition.BOTTOM;

    public int lineOffset = 0;

    public boolean bold = false;
    public boolean italic = false;
    public boolean underlined = false;
    public boolean strikethrough = false;
    public boolean obfuscated = false;

    public boolean requireKeybind = false;
    public boolean emptyLineBefore = false;
    public boolean hideVanillaLines = false;
    
    public boolean showOnlyIfDamaged = false;
    public boolean showOnlyIfEnchanted = false;
    public boolean showOnlyIfUnbreakable = false;

    public Identifier font = Identifier.of("minecraft", "default");

    public int animationOffset = 0;
    public int tickrate = 100;
    public boolean reverseAnimation = false;

    public UUID uuid;

    // Ignored caches
    private transient boolean cachesInitialized = false;
    private transient TargetMatcher targetMatcher = null;
    private transient int parsedColor1 = DEFAULT_COLOR;
    private transient int parsedColor2 = DEFAULT_COLOR;
    private transient boolean isGradient = false;
    private transient List<Text> cachedStaticText = null;
    private transient Style cachedStyleModifier = null;
    private transient int parsedBorderColorStart = DEFAULT_BORDER_COLORS.get(0);
    private transient int parsedBorderColorEnd = DEFAULT_BORDER_COLORS.get(1);
    private transient int parsedBackgroundColorStart = DEFAULT_BACKGROUND_COLORS.get(0);
    private transient int parsedBackgroundColorEnd = DEFAULT_BACKGROUND_COLORS.get(1);

    public transient boolean apiEntry = false;
    public transient String apiEntryId = "";
    public transient Function<ItemStack, List<String>> dynamicTextProvider = null;
    public transient Predicate<ItemStack> displayCondition = stack -> true;
    public transient boolean hasDynamicText = false;

    public TooltipEntry() {
        this.uuid = UUID.randomUUID();
    }

    public TooltipEntry(String target, List<String> text, TooltipStyle style, List<TextColor> colors, List<Color> borderColors, List<Color> backgroundColors, int backgroundOpacity, int borderOpacity, int backgroundScale, boolean bold, boolean italic, boolean underlined, boolean strikethrough, boolean obfuscated, boolean requireKeybind, boolean emptyLineBefore, boolean hideVanillaLines, boolean showOnlyIfDamaged, boolean showOnlyIfEnchanted, boolean showOnlyIfUnbreakable, TooltipPosition position, int lineOffset, int animationOffset, int tickrate, boolean reverseAnimation, Identifier font) {
        this.target = target;
        this.text = text != null ? text : new ArrayList<>();
        this.style = style;
        this.colors = colors != null ? colors : new ArrayList<>();
        this.borderColors = borderColors != null ? borderColors : new ArrayList<>();
        this.backgroundColors = backgroundColors != null ? backgroundColors : new ArrayList<>();
        this.backgroundOpacity = backgroundOpacity;
        this.borderOpacity = borderOpacity;
        this.backgroundScale = backgroundScale;
        this.bold = bold;
        this.italic = italic;
        this.underlined = underlined;
        this.strikethrough = strikethrough;
        this.obfuscated = obfuscated;
        this.requireKeybind = requireKeybind;
        this.emptyLineBefore = emptyLineBefore;
        this.hideVanillaLines = hideVanillaLines;
        this.showOnlyIfDamaged = showOnlyIfDamaged;
        this.showOnlyIfEnchanted = showOnlyIfEnchanted;
        this.showOnlyIfUnbreakable = showOnlyIfUnbreakable;
        this.position = position;
        this.lineOffset = lineOffset;
        this.animationOffset = animationOffset;
        this.tickrate = tickrate;
        this.reverseAnimation = reverseAnimation;
        this.font = font != null ? font : Identifier.of("minecraft", "default");
        this.uuid = UUID.randomUUID();
    }

    public String getIdentifier() {
        if (this.apiEntry && this.apiEntryId != null && !this.apiEntryId.isEmpty()) {
            // Creates a deterministic hash based on the entry's content to differentiate multiple entries with the same apiEntryId
            long hash = this.target.hashCode() + this.text.hashCode() + this.position.toString().hashCode() + this.style.toString().hashCode() + this.colors.hashCode();
            return this.apiEntryId + ":" + hash;
        }
        return this.uuid.toString();
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
                !this.backgroundColors.isEmpty() && this.parsedBackgroundColorStart != DEFAULT_BACKGROUND_COLORS.get(0) && this.backgroundType == BackgroundType.SOLID ||
                !this.backgroundColors.isEmpty() && !List.of(this.parsedBackgroundColorStart, this.parsedBackgroundColorEnd).equals(DEFAULT_BACKGROUND_COLORS) && this.backgroundType == BackgroundType.GRADIENT ||
                this.backgroundType == BackgroundType.TEXTURE && !this.backgroundTexture.isEmpty() ||
                this.backgroundType == BackgroundType.SIMPLE_TEXTURE && !this.backgroundTexture.isEmpty() ||
                this.backgroundType == BackgroundType.REPEATING_TEXTURE && !this.backgroundTexture.isEmpty();
    }
    public int getParsedBackgroundColorStart() { return this.parsedBackgroundColorStart; }
    public int getParsedBackgroundColorEnd() { return this.parsedBackgroundColorEnd; }

    public void invalidateCaches() {
        this.cachesInitialized = false;
        this.cachedStaticText = null;
        this.cachedStyleModifier = null;
        this.targetMatcher = null;
    }

    public void initCaches() {
        if (cachesInitialized) return;

        this.targetMatcher = TargetMatcherFactory.create(this.target);

        this.isGradient = this.colors != null && this.colors.size() >= 2;
        this.parsedColor1 = (this.colors != null && !this.colors.isEmpty() && this.colors.get(0) != null) ? this.colors.get(0).getRgb() : DEFAULT_COLOR;
        this.parsedColor2 = this.isGradient && this.colors.get(1) != null ? this.colors.get(1).getRgb() : DEFAULT_COLOR;
        if (this.tickrate <= 0) this.tickrate = 100;

        this.cachedStyleModifier = StyleApplier.buildStyleModifier(this);
        
        this.hasDynamicText = false;
        for (String line : this.text) {
            if (PlaceholderParser.containsDynamicPlaceholders(line) || TranslationParser.containsTranslation(line)) {
                this.hasDynamicText = true;
                break;
            }
        }

        if (this.borderColors != null && !this.borderColors.isEmpty()) {
            this.parsedBorderColorStart = this.borderColors.get(0) != null ? this.borderColors.get(0).getRGB() : DEFAULT_BORDER_COLORS.get(0);
            if (this.borderColors.size() > 1 && this.borderColors.get(1) != null) {
                this.parsedBorderColorEnd = this.borderColors.get(1).getRGB();
            } else {
                this.parsedBorderColorEnd = DEFAULT_BORDER_COLORS.get(1);
            }
        }

        if (this.backgroundColors != null && !this.backgroundColors.isEmpty()) {
            this.parsedBackgroundColorStart = this.backgroundColors.get(0) != null ? this.backgroundColors.get(0).getRGB() : DEFAULT_BACKGROUND_COLORS.get(0);
            if (this.backgroundType != BackgroundType.SOLID && this.backgroundColors.size() > 1 && this.backgroundColors.get(1) != null) {
                this.parsedBackgroundColorEnd = this.backgroundColors.get(1).getRGB();
            } else {
                this.parsedBackgroundColorEnd = DEFAULT_BACKGROUND_COLORS.get(1);
            }
        }

        this.cachesInitialized = true;
    }

    public boolean matches(ItemStack stack) {
        if (!cachesInitialized) initCaches();

        if (this.displayCondition != null && !this.displayCondition.test(stack)) {
            return false;
        }

        return this.targetMatcher != null && this.targetMatcher.matches(stack);
    }

    public boolean areItemConditionsMet(ItemStack stack) {
        if (this.showOnlyIfDamaged && !stack.isDamaged()) return false;
        if (this.showOnlyIfEnchanted && !stack.hasEnchantments()) return false;
        return !this.showOnlyIfUnbreakable || stack.hasNbt() && Objects.requireNonNull(stack.getNbt()).getBoolean("Unbreakable");
    }

    public TooltipEntry copy() {
        return CustomTooltipApi.builder(this.target)
                .text(this.text)
                .dynamicText(this.dynamicTextProvider)
                .displayCondition(this.displayCondition)
                .style(this.style)
                .colorsList(this.colors)
                .borderColorsList(this.borderColors)
                .backgroundColorsList(this.backgroundColors)
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
                .requireKeybind(this.requireKeybind)
                .emptyLineBefore(this.emptyLineBefore)
                .hideVanillaLines(this.hideVanillaLines)
                .showOnlyIfDamaged(this.showOnlyIfDamaged)
                .showOnlyIfEnchanted(this.showOnlyIfEnchanted)
                .showOnlyIfUnbreakable(this.showOnlyIfUnbreakable)
                .font(this.font)
                .animationOffset(this.animationOffset)
                .tickrate(this.tickrate)
                .reverseAnimation(this.reverseAnimation)
                .build();
    }

    public List<Text> getTextComponents(ItemStack stack) {
        if (!cachesInitialized) initCaches();
        return TextFormatter.getOrGenerateComponents(this, stack);
    }

    public int getLineOffset(int size) {
        if (size == 0) return 0;
        if (acceptsPositiveOffset()) {
            return MathUtils.clamp(this.lineOffset, 0, size - 1);
        } else {
            return MathUtils.clamp(this.lineOffset, -(size - 1), 0);
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