package com.stalemated.customtooltips.core.text;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.lib.util.ResourceIds;
import com.stalemated.lib.util.color.GradientGenerator;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

public class StyleApplier {

    public static MutableComponent apply(Component baseText, TooltipEntry entry) {
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

        TextColor textColor = (entry.colors != null && !entry.colors.isEmpty() && entry.colors.get(0) != null) ? entry.colors.get(0) : TextColor.fromLegacyFormat(ChatFormatting.WHITE);
        Style style = Style.EMPTY.withColor(textColor);

        return baseText.copy().setStyle(style);
    }

    public static Style buildStyleModifier(TooltipEntry entry) {
        Style style = Style.EMPTY;
        if (entry.font != null && !entry.font.getPath().isEmpty() && !entry.font.equals(ResourceIds.of("minecraft", "default"))) {
            style = style.withFont(entry.font);
        }
        if (entry.bold) style = style.withBold(true);
        if (entry.italic) style = style.withItalic(true);
        if (entry.underlined) style = style.withUnderlined(true);
        if (entry.strikethrough) style = style.withStrikethrough(true);
        if (entry.obfuscated) style = style.withObfuscated(true);
        return style;
    }

    public static void applyModifiers(MutableComponent processedText, Style cachedStyleModifier) {
        if (cachedStyleModifier == null || cachedStyleModifier == Style.EMPTY) return;
        processedText.setStyle(processedText.getStyle().applyTo(cachedStyleModifier));
    }
}