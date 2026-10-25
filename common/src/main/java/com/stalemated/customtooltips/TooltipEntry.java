package com.stalemated.customtooltips;

import com.stalemated.customtooltips.api.enums.BackgroundType;
import com.stalemated.customtooltips.api.enums.TooltipPosition;
import com.stalemated.customtooltips.api.enums.TooltipStyle;
import com.stalemated.customtooltips.core.TooltipRuntimeCache;
import com.stalemated.customtooltips.core.text.TextFormatter;
import com.stalemated.lib.config.annotation.ItemDropdown;
import com.stalemated.lib.config.annotation.RangeInt;
import com.stalemated.lib.config.annotation.Title;
import com.stalemated.lib.config.model.OptionTree;
import com.stalemated.lib.predicate.target.TargetMatcher;
import com.stalemated.lib.util.math.MathUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.stalemated.lib.util.color.ColorUtils.*;

public class TooltipEntry {

    @Title("target")
    @ItemDropdown()
    public String target = "";

    public List<String> text = new ArrayList<>();

    @Title("style_colors")
    public TooltipStyle style = TooltipStyle.SOLID;

    public List<TextColor> colors = new ArrayList<>();

    @Title("border")
    public List<Color> borderColors = new ArrayList<>();

    @RangeInt(min = 0, max = 255)
    public int borderOpacity = DEFAULT_OPACITY;

    @Title("background")
    public BackgroundType backgroundType = BackgroundType.SOLID;

    @RangeInt(min = 0, max = 255)
    public int backgroundOpacity = DEFAULT_OPACITY;

    public List<Color> backgroundColors = new ArrayList<>();

    public String backgroundTexture = "";

    @RangeInt(min = 10, max = 300, step = 5)
    public int backgroundScale = 100;

    @Title("position_animation")
    public TooltipPosition position = TooltipPosition.BOTTOM;

    public int lineOffset = 0;

    @RangeInt(min = 1, max = 500)
    public int tickrate = 100;

    @RangeInt(min = -100, max = 100)
    public int animationOffset = 0;

    public boolean reverseAnimation = false;

    @Title("formatting")
    public Identifier font = Identifier.of("minecraft", "default");
    public boolean bold = false;
    public boolean italic = false;
    public boolean underlined = false;
    public boolean strikethrough = false;
    public boolean obfuscated = false;

    @Title("conditions")
    public boolean requireKeybind = false;
    public boolean emptyLineBefore = false;
    public boolean hideVanillaLines = false;
    public boolean showOnlyIfDamaged = false;
    public boolean showOnlyIfEnchanted = false;
    public boolean showOnlyIfUnbreakable = false;

    public UUID uuid = UUID.randomUUID();

    // Runtime-only API and cache state (excluded from serialization and OptionTree)
    private transient TooltipRuntimeCache cache = new TooltipRuntimeCache();

    public transient boolean apiEntry = false;
    public transient String apiEntryId = "";
    public transient Function<ItemStack, List<String>> dynamicTextProvider = null;
    public transient Predicate<ItemStack> displayCondition = stack -> true;

    public TooltipEntry() {}

    private TooltipRuntimeCache cache() {
        if (this.cache == null) {
            this.cache = new TooltipRuntimeCache();
        }
        return this.cache;
    }

    public String getIdentifier() {
        if (this.apiEntry && this.apiEntryId != null && !this.apiEntryId.isEmpty()) {
            long hash = this.target.hashCode()
                    + this.text.hashCode()
                    + this.position.toString().hashCode()
                    + this.style.toString().hashCode()
                    + this.colors.hashCode();
            return this.apiEntryId + ":" + hash;
        }
        return this.uuid.toString();
    }

    public boolean isGradient() { return cache().isGradient(this); }
    public int getParsedColor1() { return cache().getParsedColor1(this); }
    public int getParsedColor2() { return cache().getParsedColor2(this); }

    public Style getCachedStyleModifier() { return cache().getCachedStyleModifier(this); }
    public List<Text> getCachedStaticText() { return cache().getCachedStaticText(this); }
    public void setCachedStaticText(List<Text> text) { cache().setCachedStaticText(text); }
    public boolean hasDynamicText() { return cache().hasDynamicText(this); }

    public boolean hasCustomBorder() {
        int start = getParsedBorderColorStart();
        int end = getParsedBorderColorEnd();
        return (!this.borderColors.isEmpty() && !List.of(start, end).equals(DEFAULT_BORDER_COLORS))
                || this.borderOpacity != DEFAULT_OPACITY;
    }

    public int getParsedBorderColorStart() { return cache().getParsedBorderColorStart(this); }
    public int getParsedBorderColorEnd() { return cache().getParsedBorderColorEnd(this); }

    public boolean hasCustomBackground() {
        int start = getParsedBackgroundColorStart();
        int end = getParsedBackgroundColorEnd();
        return this.backgroundOpacity != DEFAULT_OPACITY
                || (!this.backgroundColors.isEmpty() && start != DEFAULT_BACKGROUND_COLORS.get(0) && this.backgroundType == BackgroundType.SOLID)
                || (!this.backgroundColors.isEmpty() && !List.of(start, end).equals(DEFAULT_BACKGROUND_COLORS) && this.backgroundType == BackgroundType.GRADIENT)
                || (this.backgroundType == BackgroundType.TEXTURE && !this.backgroundTexture.isEmpty())
                || (this.backgroundType == BackgroundType.SIMPLE_TEXTURE && !this.backgroundTexture.isEmpty())
                || (this.backgroundType == BackgroundType.REPEATING_TEXTURE && !this.backgroundTexture.isEmpty());
    }

    public int getParsedBackgroundColorStart() { return cache().getParsedBackgroundColorStart(this); }
    public int getParsedBackgroundColorEnd() { return cache().getParsedBackgroundColorEnd(this); }

    public void invalidateCaches() {
        cache().invalidate();
    }

    public void initCaches() {
        cache().ensureInitialized(this);
    }

    public boolean matches(ItemStack stack) {
        if (this.displayCondition != null && !this.displayCondition.test(stack)) {
            return false;
        }
        TargetMatcher matcher = cache().getTargetMatcher(this);
        return matcher != null && matcher.matches(stack);
    }

    public boolean areItemConditionsMet(ItemStack stack) {
        if (this.showOnlyIfDamaged && !stack.isDamaged()) return false;
        if (this.showOnlyIfEnchanted && !stack.hasEnchantments()) return false;
        return !this.showOnlyIfUnbreakable || (stack.hasNbt() && Objects.requireNonNull(stack.getNbt()).getBoolean("Unbreakable"));
    }

    public TooltipEntry copy() {
        TooltipEntry clone = new TooltipEntry();
        OptionTree.of(TooltipEntry.class, TooltipEntry::new).copyAllValues(this, clone);
        clone.uuid = UUID.randomUUID();
        clone.dynamicTextProvider = this.dynamicTextProvider;
        clone.displayCondition = this.displayCondition;
        return clone;
    }

    public List<Text> getTextComponents(ItemStack stack) {
        return TextFormatter.getOrGenerateComponents(this, stack);
    }

    public int getLineOffset(int size) {
        if (size == 0) return 0;
        if (acceptsPositiveOffset()) {
            return MathUtils.clamp(this.lineOffset, 0, size - 1);
        }
        return MathUtils.clamp(this.lineOffset, -(size - 1), 0);
    }

    private boolean acceptsPositiveOffset() {
        return this.position == TooltipPosition.TOP
                || this.position == TooltipPosition.REPLACE_NAME
                || this.position == TooltipPosition.APPEND
                || this.position == TooltipPosition.PREPEND
                || this.position == TooltipPosition.REPLACE_LINE;
    }
}
