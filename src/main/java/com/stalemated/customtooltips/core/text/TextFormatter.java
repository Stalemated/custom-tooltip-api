package com.stalemated.customtooltips.core.text;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.api.enums.TooltipStyle;
import com.stalemated.customtooltips.core.text.parser.TextParser;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

public class TextFormatter {

    public static List<Component> getOrGenerateComponents(TooltipEntry entry, ItemStack stack) {
        boolean isStatic = (entry.style == TooltipStyle.SOLID || entry.style == TooltipStyle.STATIC_GRADIENT) && entry.dynamicTextProvider == null && !entry.hasDynamicText();
        if (isStatic && entry.getCachedStaticText() != null) return entry.getCachedStaticText();

        List<Component> linesList = new ArrayList<>();
        List<String> rawText = entry.dynamicTextProvider != null ? entry.dynamicTextProvider.apply(stack) : entry.text;
        if (rawText == null) return linesList;

        List<String> parsedLines = TextParser.parseAll(rawText, stack);

        for (String parsedLine : parsedLines) {
            MutableComponent processedText = StyleApplier.apply(Component.literal(parsedLine), entry);
            StyleApplier.applyModifiers(processedText, entry.getCachedStyleModifier());

            linesList.add(processedText);
        }

        if (isStatic) entry.setCachedStaticText(linesList);
        return linesList;
    }
}