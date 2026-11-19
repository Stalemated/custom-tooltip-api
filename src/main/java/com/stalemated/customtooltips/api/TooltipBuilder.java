package com.stalemated.customtooltips.api;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.api.enums.BackgroundType;
import com.stalemated.customtooltips.api.enums.TooltipPosition;
import com.stalemated.customtooltips.api.enums.TooltipStyle;
import com.stalemated.lib.util.ResourceIds;
import com.stalemated.lib.util.color.ColorUtils;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

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
     *
     * @param colors The colors to apply (1 or 2 depending on the style).
     * @return This builder instance.
     */
    TooltipBuilder colors(TextColor... colors);

    /**
     * Sets the colors used by the tooltip style from a list.
     *
     * @param colors A list of colors.
     * @return This builder instance.
     */
    TooltipBuilder colorsList(List<TextColor> colors);

    /**
     * Sets the colors used by the tooltip style.
     * <p>
     * Accepts hex codes (e.g., "#FF0000", "0x00FF00", "x0000FF", "FFFFFF"), Minecraft color names (e.g., "red", "blue") or legacy color codes (e.g., "&4", "&c").
     *
     * @param colors The colors to apply (1 or 2 depending on the style).
     * @return This builder instance.
     * @throws IllegalArgumentException If a color cannot be read, with that color in the message.
     */
    default TooltipBuilder colors(String... colors) {
        return colorsList(Arrays.stream(colors).map(TooltipBuilder::requireTextColor).toList());
    }

    /**
     * Sets the colors used by the tooltip style from a list.
     * <p>
     * Accepts hex codes (e.g., "#FF0000", "0x00FF00", "x0000FF", "FFFFFF"), Minecraft color names (e.g., "red", "blue") or legacy color codes (e.g., "&4", "&c").
     *
     * @param colors A list of colors strings.
     * @return This builder instance.
     * @throws IllegalArgumentException If a color cannot be read, with that color in the message.
     */
    default TooltipBuilder colors(List<String> colors) {
        return colorsList(colors.stream().map(TooltipBuilder::requireTextColor).toList());
    }

    /**
     * Sets the border colors used by the tooltip.
     *
     * @param colors The colors to apply (2 colors, start and end).
     * @return This builder instance.
     */
    TooltipBuilder borderColors(Color... colors);

    /**
     * Sets the border colors used by the tooltip from a list.
     *
     * @param colors A list of colors (2 colors, start and end).
     * @return This builder instance.
     */
    TooltipBuilder borderColorsList(List<Color> colors);

    /**
     * Sets the border colors used by the tooltip.
     * <p>
     * Accepts ARGB hex codes (e.g., "#FAFF0000", "0x8000FF00", "xFF0000FF", "DDFFFFFF"), Minecraft color names (e.g., "red", "blue") or legacy color codes (e.g., "&4", "&c").
     *
     * @param colors The colors to apply (2 colors, start and end).
     * @return This builder instance.
     * @throws IllegalArgumentException If a color cannot be read, with that color in the message.
     */
    default TooltipBuilder borderColors(String... colors) {
        return borderColorsList(Arrays.stream(colors).map(color -> requireArgb("border color", color)).toList());
    }

    /**
     * Sets the border colors used by the tooltip from a list.
     * <p>
     * Accepts ARGB hex codes (e.g., "#FAFF0000", "0x8000FF00", "xFF0000FF", "DDFFFFFF"), Minecraft color names (e.g., "red", "blue") or legacy color codes (e.g., "&4", "&c").
     *
     * @param colors A list of color strings (2 colors, start and end).
     * @return This builder instance.
     * @throws IllegalArgumentException If a color cannot be read, with that color in the message.
     */
    default TooltipBuilder borderColors(List<String> colors) {
        return borderColorsList(colors.stream().map(color -> requireArgb("border color", color)).toList());
    }

    /**
     * Sets the background colors used by the tooltip.
     *
     * @param colors The colors to apply (2 colors, start and end).
     * @return This builder instance.
     */
    TooltipBuilder backgroundColors(Color... colors);

    /**
     * Sets the background colors used by the tooltip from a list.
     *
     * @param colors A list of colors (2 colors, start and end).
     * @return This builder instance.
     */
    TooltipBuilder backgroundColorsList(List<Color> colors);

    /**
     * Sets the background colors used by the tooltip.
     * <p>
     * Accepts ARGB hex codes (e.g., "#FAFF0000", "0x8000FF00", "xFF0000FF", "DDFFFFFF"), Minecraft color names (e.g., "red", "blue") or legacy color codes (e.g., "&4", "&c").
     *
     * @param colors The colors to apply (2 colors, start and end).
     * @return This builder instance.
     * @throws IllegalArgumentException If a color cannot be read, with that color in the message.
     */
    default TooltipBuilder backgroundColors(String... colors) {
        return backgroundColorsList(Arrays.stream(colors).map(color -> requireArgb("background color", color)).toList());
    }

    /**
     * Sets the background colors used by the tooltip from a list.
     * <p>
     * Accepts ARGB hex codes (e.g., "#FAFF0000", "0x8000FF00", "xFF0000FF", "DDFFFFFF"), Minecraft color names (e.g., "red", "blue") or legacy color codes (e.g., "&4", "&c").
     *
     * @param colors A list of colors strings.
     * @return This builder instance.
     * @throws IllegalArgumentException If a color cannot be read, with that color in the message.
     */
    default TooltipBuilder backgroundColors(List<String> colors) {
        return backgroundColorsList(colors.stream().map(color -> requireArgb("background color", color)).toList());
    }

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
     * Sets the tooltip's background opacity, a multiplier on the alpha of everything the background paints: its colors, its texture, or the vanilla colors if the entry keeps them.
     * <p>
     * 1 keeps the background as its colors and texture are, 0 makes it invisible, and 0.5 halves its alpha. To make a background more opaque than its colors, raise the alpha of the colors instead. Defaults to 1.
     *
     * @param backgroundOpacity The multiplier, from 0 to 1.
     * @return This builder instance.
     * @throws IllegalArgumentException if {@code backgroundOpacity} is not between 0 and 1.
     */
    TooltipBuilder backgroundOpacity(float backgroundOpacity);

    /**
     * Sets the background scale of the tooltip (for repeating and framed background types).
     *
     * @param backgroundScale The scale percentage (e.g., 100 for native size, 50 for half size).
     * @return This builder instance.
     */
    TooltipBuilder backgroundScale(int backgroundScale);

    /**
     * Sets the tooltip's border opacity, a multiplier on the alpha of the border colors, or of the vanilla ones if the entry keeps them.
     * <p>
     * 1 keeps the border as its colors are, 0 makes it invisible, and 0.5 halves its alpha. To make a border more opaque than its colors, raise the alpha of the colors instead: the vanilla border colors have an alpha of 0x50. Defaults to 1.
     *
     * @param borderOpacity The multiplier, from 0 to 1.
     * @return This builder instance.
     * @throws IllegalArgumentException if {@code borderOpacity} is not between 0 and 1.
     */
    TooltipBuilder borderOpacity(float borderOpacity);

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
     * @param lineOffset The number of lines to offset.
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
     * With {@link #requireKeybind(boolean)}, the lines are hidden only while the key is held.
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
    TooltipBuilder font(ResourceLocation fontIdentifier);

    /**
     * Sets a custom font identifier for the tooltip text.
     *
     * @param fontIdentifier The Identifier string of the font (e.g., "minecraft:default", "minecraft:alt").
     * @return This builder instance.
     */
    default TooltipBuilder font(String fontIdentifier) {
        return font(ResourceIds.parse(fontIdentifier));
    }

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
     * Gives the tooltip a stable id, which is what the player's list of disabled tooltips stores.
     * Without one, the tooltip is known by its target plus a hash of its text, position, style, and
     * colors, so changing any of those brings it back for players who had disabled it.
     * <p>
     * Use your mod id as the namespace, for example {@code "mymod:sword_lore"}. Each id can be
     * registered only once. A player who disabled the tooltip before it had an id keeps it disabled,
     * as long as the tooltip gets its id in a release that does not also change those fields.
     *
     * @param id The id, as {@code namespace:path}.
     * @return This builder instance.
     * @throws IllegalArgumentException If the id is not a valid {@code namespace:path}.
     */
    TooltipBuilder id(String id);

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
     * @throws IllegalArgumentException If a tooltip with the same {@link #id(String) id} is already registered.
     */
    TooltipEntry register();

    private static TextColor requireTextColor(String color) {
        TextColor parsed = ColorUtils.parseTextColor(color);
        if (parsed == null) throw new IllegalArgumentException("text color must be RRGGBB hex, a color name or an & code, got '" + color + "'");
        return parsed;
    }

    private static Color requireArgb(String option, String color) {
        Color parsed = ColorUtils.parseArgbColor(color);
        if (parsed == null) throw new IllegalArgumentException(option + " must be AARRGGBB or RRGGBB hex, a color name or an & code, got '" + color + "'");
        return parsed;
    }
}
