package com.stalemated.customtooltips.core;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.api.enums.BackgroundType;
import com.stalemated.customtooltips.core.text.StyleApplier;
import com.stalemated.customtooltips.core.text.TooltipTextParser;
import com.stalemated.lib.predicate.target.TargetMatcher;
import com.stalemated.lib.predicate.target.TargetMatcherFactory;
import com.stalemated.lib.util.ResourceIds;
import java.awt.Color;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

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
        ResourceLocation backgroundTexture,
        ResourceLocation backgroundSprite,
        AtomicReference<List<Component>> staticText
) {
    private static final int DEFAULT_TICKRATE = 100;
    private static final Set<BackgroundType> TEXTURED = EnumSet.of(BackgroundType.SIMPLE_TEXTURE, BackgroundType.TEXTURE, BackgroundType.REPEATING_TEXTURE);

    public static TooltipRuntimeCache of(TooltipEntry entry) {
        TargetMatcher targetMatcher = TargetMatcherFactory.tryCreate(entry.target).orElse(null);
        if (targetMatcher == null) LOGGER.warn("Tooltip target '{}' is invalid, the entry will not match any item", entry.target);

        boolean gradient = entry.colors != null && entry.colors.size() >= 2;
        int primaryColor = entry.colors != null && !entry.colors.isEmpty() && entry.colors.get(0) != null ? entry.colors.get(0).getValue() : TEXT;
        int secondaryColor = gradient && entry.colors.get(1) != null ? entry.colors.get(1).getValue() : TEXT;
        int backgroundColorEnd = entry.backgroundType != BackgroundType.SOLID ? colorAt(entry.backgroundColors, 1, BACKGROUND_GRADIENT.get(1)) : BACKGROUND_GRADIENT.get(1);
        ResourceLocation backgroundTexture = backgroundTextureOf(entry);

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
                backgroundTexture,
                backgroundTexture != null ? atlasSpriteOf(entry.backgroundTexture) : null,
                new AtomicReference<>()
        );
    }

    private static ResourceLocation backgroundTextureOf(TooltipEntry entry) {
        if (!TEXTURED.contains(entry.backgroundType) || entry.backgroundTexture == null || entry.backgroundTexture.isEmpty()) return null;
        if (ResourceIds.isValid(entry.backgroundTexture)) return ResourceIds.parse(entry.backgroundTexture);

        LOGGER.warn("Tooltip background texture '{}' is invalid, the entry fills its background with its color instead", entry.backgroundTexture);
        return null;
    }

    private static ResourceLocation atlasSpriteOf(String backgroundTexture) {
        return ResourceIds.parse(backgroundTexture.replace(".png", "").replace("textures/", ""));
    }

    private static int colorAt(List<Color> colors, int index, int fallback) {
        return colors != null && colors.size() > index && colors.get(index) != null ? colors.get(index).getRGB() : fallback;
    }
}
