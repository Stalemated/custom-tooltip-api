package com.stalemated.customtooltips.core;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.api.enums.BackgroundType;
import com.stalemated.customtooltips.core.text.StyleApplier;
import com.stalemated.customtooltips.core.text.parser.PlaceholderParser;
import com.stalemated.customtooltips.core.text.parser.TranslationParser;
import com.stalemated.lib.predicate.target.TargetMatcher;
import com.stalemated.lib.predicate.target.TargetMatcherFactory;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.List;

import static com.stalemated.lib.util.color.ColorUtils.*;

/**
 * Encapsulates runtime-only parsed colors, style modifiers, static text caches,
 * and target matchers for a {@link TooltipEntry}, keeping the entry data model clean (SRP).
 */
public final class TooltipRuntimeCache {

    private boolean initialized = false;
    private TargetMatcher targetMatcher = null;
    private int parsedColor1 = DEFAULT_COLOR;
    private int parsedColor2 = DEFAULT_COLOR;
    private boolean gradient = false;
    private List<Text> cachedStaticText = null;
    private Style cachedStyleModifier = null;
    private int parsedBorderColorStart = DEFAULT_BORDER_COLORS.get(0);
    private int parsedBorderColorEnd = DEFAULT_BORDER_COLORS.get(1);
    private int parsedBackgroundColorStart = DEFAULT_BACKGROUND_COLORS.get(0);
    private int parsedBackgroundColorEnd = DEFAULT_BACKGROUND_COLORS.get(1);
    private boolean dynamicText = false;

    public void invalidate() {
        this.initialized = false;
        this.cachedStaticText = null;
        this.cachedStyleModifier = null;
        this.targetMatcher = null;
    }

    public void ensureInitialized(TooltipEntry entry) {
        if (this.initialized) return;

        this.targetMatcher = TargetMatcherFactory.create(entry.target);

        this.gradient = entry.colors != null && entry.colors.size() >= 2;
        this.parsedColor1 = (entry.colors != null && !entry.colors.isEmpty() && entry.colors.get(0) != null)
                ? entry.colors.get(0).getRgb()
                : DEFAULT_COLOR;
        this.parsedColor2 = (this.gradient && entry.colors.get(1) != null)
                ? entry.colors.get(1).getRgb()
                : DEFAULT_COLOR;

        if (entry.tickrate <= 0) {
            entry.tickrate = 100;
        }

        this.cachedStyleModifier = StyleApplier.buildStyleModifier(entry);

        this.dynamicText = false;
        if (entry.text != null) {
            for (String line : entry.text) {
                if (PlaceholderParser.containsDynamicPlaceholders(line) || TranslationParser.containsTranslation(line)) {
                    this.dynamicText = true;
                    break;
                }
            }
        }

        if (entry.borderColors != null && !entry.borderColors.isEmpty()) {
            this.parsedBorderColorStart = entry.borderColors.get(0) != null
                    ? entry.borderColors.get(0).getRGB()
                    : DEFAULT_BORDER_COLORS.get(0);
            this.parsedBorderColorEnd = (entry.borderColors.size() > 1 && entry.borderColors.get(1) != null)
                    ? entry.borderColors.get(1).getRGB()
                    : DEFAULT_BORDER_COLORS.get(1);
        } else {
            this.parsedBorderColorStart = DEFAULT_BORDER_COLORS.get(0);
            this.parsedBorderColorEnd = DEFAULT_BORDER_COLORS.get(1);
        }

        if (entry.backgroundColors != null && !entry.backgroundColors.isEmpty()) {
            this.parsedBackgroundColorStart = entry.backgroundColors.get(0) != null
                    ? entry.backgroundColors.get(0).getRGB()
                    : DEFAULT_BACKGROUND_COLORS.get(0);
            this.parsedBackgroundColorEnd = (entry.backgroundType != BackgroundType.SOLID
                    && entry.backgroundColors.size() > 1
                    && entry.backgroundColors.get(1) != null)
                    ? entry.backgroundColors.get(1).getRGB()
                    : DEFAULT_BACKGROUND_COLORS.get(1);
        } else {
            this.parsedBackgroundColorStart = DEFAULT_BACKGROUND_COLORS.get(0);
            this.parsedBackgroundColorEnd = DEFAULT_BACKGROUND_COLORS.get(1);
        }

        this.initialized = true;
    }

    public TargetMatcher getTargetMatcher(TooltipEntry entry) {
        ensureInitialized(entry);
        return this.targetMatcher;
    }

    public boolean isGradient(TooltipEntry entry) {
        ensureInitialized(entry);
        return this.gradient;
    }

    public int getParsedColor1(TooltipEntry entry) {
        ensureInitialized(entry);
        return this.parsedColor1;
    }

    public int getParsedColor2(TooltipEntry entry) {
        ensureInitialized(entry);
        return this.parsedColor2;
    }

    public Style getCachedStyleModifier(TooltipEntry entry) {
        ensureInitialized(entry);
        return this.cachedStyleModifier;
    }

    public List<Text> getCachedStaticText(TooltipEntry entry) {
        ensureInitialized(entry);
        return this.cachedStaticText;
    }

    public void setCachedStaticText(List<Text> text) {
        this.cachedStaticText = text;
    }

    public int getParsedBorderColorStart(TooltipEntry entry) {
        ensureInitialized(entry);
        return this.parsedBorderColorStart;
    }

    public int getParsedBorderColorEnd(TooltipEntry entry) {
        ensureInitialized(entry);
        return this.parsedBorderColorEnd;
    }

    public int getParsedBackgroundColorStart(TooltipEntry entry) {
        ensureInitialized(entry);
        return this.parsedBackgroundColorStart;
    }

    public int getParsedBackgroundColorEnd(TooltipEntry entry) {
        ensureInitialized(entry);
        return this.parsedBackgroundColorEnd;
    }

    public boolean hasDynamicText(TooltipEntry entry) {
        ensureInitialized(entry);
        return this.dynamicText;
    }
}
