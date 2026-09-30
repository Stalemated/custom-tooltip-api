package com.stalemated.customtooltips.core.text;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.lib.util.color.GradientGenerator;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class StyleApplier {

    public static MutableText apply(Text baseText, TooltipEntry entry) {
        switch (entry.style) {
            case RAINBOW:
                return GradientGenerator.getRainbowGradient(baseText, entry.animationOffset, entry.tickrate, entry.reverseAnimation);
            case STATIC_GRADIENT:
                if (entry.isGradient()) return GradientGenerator.getStaticGradient(baseText, entry.getParsedColor1(), entry.getParsedColor2());
                break;
            case SLIDE_GRADIENT:
                if (entry.isGradient()) return GradientGenerator.getSlideGradient(baseText, entry.animationOffset, entry.getParsedColor1(), entry.getParsedColor2(), entry.tickrate, entry.reverseAnimation);
                break;
            case BREATHING_GRADIENT:
                if (entry.isGradient()) return GradientGenerator.getBreathingGradient(baseText, entry.animationOffset, entry.getParsedColor1(), entry.getParsedColor2(), entry.tickrate, entry.reverseAnimation);
                break;
        }

        TextColor textColor = (entry.colors != null && !entry.colors.isEmpty() && entry.colors.get(0) != null) ? entry.colors.get(0) : TextColor.fromFormatting(Formatting.WHITE);
        Style style = Style.EMPTY.withColor(textColor);

        return baseText.copy().setStyle(style);
    }

    public static Style buildStyleModifier(TooltipEntry entry) {
        Style style = Style.EMPTY;
        if (entry.font != null && !entry.font.getPath().isEmpty() && !entry.font.equals(Identifier.of("minecraft", "default"))) {
            style = style.withFont(entry.font);
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
        processedText.setStyle(processedText.getStyle().withParent(cachedStyleModifier));
    }
}