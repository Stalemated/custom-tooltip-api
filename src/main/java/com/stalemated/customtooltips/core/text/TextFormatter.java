package com.stalemated.customtooltips.core.text;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.api.enums.TooltipStyle;
import com.stalemated.customtooltips.core.TooltipRuntimeCache;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

public class TextFormatter {

    public static List<Component> getOrGenerateComponents(TooltipEntry entry, ItemStack stack) {
        TooltipRuntimeCache cache = entry.runtimeCache();
        boolean isStatic = (entry.style == TooltipStyle.SOLID || entry.style == TooltipStyle.STATIC_GRADIENT) && entry.dynamicTextProvider == null && !cache.dynamicText();
        List<Component> cachedText = cache.staticText().get();
        if (isStatic && cachedText != null) return cachedText;

        List<Component> linesList = new ArrayList<>();
        List<String> rawText = entry.dynamicTextProvider != null ? entry.dynamicTextProvider.apply(stack) : entry.text;
        if (rawText == null) return linesList;

        List<String> parsedLines = TooltipTextParser.parseLines(rawText, stack);

        for (String parsedLine : parsedLines) {
            MutableComponent processedText = StyleApplier.apply(Component.literal(parsedLine), entry);
            StyleApplier.applyModifiers(processedText, cache.styleModifier());

            linesList.add(processedText);
        }

        if (isStatic) cache.staticText().set(linesList);
        return linesList;
    }
}
