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
import net.minecraft.util.InvalidIdentifierException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class TooltipEntry {

    public enum TooltipStyle {
        SOLID, STATIC_GRADIENT, SLIDE_GRADIENT, BREATHING_GRADIENT, RAINBOW
    }

    public enum TooltipPosition {
        REPLACE_NAME, REPLACE_ALL, TOP, BOTTOM, APPEND, PREPEND
    }

    public String target = "";
    public List<String> text = new ArrayList<>();

    public TooltipStyle style = TooltipStyle.SOLID;
    public List<String> colors = new ArrayList<>();

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

    // Ignored caches
    private transient boolean cachesInitialized = false;
    private transient boolean isTag = false;
    private transient TagKey<Item> cachedTagKey = null;
    private transient Item cachedItem = null;
    private transient int parsedColor1 = 0xFFFFFF;
    private transient int parsedColor2 = 0xFFFFFF;
    private transient boolean isGradient = false;
    private transient List<Text> cachedStaticText = null;
    private transient Style cachedStyleModifier = null;

    public transient boolean apiEntry = false;
    public transient String apiEntryId = "";
    public String uuid;

    public TooltipEntry() {
        this.uuid = UUID.randomUUID().toString();
    }

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

    public void invalidateCaches() {
        this.cachesInitialized = false;
        this.cachedStaticText = null;
        this.cachedStyleModifier = null;
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

        this.cachedStyleModifier = buildStyleModifier();
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

        for (String line : this.text) {
            if (line == null || line.isEmpty()) continue;

            String translatedLine = line.replaceAll("(?i)&([0-9a-fk-or])", "§$1").replace("&&", "&");
            MutableText processedText;
            Text baseText = Text.literal(translatedLine);

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

            if (this.cachedStyleModifier != null && this.cachedStyleModifier != Style.EMPTY) {
                if (!processedText.getSiblings().isEmpty()) {
                    for (Text sibling : processedText.getSiblings()) {
                        ((MutableText) sibling).setStyle(sibling.getStyle().withParent(this.cachedStyleModifier));
                    }
                } else {
                    processedText.setStyle(processedText.getStyle().withParent(this.cachedStyleModifier));
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
        TextColor color = resolveTextColor(colorStr);
        
        if (color != null) style = style.withColor(color);
        else style = style.withColor(Formatting.GRAY);
        
        return textComponent.setStyle(style);
    }

    private int parseColor(String colorStr) {
        TextColor color = resolveTextColor(colorStr);
        return color != null ? color.getRgb() : 0xFFFFFF;
    }

    private TextColor resolveTextColor(String colorStr) {
        if (colorStr == null || colorStr.isEmpty()) return null;

        if (colorStr.length() == 2 && colorStr.charAt(0) == '&') {
            Formatting format = Formatting.byCode(Character.toLowerCase(colorStr.charAt(1)));
            if (format != null && format.getColorValue() != null) {
                return TextColor.fromFormatting(format);
            }
        }

        Formatting format = Formatting.byName(colorStr.toLowerCase(Locale.ROOT));
        if (format != null && format.getColorValue() != null) {
            return TextColor.fromFormatting(format);
        }

        String hex = colorStr;
        if (hex.startsWith("#")) hex = hex.substring(1);
        else if (hex.startsWith("0x") || hex.startsWith("0X")) hex = hex.substring(2);
        else if (hex.startsWith("x") || hex.startsWith("X")) hex = hex.substring(1);

        if (hex.matches("^[0-9a-fA-F]{6}$")) {
            return TextColor.parse("#" + hex);
        }

        return null;
    }

    public int getLineOffset(int size) {
        if (this.position == TooltipPosition.TOP || this.position == TooltipPosition.REPLACE_NAME || this.position == TooltipPosition.APPEND || this.position == TooltipPosition.PREPEND) {
            return Math.max(this.lineOffset, 0) < size ? Math.max(this.lineOffset, 0) : Math.max(size - 1, 0);
        } else {
            return Math.min(this.lineOffset, 0) > (-size) ? Math.min(this.lineOffset, 0) : Math.min(-(size - 1), 0);
        }
    }
}