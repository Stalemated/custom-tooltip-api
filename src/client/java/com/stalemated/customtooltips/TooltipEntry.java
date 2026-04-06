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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TooltipEntry {
    public String target = "";
    public String[] text = new String[]{"Default text"};

    // Available styles: SOLID, STATIC_GRADIENT, SLIDE_GRADIENT, BREATHING_GRADIENT, RAINBOW
    public String style = "SOLID";
    public String[] colors = new String[]{"gray"};

    // Available positions: TOP, BOTTOM
    public String position = "BOTTOM";

    public boolean bold = false;
    public boolean italic = false;
    public boolean underlined = false;
    public boolean strikethrough = false;
    public boolean obfuscated = false;

    public boolean require_shift = false;
    public boolean empty_line_before = false;

    public int offset = 0;
    public long tickrate = 1;

    public static final Logger LOGGER = LoggerFactory.getLogger("Custom Tooltip API");

    public TooltipEntry() {}

    public TooltipEntry(String target, String[] text, String style, String[] colors, boolean bold, boolean italic, boolean underlined, boolean strikethrough, boolean obfuscated, boolean require_shift, boolean empty_line_before, String position, int offset, long tickrate) {
        this.target = target;
        this.text = text;
        this.style = style;
        this.colors = colors;
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

    public boolean matches(ItemStack stack) {
        if (target == null || target.isEmpty()) return false;

        try {
            if (target.startsWith("#")) {
                Identifier id = new Identifier(target.substring(1));
                TagKey<Item> tagKey = TagKey.of(RegistryKeys.ITEM, id);
                return stack.isIn(tagKey);
            } else {
                Identifier id = new Identifier(target);
                return Registries.ITEM.getId(stack.getItem()).equals(id);
            }
        } catch (Exception e) {
            return false;
        }
    }

    public List<Text> getTextComponents() {
        List<Text> linesList = new ArrayList<>();
        if (this.text == null) return linesList;

        for (String line : this.text) {
            MutableText processedText;
            Text baseText = Text.literal(line);

            boolean isGradient = this.colors != null && this.colors.length >= 2;

            int color1 = isGradient ? parseColor(this.colors[0]) : 0xFFFFFF;
            int color2 = isGradient ? parseColor(this.colors[1]) : 0xFFFFFF;

            this.tickrate = Math.max(1, this.tickrate);

            if ("RAINBOW".equalsIgnoreCase(this.style)) {
                processedText = TextAPI.Styles.getRainbowGradient(baseText, this.offset, this.tickrate);

            } else if ("STATIC_GRADIENT".equalsIgnoreCase(this.style) && isGradient) {
                processedText = TextAPI.Styles.getStaticGradient(baseText, color1, color2);

            } else if ("SLIDE_GRADIENT".equalsIgnoreCase(this.style) && isGradient) {
                processedText = TextAPI.Styles.getGradient(baseText, this.offset, color1, color2, this.tickrate);

            } else if ("BREATHING_GRADIENT".equalsIgnoreCase(this.style) && isGradient) {
                processedText = TextAPI.Styles.getBreathingGradient(baseText, this.offset, color1, color2, this.tickrate);

            } else {
                // Caída (Fallback) a SOLID color clásico
                String colorStr = (this.colors != null && this.colors.length > 0) ? this.colors[0] : "gray";
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