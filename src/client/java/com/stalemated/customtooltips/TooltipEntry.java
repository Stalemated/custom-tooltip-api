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
        TOP, BOTTOM
    }

    public String target = "";
    public List<String> text = new ArrayList<>();

    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public TooltipStyle style = TooltipStyle.SOLID;
    public List<String> colors = new ArrayList<>();

    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public TooltipPosition position = TooltipPosition.BOTTOM;

    public boolean bold = false;
    public boolean italic = false;
    public boolean underlined = false;
    public boolean strikethrough = false;
    public boolean obfuscated = false;

    public boolean require_shift = false;
    public boolean empty_line_before = false;

    public int offset = 0;
    public long tickrate = 1;

    @ConfigEntry.Gui.Excluded
    private transient boolean cachesInitialized = false;
    @ConfigEntry.Gui.Excluded
    private transient boolean isTag = false;
    @ConfigEntry.Gui.Excluded
    private transient TagKey<Item> cachedTagKey = null;
    @ConfigEntry.Gui.Excluded
    private transient Identifier cachedItemId = null;
    @ConfigEntry.Gui.Excluded
    private transient int parsedColor1 = 0xFFFFFF;
    @ConfigEntry.Gui.Excluded
    private transient int parsedColor2 = 0xFFFFFF;
    @ConfigEntry.Gui.Excluded
    private transient boolean isGradient = false;
    @ConfigEntry.Gui.Excluded
    private transient List<Text> cachedStaticText = null;

    public TooltipEntry() {}

    public TooltipEntry(String target, List<String> text, TooltipStyle style, List<String> colors, boolean bold, boolean italic, boolean underlined, boolean strikethrough, boolean obfuscated, boolean require_shift, boolean empty_line_before, TooltipPosition position, int offset, long tickrate) {
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
        this.offset = offset;
        this.tickrate = tickrate;
    }

    private void initCaches() {
        if (cachesInitialized) return;

        if (this.target != null && !this.target.isEmpty()) {
            try {
                if (this.target.startsWith("#")) {
                    this.isTag = true;
                    this.cachedTagKey = TagKey.of(RegistryKeys.ITEM, new Identifier(this.target.substring(1)));
                } else {
                    this.cachedItemId = new Identifier(this.target);
                }
            } catch (InvalidIdentifierException e) {
                this.cachedItemId = null;
                this.cachedTagKey = null;
            }
        }

        this.isGradient = this.colors != null && this.colors.size() >= 2;
        this.parsedColor1 = (this.colors != null && !this.colors.isEmpty()) ? parseColor(this.colors.get(0)) : 0xFFFFFF;
        this.parsedColor2 = this.isGradient ? parseColor(this.colors.get(1)) : 0xFFFFFF;
        if (this.tickrate == 0) this.tickrate = 1;

        this.cachesInitialized = true;
    }

    public boolean matches(ItemStack stack) {
        initCaches();

        if (this.isTag && this.cachedTagKey != null) {
            return stack.isIn(this.cachedTagKey);
        } else if (!this.isTag && this.cachedItemId != null) {
            return Registries.ITEM.getId(stack.getItem()).equals(this.cachedItemId);
        }

        return false;
    }

    public List<Text> getTextComponents() {
        initCaches();

        boolean isStatic = this.style == TooltipStyle.SOLID || this.style == TooltipStyle.STATIC_GRADIENT;
        if (isStatic && this.cachedStaticText != null) {
            return this.cachedStaticText;
        }

        List<Text> linesList = new ArrayList<>();
        if (this.text == null) return linesList;

        for (String line : this.text) {
            MutableText processedText;
            Text baseText = Text.literal(line);

            if (this.style == TooltipStyle.RAINBOW) {
                processedText = TextAPI.Styles.getRainbowGradient(baseText, this.offset, this.tickrate);

            } else if (this.style == TooltipStyle.STATIC_GRADIENT && this.isGradient) {
                processedText = TextAPI.Styles.getStaticGradient(baseText, this.parsedColor1, this.parsedColor2);

            } else if (this.style == TooltipStyle.SLIDE_GRADIENT && this.isGradient) {
                processedText = TextAPI.Styles.getGradient(baseText, this.offset, this.parsedColor1, this.parsedColor2, this.tickrate);

            } else if (this.style == TooltipStyle.BREATHING_GRADIENT && this.isGradient) {
                processedText = TextAPI.Styles.getBreathingGradient(baseText, this.offset, this.parsedColor1, this.parsedColor2, this.tickrate);

            } else {
                String colorStr = (this.colors != null && !this.colors.isEmpty()) ? this.colors.get(0) : "gray";
                processedText = applySolidStyle(Text.literal(line), colorStr);
            }

            if (this.bold || this.italic || this.underlined || this.strikethrough || this.obfuscated) {
                Style modifier = Style.EMPTY;
                if (this.bold) modifier = modifier.withBold(true);
                if (this.italic) modifier = modifier.withItalic(true);
                if (this.underlined) modifier = modifier.withUnderline(true);
                if (this.strikethrough) modifier = modifier.withStrikethrough(true);
                if (this.obfuscated) modifier = modifier.withObfuscated(true);

                if (!processedText.getSiblings().isEmpty()) {
                    for (Text sibling : processedText.getSiblings()) {
                        ((MutableText) sibling).setStyle(sibling.getStyle().withParent(modifier));
                    }
                } else {
                    processedText.setStyle(processedText.getStyle().withParent(modifier));
                }
            }

            linesList.add(processedText);
        }

        if (isStatic) {
            this.cachedStaticText = linesList;
        }

        return linesList;
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
}