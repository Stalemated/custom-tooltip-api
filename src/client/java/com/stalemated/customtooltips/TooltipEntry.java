package com.stalemated.customtooltips;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import elocindev.necronomicon.api.text.TextAPI;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.minecraft.util.InvalidIdentifierException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TooltipEntry {

    public enum TooltipStyle {
        SOLID, STATIC_GRADIENT, SLIDE_GRADIENT, BREATHING_GRADIENT, RAINBOW
    }

    public enum TooltipPosition {
        REPLACE_NAME, REPLACE_ALL, TOP, BOTTOM, APPEND, PREPEND
    }

    public String target = "";
    public List<String> text = new ArrayList<>();

    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.DROPDOWN)
    public TooltipStyle style = TooltipStyle.SOLID;
    public List<String> colors = new ArrayList<>();

    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.DROPDOWN)
    public TooltipPosition position = TooltipPosition.BOTTOM;

    public int lineOffset = 0;

    public boolean bold = false;
    public boolean italic = false;
    public boolean underlined = false;
    public boolean strikethrough = false;
    public boolean obfuscated = false;

    public boolean require_shift = false;
    public boolean empty_line_before = false;

    public int animation_offset = 0;
    public long tickrate = 1;

    // --- Cachés Ignorados ---
    @ConfigEntry.Gui.Excluded private transient boolean cachesInitialized = false;
    @ConfigEntry.Gui.Excluded private transient boolean isTag = false;
    @ConfigEntry.Gui.Excluded private transient TagKey<Item> cachedTagKey = null;
    @ConfigEntry.Gui.Excluded private transient Item cachedItem = null;
    @ConfigEntry.Gui.Excluded private transient int parsedColor1 = 0xFFFFFF;
    @ConfigEntry.Gui.Excluded private transient int parsedColor2 = 0xFFFFFF;
    @ConfigEntry.Gui.Excluded private transient boolean isGradient = false;
    @ConfigEntry.Gui.Excluded private transient List<Text> cachedStaticText = null;

    public TooltipEntry() {}

    public TooltipEntry(String target, List<String> text, TooltipStyle style, List<String> colors, boolean bold, boolean italic, boolean underlined, boolean strikethrough, boolean obfuscated, boolean require_shift, boolean empty_line_before, TooltipPosition position, int lineOffset, int animation_offset, long tickrate) {
        this.target = target;
        this.text = text != null ? text : new ArrayList<>();
        this.style = style;
        this.colors = colors != null ? colors : new ArrayList<>();
        this.bold = bold;
        this.italic = italic;
        this.underlined = underlined;
        this.strikethrough = strikethrough;
        this.obfuscated = obfuscated;
        this.require_shift = require_shift;
        this.empty_line_before = empty_line_before;
        this.position = position;
        this.lineOffset = lineOffset;
        this.animation_offset = animation_offset;
        this.tickrate = tickrate;
    }

    public void invalidateCaches() {
        this.cachesInitialized = false;
        this.cachedStaticText = null;
    }

    public void initCaches() {
        if (cachesInitialized) return;

        if (this.target != null && !this.target.isEmpty()) {
            try {
                if (this.target.startsWith("#")) {
                    this.isTag = true;
                    this.cachedTagKey = TagKey.of(RegistryKeys.ITEM, new Identifier(this.target.substring(1)));
                } else {
                    this.isTag = false;
                    this.cachedItem = Registries.ITEM.get(new Identifier(this.target));
                }
            } catch (InvalidIdentifierException e) {
                this.cachedItem = null;
                this.cachedTagKey = null;
            }
        }

        this.isGradient = this.colors != null && this.colors.size() >= 2;
        this.parsedColor1 = (this.colors != null && !this.colors.isEmpty()) ? parseColor(this.colors.get(0)) : 0xFFFFFF;
        this.parsedColor2 = this.isGradient ? parseColor(this.colors.get(1)) : 0xFFFFFF;
        if (this.tickrate <= 0) this.tickrate = 1;

        this.cachesInitialized = true;
    }

    public boolean matches(ItemStack stack) {
        if (!cachesInitialized) initCaches();

        if (this.isTag && this.cachedTagKey != null) {
            return stack.isIn(this.cachedTagKey);
        } else if (!this.isTag && this.cachedItem != null) {
            return stack.isOf(this.cachedItem);
        }

        return false;
    }

    public List<Text> getTextComponents() {
        if (!cachesInitialized) initCaches();

        boolean isStatic = this.style == TooltipStyle.SOLID || this.style == TooltipStyle.STATIC_GRADIENT;
        if (isStatic && this.cachedStaticText != null) {
            return this.cachedStaticText;
        }

        List<Text> linesList = new ArrayList<>();
        if (this.text == null) return linesList;

        Style textModifiers = buildStyleModifier();

        for (String line : this.text) {
            MutableText processedText;
            Text baseText = Text.literal(line);

            if (this.style == TooltipStyle.RAINBOW) {
                processedText = TextAPI.Styles.getRainbowGradient(baseText, this.animation_offset, this.tickrate);
            } else if (this.style == TooltipStyle.STATIC_GRADIENT && this.isGradient) {
                processedText = TextAPI.Styles.getStaticGradient(baseText, this.parsedColor1, this.parsedColor2);
            } else if (this.style == TooltipStyle.SLIDE_GRADIENT && this.isGradient) {
                processedText = TextAPI.Styles.getGradient(baseText, this.animation_offset, this.parsedColor1, this.parsedColor2, this.tickrate);
            } else if (this.style == TooltipStyle.BREATHING_GRADIENT && this.isGradient) {
                processedText = TextAPI.Styles.getBreathingGradient(baseText, this.animation_offset, this.parsedColor1, this.parsedColor2, this.tickrate);
            } else {
                String colorStr = (this.colors != null && !this.colors.isEmpty()) ? this.colors.get(0) : "gray";
                processedText = applySolidStyle(baseText.copy(), colorStr);
            }

            if (textModifiers != Style.EMPTY) {
                if (!processedText.getSiblings().isEmpty()) {
                    for (Text sibling : processedText.getSiblings()) {
                        ((MutableText) sibling).setStyle(sibling.getStyle().withParent(textModifiers));
                    }
                } else {
                    processedText.setStyle(processedText.getStyle().withParent(textModifiers));
                }
            }

            linesList.add(processedText);
        }

        if (isStatic) this.cachedStaticText = linesList;
        return linesList;
    }

    private Style buildStyleModifier() {
        Style style = Style.EMPTY;
        if (this.bold) style = style.withBold(true);
        if (this.italic) style = style.withItalic(true);
        if (this.underlined) style = style.withUnderline(true);
        if (this.strikethrough) style = style.withStrikethrough(true);
        if (this.obfuscated) style = style.withObfuscated(true);
        return style;
    }

    private MutableText applySolidStyle(MutableText textComponent, String colorStr) {
        Style style = Style.EMPTY;
        if (colorStr != null && !colorStr.isEmpty()) {
            if (colorStr.startsWith("#")) {
                style = style.withColor(TextColor.parse(colorStr));
            } else {
                Formatting format = Formatting.byName(colorStr.toLowerCase(Locale.ROOT));
                if (format != null) {
                    style = style.withFormatting(format);
                } else {
                    style = style.withColor(TextColor.parse(colorStr));
                }
            }
        }
        return textComponent.setStyle(style);
    }

    private int parseColor(String colorStr) {
        if (colorStr.startsWith("#")) {
            try {
                return Integer.parseInt(colorStr.substring(1), 16);
            } catch (NumberFormatException e) {
                return 0xFFFFFF;
            }
        } else {
            Formatting format = Formatting.byName(colorStr.toLowerCase(Locale.ROOT));
            if (format != null && format.getColorValue() != null) {
                return format.getColorValue();
            }
            return 0xFFFFFF;
        }
    }

    public int getLineOffset(int size) {
        if (this.position == TooltipPosition.TOP || this.position == TooltipPosition.REPLACE_NAME || this.position == TooltipPosition.APPEND || this.position == TooltipPosition.PREPEND) {
            return Math.max(this.lineOffset, 0) < size ? Math.max(this.lineOffset, 0) : Math.max(size - 1, 0);
        } else {
            return Math.min(this.lineOffset, 0) > (-size) ? Math.min(this.lineOffset, 0) : Math.min(-(size - 1), 0);
        }
    }
}