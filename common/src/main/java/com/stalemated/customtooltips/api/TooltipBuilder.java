package com.stalemated.customtooltips.api;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.api.enums.BackgroundType;
import com.stalemated.customtooltips.api.enums.TooltipPosition;
import com.stalemated.customtooltips.api.enums.TooltipStyle;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A fluent builder interface for creating and configuring {@link TooltipEntry} instances.
 * Allows for method chaining to easily set tooltip properties before building or registering.
 */
public interface TooltipBuilder {

    /**
     * Adds a single line of text to the tooltip.
     *
     * @param line The text line to add.
     * @return This builder instance.
     */
    TooltipBuilder addLine(String line);

    /**
     * Adds multiple lines of text to the tooltip.
     *
     * @param lines A list of text lines to add.
     * @return This builder instance.
     */
    TooltipBuilder text(List<String> lines);

    /**
     * Sets a dynamic text provider for this tooltip.
     * This allows the text to change every frame based on the ItemStack's state (e.g., NBT data, enchantments, or any other dynamic properties).
     * Overrides the static text set by {@link #text(List)} or {@link #addLine(String)}.
     *
     * @param provider A function that takes an ItemStack and returns a list of strings.
     * @return This builder instance.
     */
    TooltipBuilder dynamicText(Function<ItemStack, List<String>> provider);

    /**
     * Sets the rendering and animation style of the tooltip.
     *
     * @param style The desired {@link TooltipStyle} (e.g., SOLID, RAINBOW, SLIDE_GRADIENT).
     * @return This builder instance.
     */
    TooltipBuilder style(TooltipStyle style);

    /**
     * Sets the colors used by the tooltip style.
     * <p>
     * Accepts hex codes (e.g., "#FF0000", "0x00FF00", "x0000FF", "FFFFFF"), Minecraft color names (e.g., "red", "blue") or legacy color codes (e.g., "&4", "&c").
     *
     * @param colors The colors to apply (1 or 2 depending on the style).
     * @return This builder instance.
     */
    TooltipBuilder colors(String... colors);

    /**
     * Sets the colors used by the tooltip style from a list.
     * <p>
     * Accepts RGB hex codes (e.g., "#FF0000", "0x00FF00", "x0000FF", "FFFFFF"), Minecraft color names (e.g., "red", "blue") or legacy color codes (e.g., "&4", "&c").
     *
     * @param colors A list of color strings.
     * @return This builder instance.
     */
    TooltipBuilder colors(List<String> colors);

    /**
     * Sets the border colors used by the tooltip.
     * <p>
     * Accepts ARGB hex codes (e.g., "#FAFF0000", "0x8000FF00", "xFF0000FF", "DDFFFFFF"), Minecraft color names (e.g., "red", "blue") or legacy color codes (e.g., "&4", "&c").
     *
     * @param colors The colors to apply (2 colors, start and end).
     * @return This builder instance.
     */
    TooltipBuilder borderColors(String... colors);

    /**
     * Sets the border colors used by the tooltip from a list.
     * <p>
     * Accepts ARGB hex codes (e.g., "#FAFF0000", "0x8000FF00", "xFF0000FF", "DDFFFFFF"), Minecraft color names (e.g., "red", "blue") or legacy color codes (e.g., "&4", "&c").
     *
     * @param colors A list of color strings (2 colors, start and end).
     * @return This builder instance.
     */
    TooltipBuilder borderColors(List<String> colors);

    /**
     * Sets the background colors used by the tooltip.
     * <p>
     * Accepts ARGB hex codes (e.g., "#FAFF0000", "0x8000FF00", "xFF0000FF", "DDFFFFFF"), Minecraft color names (e.g., "red", "blue") or legacy color codes (e.g., "&4", "&c").
     *
     * @param colors The colors to apply (2 colors, start and end).
     * @return This builder instance.
     */
    TooltipBuilder backgroundColors(String... colors);

    /**
     * Sets the background colors used by the tooltip from a list.
     * <p>
     * Accepts ARGB hex codes (e.g., "#FAFF0000", "0x8000FF00", "xFF0000FF", "DDFFFFFF"), Minecraft color names (e.g., "red", "blue") or legacy color codes (e.g., "&4", "&c").
     *
     * @param colors A list of color strings (2 colors, start and end).
     * @return This builder instance.
     */
    TooltipBuilder backgroundColors(List<String> colors);

    /**
     * Sets the background type used by the tooltip.
     *
     * @param backgroundType The background type (SOLID, GRADIENT, TEXTURE).
     * @return This builder instance.
     */
    TooltipBuilder backgroundType(BackgroundType backgroundType);

    /**
     * Sets the texture identifier used for TEXTURE, SIMPLE_TEXTURE or REPEATING_TEXTURE background types.
     * <p>
     * The default auto-generated Identifiers use the following path:
     * {@code custom_tooltip_api:textures/gui/tooltip_backgrounds/<IMAGE_NAME>.png}
     *
     * @param backgroundTexture The identifier of the texture.
     * @return This builder instance.
     */
    TooltipBuilder backgroundTexture(String backgroundTexture);

    /**
     * Sets the tooltip's background opacity.
     * <p>
     * Accepts integers from 0 to 255 to adjust the individual tooltip's background opacity. 0 is fully transparent, while 255 is fully opaque.
     *
     * @param backgroundOpacity The background opacity of the tooltip.
     * @return This builder instance.
     */
    TooltipBuilder backgroundOpacity(int backgroundOpacity);

    /**
     * Sets the background scale of the tooltip (for repeating and framed background types).
     *
     * @param backgroundScale The scale percentage (e.g. 100 for native size, 50 for half size).
     * @return This builder instance.
     */
    TooltipBuilder backgroundScale(int backgroundScale);

    /**
     * Sets the tooltip's border opacity.
     * <p>
     * Accepts integers from 0 to 255 to adjust the individual tooltip's border opacity. 0 is fully transparent, while 255 is fully opaque.
     *
     * @param borderOpacity The border opacity of the tooltip.
     * @return This builder instance.
     */
    TooltipBuilder borderOpacity(int borderOpacity);

    /**
     * Sets the position where the tooltip will be injected.
     *
     * @param position The desired {@link TooltipPosition} (e.g., TOP, BOTTOM, REPLACE_NAME).
     * @return This builder instance.
     */
    TooltipBuilder position(TooltipPosition position);

    /**
     * Adjusts the specific line index where the tooltip is inserted.
     * Positive values offset downwards, negative values offset upwards.
     *
     * @param lineOffset The amount of lines to offset.
     * @return This builder instance.
     */
    TooltipBuilder lineOffset(int lineOffset);

    /**
     * Applies bold formatting to the tooltip text.
     *
     * @param bold True to make the text bold.
     * @return This builder instance.
     */
    TooltipBuilder bold(boolean bold);

    /**
     * Applies italic formatting to the tooltip text.
     *
     * @param italic True to make the text italic.
     * @return This builder instance.
     */
    TooltipBuilder italic(boolean italic);

    /**
     * Applies an underline to the tooltip text.
     *
     * @param underlined True to underline the text.
     * @return This builder instance.
     */
    TooltipBuilder underlined(boolean underlined);

    /**
     * Applies a strikethrough to the tooltip text.
     *
     * @param strikethrough True to strike through the text.
     * @return This builder instance.
     */
    TooltipBuilder strikethrough(boolean strikethrough);

    /**
     * Obfuscates the tooltip text.
     *
     * @param obfuscated True to obfuscate the text.
     * @return This builder instance.
     */
    TooltipBuilder obfuscated(boolean obfuscated);

    /**
     * Makes the tooltip only visible when the player is holding the Shift key.
     *
     * @param requireKeybind True to require the Shift key.
     * @return This builder instance.
     */
    TooltipBuilder requireKeybind(boolean requireKeybind);

    /**
     * Inserts a blank line before this tooltip for better visual spacing.
     *
     * @param emptyLineBefore True to add an empty line before the text.
     * @return This builder instance.
     */
    TooltipBuilder emptyLineBefore(boolean emptyLineBefore);

    /**
     * Hides the original vanilla tooltip lines (except the name) before applying this tooltip.
     *
     * @param hideVanillaLines True to hide vanilla lines.
     * @return This builder instance.
     */
    TooltipBuilder hideVanillaLines(boolean hideVanillaLines);

    /**
     * Makes the tooltip only visible when the item has lost durability (damaged).
     *
     * @param showOnlyIfDamaged True to require the item to be damaged.
     * @return This builder instance.
     */
    TooltipBuilder showOnlyIfDamaged(boolean showOnlyIfDamaged);

    /**
     * Makes the tooltip only visible when the item has at least one enchantment.
     *
     * @param showOnlyIfEnchanted True to require the item to be enchanted.
     * @return This builder instance.
     */
    TooltipBuilder showOnlyIfEnchanted(boolean showOnlyIfEnchanted);

    /**
     * Makes the tooltip only visible when the item possesses the "Unbreakable" NBT tag.
     *
     * @param showOnlyIfUnbreakable True to require the item to be unbreakable.
     * @return This builder instance.
     */
    TooltipBuilder showOnlyIfUnbreakable(boolean showOnlyIfUnbreakable);

    /**
     * Sets a custom font identifier for the tooltip text.
     *
     * @param fontIdentifier The Identifier of the font (e.g., "minecraft:default", "minecraft:alt").
     * @return This builder instance.
     */
    TooltipBuilder font(String fontIdentifier);

    /**
     * Sets the animation offset to desynchronize animations across different tooltips or lines.
     *
     * @param offset The animation offset value.
     * @return This builder instance.
     */
    TooltipBuilder animationOffset(int offset);

    /**
     * Sets the animation tickrate (speed).
     * Closer to 0 is faster. Value must be greater than 0.
     *
     * @param tickrate The cycle duration of the animation.
     * @return This builder instance.
     */
    TooltipBuilder tickrate(int tickrate);

    /**
     * Reverses the flow direction of animated gradients (e.g., Right to Left instead of Left to Right).
     *
     * @param reverse True to reverse the animation direction.
     * @return This builder instance.
     */
    TooltipBuilder reverseAnimation(boolean reverse);

    /**
     * Sets a dynamic condition that determines if the tooltip should be displayed.
     *
     * @param condition A predicate that takes the current ItemStack. If it returns false, the tooltip will be hidden.
     * @return This builder instance.
     */
    TooltipBuilder displayCondition(Predicate<ItemStack> condition);

    /**
     * Builds and returns the configured {@link TooltipEntry} without registering it.
     * The returned entry must be registered manually using {@link CustomTooltipApi#registerTooltip(TooltipEntry)}.
     *
     * @return The built TooltipEntry.
     */
    TooltipEntry build();

    /**
     * Builds the TooltipEntry and automatically registers it to the Custom Tooltip API.
     *
     * @return The built and registered TooltipEntry.
     */
    TooltipEntry register();
}
