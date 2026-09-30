package com.stalemated.customtooltips.config;

import com.stalemated.lib.config.annotation.Comment;

import java.util.ArrayList;
import java.util.List;

public class ExternalBackgroundsConfig {

    @Comment("List of specific external textures to include in the GUI dropdown. Example: botania:textures/gui/magic_bg.png")
    public List<String> textures = new ArrayList<>();

    @Comment("List of external directories to scan for .png files and include in the GUI dropdown. Example: simplyswords:textures/item")
    public List<String> directories = new ArrayList<>();
}
