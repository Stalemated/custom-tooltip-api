package com.stalemated.customtooltips.core.text;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.util.ColorUtils;
import elocindev.necronomicon.api.text.TextAPI;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

public class StyleApplier {

    public static MutableText apply(Text baseText, TooltipEntry entry) {
        switch (entry.style) {
            case RAINBOW:
                return TextAPI.Styles.getRainbowGradient(baseText, entry.animation_offset, entry.tickrate);
            case STATIC_GRADIENT:
                if (entry.isGradient()) return TextAPI.Styles.getStaticGradient(baseText, entry.getParsedColor1(), entry.getParsedColor2());
                break;
            case SLIDE_GRADIENT:
                if (entry.isGradient()) return TextAPI.Styles.getGradient(baseText, entry.animation_offset, entry.getParsedColor1(), entry.getParsedColor2(), entry.tickrate);
                break;
            case BREATHING_GRADIENT:
                if (entry.isGradient()) return TextAPI.Styles.getBreathingGradient(baseText, entry.animation_offset, entry.getParsedColor1(), entry.getParsedColor2(), entry.tickrate);
                break;
        }

        String colorStr = (entry.colors != null && !entry.colors.isEmpty()) ? entry.colors.get(0) : "white";
        Style style = Style.EMPTY.withColor(ColorUtils.resolveTextColor(colorStr) != null ? ColorUtils.resolveTextColor(colorStr) : TextColor.fromFormatting(Formatting.WHITE));

        return baseText.copy().setStyle(style);
    }

    public static Style buildStyleModifier(TooltipEntry entry) {
        Style style = Style.EMPTY;
        if (entry.font != null && !entry.font.isEmpty() && !entry.font.equals("minecraft:default")) {
            try {
                style = style.withFont(new Identifier(entry.font));
            }
            catch (InvalidIdentifierException ignored) {}
        }
        if (entry.bold) style = style.withBold(true);
        if (entry.italic) style = style.withItalic(true);
        if (entry.underlined) style = style.withUnderline(true);
        if (entry.strikethrough) style = style.withStrikethrough(true);
        if (entry.obfuscated) style = style.withObfuscated(true);
        return style;
    }

    public static void applyModifiers(MutableText processedText, Style cachedStyleModifier) {
        if (cachedStyleModifier == null || cachedStyleModifier == Style.EMPTY) return;
        if (!processedText.getSiblings().isEmpty()) {
            for (Text sibling : processedText.getSiblings()) {
                if (sibling instanceof MutableText mutableSibling) mutableSibling.setStyle(sibling.getStyle().withParent(cachedStyleModifier));
            }
        } else {
            processedText.setStyle(processedText.getStyle().withParent(cachedStyleModifier));
        }
    }
}