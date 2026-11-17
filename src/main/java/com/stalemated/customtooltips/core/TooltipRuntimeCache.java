package com.stalemated.customtooltips.core;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.api.enums.BackgroundType;
import com.stalemated.customtooltips.core.text.StyleApplier;
import com.stalemated.customtooltips.core.text.TooltipTextParser;
import com.stalemated.lib.predicate.target.TargetMatcher;
import com.stalemated.lib.predicate.target.TargetMatcherFactory;
import java.awt.Color;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import static com.stalemated.customtooltips.CustomTooltips.LOGGER;
import static com.stalemated.lib.util.color.VanillaTooltipColors.BACKGROUND_GRADIENT;
import static com.stalemated.lib.util.color.VanillaTooltipColors.BORDER;
import static com.stalemated.lib.util.color.VanillaTooltipColors.TEXT;

/**
 * Encapsulates runtime-only parsed colors, style modifiers, static text caches,
 * and target matchers for a {@link TooltipEntry}, keeping the entry data model clean (SRP).
 */
public record TooltipRuntimeCache(
        TargetMatcher targetMatcher,
        boolean gradient,
        int primaryColor,
        int secondaryColor,
        int tickrate,
        Style styleModifier,
        boolean dynamicText,
        int borderColorStart,
        int borderColorEnd,
        int backgroundColorStart,
        int backgroundColorEnd,
        AtomicReference<List<Component>> staticText
) {
    private static final int DEFAULT_TICKRATE = 100;

    public static TooltipRuntimeCache of(TooltipEntry entry) {
        TargetMatcher targetMatcher = TargetMatcherFactory.tryCreate(entry.target).orElse(null);
        if (targetMatcher == null) LOGGER.warn("Tooltip target '{}' is invalid, the entry will not match any item", entry.target);

        boolean gradient = entry.colors != null && entry.colors.size() >= 2;
        int primaryColor = entry.colors != null && !entry.colors.isEmpty() && entry.colors.get(0) != null ? entry.colors.get(0).getValue() : TEXT;
        int secondaryColor = gradient && entry.colors.get(1) != null ? entry.colors.get(1).getValue() : TEXT;
        int backgroundColorEnd = entry.backgroundType != BackgroundType.SOLID ? colorAt(entry.backgroundColors, 1, BACKGROUND_GRADIENT.get(1)) : BACKGROUND_GRADIENT.get(1);

        return new TooltipRuntimeCache(
                targetMatcher,
                gradient,
                primaryColor,
                secondaryColor,
                entry.tickrate > 0 ? entry.tickrate : DEFAULT_TICKRATE,
                StyleApplier.buildStyleModifier(entry),
                entry.text != null && entry.text.stream().anyMatch(TooltipTextParser::isDynamic),
                colorAt(entry.borderColors, 0, BORDER.get(0)),
                colorAt(entry.borderColors, 1, BORDER.get(1)),
                colorAt(entry.backgroundColors, 0, BACKGROUND_GRADIENT.get(0)),
                backgroundColorEnd,
                new AtomicReference<>()
        );
    }

    private static int colorAt(List<Color> colors, int index, int fallback) {
        return colors != null && colors.size() > index && colors.get(index) != null ? colors.get(index).getRGB() : fallback;
    }
}
