package com.stalemated.customtooltips;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "custom_tooltips")
public class TooltipConfig implements ConfigData {

    @Comment("""
             Test comment
            """)
    public TooltipEntry[] entries = new TooltipEntry[]{};
}