package com.stalemated.customtooltips.config;

import com.stalemated.customtooltips.TooltipEntry;
import dev.isxander.yacl3.config.v2.api.SerialEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TooltipConfig {

    @SerialEntry(comment = "Aligns icons from resource packs at the start of the line so they stay aligned.")
    public boolean align_attribute_icons = false;

    @SerialEntry(comment = "Enables double click to select text in textboxes throughout the entire game.")
    public boolean enable_double_click_selection = true;

    public enum SortMode {
        CREATION_DATE,
        NAME_AND_TAG,
        DISABLED_FIRST
    }

    @SerialEntry(comment = "Tooltip sorting mode in the config menu. Accepts: CREATION_DATE, NAME_AND_TAG, DISABLED_FIRST")
    public SortMode sort_mode = SortMode.CREATION_DATE;

    @SerialEntry(comment = "List of unique entry Identifiers that have been disabled by the user.")
    public List<String> disabled_entries = new ArrayList<>();

    @SerialEntry(comment = """
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
            'line_offset': Offsets which line the tooltip is gonna show up on. Accepts integers.
            'animation_offset': Animation animation_offset that desynchronizes different animations. Accepts integers.
            'tickrate': Accepts integers different from 0. Speed of the animation. Closer to 0 is higher speed.
            """)
    public List<TooltipEntry> entries = new ArrayList<>();

    public void addDefaultEntries() {
        this.entries.add(new TooltipEntry(
                "#c:swords",
                new ArrayList<>(Arrays.asList("Assassin's Sword", "", "+15% Critical Damage")),
                TooltipEntry.TooltipStyle.SLIDE_GRADIENT,
                new ArrayList<>(Arrays.asList("#59CDE9", "#0A2A88")),
                true, false, true, false, false,
                false, true,
                TooltipEntry.TooltipPosition.TOP, 0,
                0, 1, "minecraft:default"
        ));
    }
}