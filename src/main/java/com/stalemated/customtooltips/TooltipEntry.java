package com.stalemated.customtooltips;

import com.stalemated.customtooltips.api.enums.BackgroundType;
import com.stalemated.customtooltips.api.enums.TooltipPosition;
import com.stalemated.customtooltips.api.enums.TooltipStyle;
import com.stalemated.customtooltips.config.CustomTooltipsConfigRegistry;
import com.stalemated.customtooltips.core.ItemConditions;
import com.stalemated.customtooltips.core.TooltipLook;
import com.stalemated.customtooltips.core.TooltipRuntimeCache;
import com.stalemated.customtooltips.core.position.LinePlacement;
import com.stalemated.customtooltips.core.text.TextFormatter;
import com.stalemated.lib.config.annotation.ItemDropdown;
import com.stalemated.lib.config.annotation.RangeInt;
import com.stalemated.lib.config.annotation.Title;
import com.stalemated.lib.predicate.target.TargetMatcher;
import com.stalemated.lib.util.ResourceIds;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import static com.stalemated.lib.util.color.VanillaTooltipColors.BACKGROUND_OPACITY;

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
    public int borderOpacity = BACKGROUND_OPACITY;

    @Title("background")
    public BackgroundType backgroundType = BackgroundType.SOLID;

    @RangeInt(min = 0, max = 255)
    public int backgroundOpacity = BACKGROUND_OPACITY;

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
    public ResourceLocation font = ResourceIds.of("minecraft", "default");
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

    // Runtime-only API and cache state
    private transient TooltipRuntimeCache runtimeCache;

    public transient boolean apiEntry = false;
    public transient String apiEntryId = "";
    public transient Function<ItemStack, List<String>> dynamicTextProvider = null;
    public transient Predicate<ItemStack> displayCondition = stack -> true;

    public TooltipEntry() {}

    public TooltipRuntimeCache runtimeCache() {
        TooltipRuntimeCache current = this.runtimeCache;
        if (current == null) {
            current = TooltipRuntimeCache.of(this);
            this.runtimeCache = current;
        }
        return current;
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

    public boolean hasCustomBorder() {
        return TooltipLook.setsBorderColors(this) || this.borderOpacity != BACKGROUND_OPACITY;
    }

    public boolean hasCustomBackground() {
        return TooltipLook.setsBackground(this) || this.backgroundOpacity != BACKGROUND_OPACITY;
    }

    public void invalidateCaches() {
        this.runtimeCache = null;
    }

    public void initCaches() {
        runtimeCache();
    }

    public boolean matches(ItemStack stack) {
        if (this.displayCondition != null && !this.displayCondition.test(stack)) {
            return false;
        }
        TargetMatcher matcher = runtimeCache().targetMatcher();
        return matcher != null && matcher.matches(stack);
    }

    public boolean areItemConditionsMet(ItemStack stack) {
        if (this.showOnlyIfDamaged && !stack.isDamaged()) return false;
        if (this.showOnlyIfEnchanted && !stack.isEnchanted()) return false;
        return !this.showOnlyIfUnbreakable || ItemConditions.isUnbreakable(stack);
    }

    public TooltipEntry copy() {
        TooltipEntry clone = CustomTooltipsConfigRegistry.CONFIG.fromJson(CustomTooltipsConfigRegistry.CONFIG.toJson(this), TooltipEntry.class);
        clone.uuid = UUID.randomUUID();
        clone.dynamicTextProvider = this.dynamicTextProvider;
        clone.displayCondition = this.displayCondition;
        return clone;
    }

    public List<Component> getTextComponents(ItemStack stack) {
        return TextFormatter.getOrGenerateComponents(this, stack);
    }

    public int getLineOffset(int size) {
        return LinePlacement.lineOffset(this, size);
    }
}
