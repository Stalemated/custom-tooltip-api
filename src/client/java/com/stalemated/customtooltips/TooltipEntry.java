package com.stalemated.customtooltips;

import com.stalemated.customtooltips.api.CustomTooltipApi;
import com.stalemated.customtooltips.core.text.StyleApplier;
import com.stalemated.customtooltips.core.text.TextFormatter;
import com.stalemated.customtooltips.core.text.parser.PlaceholderParser;
import com.stalemated.customtooltips.core.target.TargetMatcher;
import com.stalemated.customtooltips.core.target.TargetMatcherFactory;
import com.stalemated.customtooltips.util.ColorUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

public class TooltipEntry {

    public enum TooltipStyle {
        SOLID, STATIC_GRADIENT, SLIDE_GRADIENT, BREATHING_GRADIENT, RAINBOW
    }

    public enum TooltipPosition {
        REPLACE_NAME, REPLACE_ALL, TOP, BOTTOM, APPEND, PREPEND
    }

    public enum BackgroundType {
        SOLID, GRADIENT, SIMPLE_TEXTURE, TEXTURE
    }

    public String target = "";
    public List<String> text = new ArrayList<>();

    public TooltipStyle style = TooltipStyle.SOLID;

    public static final int DEFAULT_COLOR = 0xFFFFFF;
    public static final List<Integer> DEFAULT_BORDER_COLORS = new ArrayList<>(List.of(0x505000FF, 0x5028007F));
    public static final List<Integer> DEFAULT_BACKGROUND_COLORS = new ArrayList<>(List.of(0xF0100010, 0xF0100010));

    public List<String> colors = new ArrayList<>();
    public List<String> borderColors = new ArrayList<>();
    public List<String> backgroundColors = new ArrayList<>();
    public BackgroundType backgroundType = BackgroundType.SOLID;
    public String backgroundTexture = "";
    public static final int DEFAULT_OPACITY = 240;
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
    private transient int parsedBorderColorStart = DEFAULT_BORDER_COLORS.get(0);
    private transient int parsedBorderColorEnd = DEFAULT_BORDER_COLORS.get(1);
    private transient int parsedBackgroundColorStart = DEFAULT_BACKGROUND_COLORS.get(0);
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

    public boolean hasCustomBorder() { return !this.borderColors.isEmpty(); }
    public int getParsedBorderColorStart() { return this.parsedBorderColorStart; }
    public int getParsedBorderColorEnd() { return this.parsedBorderColorEnd; }

    public boolean hasCustomBackground() { return !this.backgroundColors.isEmpty(); }
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
        this.parsedColor1 = (this.colors != null && !this.colors.isEmpty()) ? ColorUtils.parseColor(this.colors.get(0)) : 0xFFFFFF;
        this.parsedColor2 = this.isGradient ? ColorUtils.parseColor(this.colors.get(1)) : 0xFFFFFF;
        if (this.tickrate <= 0) this.tickrate = 100;

        this.cachedStyleModifier = StyleApplier.buildStyleModifier(this);
        
        this.hasDynamicText = false;
        for (String line : this.text) {
            if (PlaceholderParser.containsDynamicPlaceholders(line)) {
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
        return !this.show_only_if_unbreakable || stack.hasNbt() && Objects.requireNonNull(stack.getNbt()).getBoolean("Unbreakable");
    }

    public TooltipEntry copy() {
        return TooltipEntry.builder(this.target)
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

    public TooltipEntry display() {
        return this;
    }

    public List<Text> getTextComponents(ItemStack stack) {
        if (!cachesInitialized) initCaches();
        return TextFormatter.getOrGenerateComponents(this, stack);
    }

    public int getLineOffset(int size) {
        if (this.position == TooltipPosition.TOP || this.position == TooltipPosition.REPLACE_NAME || this.position == TooltipPosition.APPEND || this.position == TooltipPosition.PREPEND) {
            return Math.max(this.lineOffset, 0) < size ? Math.max(this.lineOffset, 0) : Math.max(size - 1, 0);
        } else {
            return Math.min(this.lineOffset, 0) > (-size) ? Math.min(this.lineOffset, 0) : Math.min(-(size - 1), 0);
        }
    }

    /**
     * Creates a new Builder instance for configuring a TooltipEntry.
     *
     * @param target The target item ID, tag ("#c:swords"), namespace ("minecraft:*"), regex ("regex:.*sword.*"), or all items ("*").
     * @return A new Builder instance.
     */
    public static Builder builder(String target) {
        return new Builder(target);
    }

    /**
     * A fluent builder class for creating and configuring {@link TooltipEntry} instances.
     * Allows for method chaining to easily set tooltip properties before building or registering.
     */
    public static class Builder {
        private final TooltipEntry entry;

        /**
         * Initializes a new Builder with the specified target.
         *
         * @param target The target item ID or tag.
         */
        public Builder(String target) {
            this.entry = new TooltipEntry();
            this.entry.target = target;
        }

        /**
         * Adds a single line of text to the tooltip.
         *
         * @param line The text line to add.
         * @return This builder instance.
         */
        public Builder addLine(String line) {
            this.entry.text.add(line);
            return this;
        }

        /**
         * Adds multiple lines of text to the tooltip.
         *
         * @param lines A list of text lines to add.
         * @return This builder instance.
         */
        public Builder text(List<String> lines) {
            this.entry.text.addAll(lines);
            return this;
        }

        /**
         * Sets a dynamic text provider for this tooltip.
         * This allows the text to change every frame based on the ItemStack's state (e.g., NBT data, enchantments, or any other dynamic properties).
         * Overrides the static text set by {@link #text(List)} or {@link #addLine(String)}.
         *
         * @param provider A function that takes an ItemStack and returns a list of strings.
         * @return This builder instance.
         */
        public Builder dynamicText(Function<ItemStack, List<String>> provider) {
            this.entry.dynamicTextProvider = provider;
            return this;
        }

        /**
         * Sets the rendering and animation style of the tooltip.
         *
         * @param style The desired {@link TooltipStyle} (e.g., SOLID, RAINBOW, SLIDE_GRADIENT).
         * @return This builder instance.
         */
        public Builder style(TooltipStyle style) {
            this.entry.style = style;
            return this;
        }

        /**
         * Sets the colors used by the tooltip style.
         * <p>
         * Accepts hex codes (e.g., "#FF0000", "0x00FF00", "x0000FF", "FFFFFF"), Minecraft color names (e.g., "red", "blue") or legacy color codes (e.g., "&4", "&c").
         *
         * @param colors The colors to apply (1 or 2 depending on the style).
         * @return This builder instance.
         */
        public Builder colors(String... colors) {
            this.entry.colors.addAll(List.of(colors));
            return this;
        }

        /**
         * Sets the colors used by the tooltip style from a list.
         * <p>
         * Accepts hex codes (e.g., "#FF0000", "0x00FF00", "x0000FF", "FFFFFF"), Minecraft color names (e.g., "red", "blue") or legacy color codes (e.g., "&4", "&c").
         *
         * @param colors A list of color strings.
         * @return This builder instance.
         */
        public Builder colors(List<String> colors) {
            this.entry.colors.addAll(colors);
            return this;
        }

        /**
         * Sets the border colors used by the tooltip.
         *
         * @param colors The colors to apply (2 colors, start and end).
         * @return This builder instance.
         */
        public Builder borderColors(String... colors) {
            this.entry.borderColors.addAll(List.of(colors));
            return this;
        }

        /**
         * Sets the border colors used by the tooltip from a list.
         *
         * @param colors A list of color strings (2 colors, start and end).
         * @return This builder instance.
         */
        public Builder borderColors(List<String> colors) {
            this.entry.borderColors.addAll(colors);
            return this;
        }

        /**
         * Sets the background colors used by the tooltip.
         *
         * @param colors The colors to apply (2 colors, start and end).
         * @return This builder instance.
         */
        public Builder backgroundColors(String... colors) {
            this.entry.backgroundColors.addAll(List.of(colors));
            return this;
        }

        /**
         * Sets the background colors used by the tooltip from a list.
         *
         * @param colors A list of color strings (2 colors, start and end).
         * @return This builder instance.
         */
        public Builder backgroundColors(List<String> colors) {
            this.entry.backgroundColors.addAll(colors);
            return this;
        }

        /**
         * Sets the background type used by the tooltip.
         *
         * @param backgroundType The background type (SOLID, GRADIENT, TEXTURE).
         * @return This builder instance.
         */
        public Builder backgroundType(BackgroundType backgroundType) {
            this.entry.backgroundType = backgroundType;
            return this;
        }

        /**
         * Sets the texture identifier used for TEXTURE background type.
         *
         * @param backgroundTexture The identifier of the texture.
         * @return This builder instance.
         */
        public Builder backgroundTexture(String backgroundTexture) {
            this.entry.backgroundTexture = backgroundTexture;
            return this;
        }

        /**
         * Sets the tooltip's background opacity.
         * <p>
         * Accepts integers from 0 to 255 to adjust the individual tooltip's background opacity. 0 is fully transparent, while 255 is fully opaque.
         *
         * @param backgroundOpacity The background opacity of the tooltip.
         * @return This builder instance.
         */
        public Builder backgroundOpacity(int backgroundOpacity) {
            this.entry.backgroundOpacity = backgroundOpacity;
            return this;
        }

        /**
         * Sets the tooltip's border opacity.
         * <p>
         * Accepts integers from 0 to 255 to adjust the individual tooltip's border opacity. 0 is fully transparent, while 255 is fully opaque.
         *
         * @param borderOpacity The border opacity of the tooltip.
         * @return This builder instance.
         */
        public Builder borderOpacity(int borderOpacity) {
            this.entry.borderOpacity = borderOpacity;
            return this;
        }

        /**
         * Sets the position where the tooltip will be injected.
         *
         * @param position The desired {@link TooltipPosition} (e.g., TOP, BOTTOM, REPLACE_NAME).
         * @return This builder instance.
         */
        public Builder position(TooltipPosition position) {
            this.entry.position = position;
            return this;
        }

        /**
         * Adjusts the specific line index where the tooltip is inserted.
         * Positive values offset downwards, negative values offset upwards.
         *
         * @param lineOffset The amount of lines to offset.
         * @return This builder instance.
         */

        public Builder lineOffset(int lineOffset) {
            this.entry.lineOffset = lineOffset;
            return this;
        }

        /**
         * Applies bold formatting to the tooltip text.
         *
         * @param bold True to make the text bold.
         * @return This builder instance.
         */
        public Builder bold(boolean bold) {
            this.entry.bold = bold;
            return this;
        }

        /**
         * Applies italic formatting to the tooltip text.
         *
         * @param italic True to make the text italic.
         * @return This builder instance.
         */
        public Builder italic(boolean italic) {
            this.entry.italic = italic;
            return this;
        }

        /**
         * Applies an underline to the tooltip text.
         *
         * @param underlined True to underline the text.
         * @return This builder instance.
         */
        public Builder underlined(boolean underlined) {
            this.entry.underlined = underlined;
            return this;
        }

        /**
         * Applies a strikethrough to the tooltip text.
         *
         * @param strikethrough True to strike through the text.
         * @return This builder instance.
         */
        public Builder strikethrough(boolean strikethrough) {
            this.entry.strikethrough = strikethrough;
            return this;
        }

        /**
         * Obfuscates the tooltip text.
         *
         * @param obfuscated True to obfuscate the text.
         * @return This builder instance.
         */
        public Builder obfuscated(boolean obfuscated) {
            this.entry.obfuscated = obfuscated;
            return this;
        }

        /**
         * Makes the tooltip only visible when the player is holding the Shift key.
         *
         * @param requireKeybind True to require the Shift key.
         * @return This builder instance.
         */
        public Builder requireKeybind(boolean requireKeybind) {
            this.entry.require_keybind = requireKeybind;
            return this;
        }

        /**
         * Inserts a blank line before this tooltip for better visual spacing.
         *
         * @param emptyLineBefore True to add an empty line before the text.
         * @return This builder instance.
         */
        public Builder emptyLineBefore(boolean emptyLineBefore) {
            this.entry.empty_line_before = emptyLineBefore;
            return this;
        }

        /**
         * Hides the original vanilla tooltip lines (except the name) before applying this tooltip.
         *
         * @param hideVanillaLines True to hide vanilla lines.
         * @return This builder instance.
         */
        public Builder hideVanillaLines(boolean hideVanillaLines) {
            this.entry.hide_vanilla_lines = hideVanillaLines;
            return this;
        }

        /**
         * Makes the tooltip only visible when the item has lost durability (damaged).
         *
         * @param showOnlyIfDamaged True to require the item to be damaged.
         * @return This builder instance.
         */
        public Builder showOnlyIfDamaged(boolean showOnlyIfDamaged) {
            this.entry.show_only_if_damaged = showOnlyIfDamaged;
            return this;
        }

        /**
         * Makes the tooltip only visible when the item has at least one enchantment.
         *
         * @param showOnlyIfEnchanted True to require the item to be enchanted.
         * @return This builder instance.
         */
        public Builder showOnlyIfEnchanted(boolean showOnlyIfEnchanted) {
            this.entry.show_only_if_enchanted = showOnlyIfEnchanted;
            return this;
        }

        /**
         * Makes the tooltip only visible when the item possesses the "Unbreakable" NBT tag.
         *
         * @param showOnlyIfUnbreakable True to require the item to be unbreakable.
         * @return This builder instance.
         */
        public Builder showOnlyIfUnbreakable(boolean showOnlyIfUnbreakable) {
            this.entry.show_only_if_unbreakable = showOnlyIfUnbreakable;
            return this;
        }

        /**
         * Sets a custom font identifier for the tooltip text.
         *
         * @param fontIdentifier The Identifier of the font (e.g., "minecraft:default", "minecraft:alt").
         * @return This builder instance.
         */
        public Builder font(String fontIdentifier) {
            this.entry.font = fontIdentifier;
            return this;
        }

        /**
         * Sets the animation offset to desynchronize animations across different tooltips or lines.
         *
         * @param offset The animation offset value.
         * @return This builder instance.
         */
        public Builder animationOffset(int offset) {
            this.entry.animation_offset = offset;
            return this;
        }

        /**
         * Sets the animation tickrate (speed).
         * Closer to 0 is faster. Value must be greater than 0.
         *
         * @param tickrate The cycle duration of the animation.
         * @return This builder instance.
         */
        public Builder tickrate(int tickrate) {
            this.entry.tickrate = tickrate;
            return this;
        }

        /**
         * Reverses the flow direction of animated gradients (e.g., Right to Left instead of Left to Right).
         *
         * @param reverse True to reverse the animation direction.
         * @return This builder instance.
         */
        public Builder reverseAnimation(boolean reverse) {
            this.entry.reverse_animation = reverse;
            return this;
        }

        /**
         * Builds and returns the configured {@link TooltipEntry} without registering it.
         * The returned entry must be registered manually using {@link CustomTooltipApi#registerTooltip(TooltipEntry)}.
         *
         * @return The built TooltipEntry.
         */
        public TooltipEntry build() {
            return this.entry;
        }

        /**
         * Builds the TooltipEntry and automatically registers it to the Custom Tooltip API.
         *
         * @return The built and registered TooltipEntry.
         */
        public TooltipEntry register() {
            CustomTooltipApi.registerTooltip(this.entry);
            return this.entry;
        }
    }
}