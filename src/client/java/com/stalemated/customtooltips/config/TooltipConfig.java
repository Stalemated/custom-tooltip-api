package com.stalemated.customtooltips.config;

import com.stalemated.customtooltips.TooltipEntry;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Config(name = "custom_tooltips")
public class TooltipConfig implements ConfigData {

    @Comment("""
            Custom Tooltip API Config
            'target': Accepts tags (e.g. #c:swords) or item ids (e.g. minecraft:diamond_sword).
            'text': Accepts multiple lines of text, each one limited by quotes and separated by a comma.
            'style': Animation type. Accepts: SOLID, STATIC_GRADIENT, SLIDE_GRADIENT, BREATHING_GRADIENT, RAINBOW.
            'colors': Accepted values are: Color hex codes or Minecraft formatting colors. Up to 2 colors.
            'bold': If the displayed text should be bold. Accepts true / false.
            'italic': If the displayed text should be italic. Accepts true / false.
            'underlined': If the displayed text should be underlined. Accepts true / false.
            'strikethrough': If the displayed text should be strikethrough. Accepts true / false.
            'obfuscated': If the displayed text should be obfuscated. Accepts true / false.
            'require_shift': If the tooltip requires pressing the shift key to display. Accepts true / false.
            'empty_line_before': Adds an empty line before the tooltip. Accepts true / false.
            'position': Accepts: TOP (Under the item's name) or BOTTOM (Bottom of the tooltip).
            'tickrate': Accepts integers different from 0. Speed of the animation. Closer to 0 is higher speed.
            'offset': Animation offset that desynchronizes different animations. Accepts integers.
            """)
    @ConfigEntry.Gui.CollapsibleObject
    public List<TooltipEntry> entries = new ArrayList<>();

    @Override
    public void validatePostLoad() throws ValidationException {
        if (entries == null) {
            entries = new ArrayList<>();
        }
        if (entries.isEmpty()) {
            entries.add(new TooltipEntry(
                    "#c:swords",
                    new ArrayList<>(Arrays.asList("Assassin's Sword", "", "+15% Critical Damage")),
                    TooltipEntry.TooltipStyle.SLIDE_GRADIENT,
                    new ArrayList<>(Arrays.asList("#59CDE9", "#0A2A88")),
                    true,
                    false,
                    true,
                    false,
                    false,
                    false,
                    true,
                    TooltipEntry.TooltipPosition.TOP,
                    0,
                    1
            ));
        }
    }
}