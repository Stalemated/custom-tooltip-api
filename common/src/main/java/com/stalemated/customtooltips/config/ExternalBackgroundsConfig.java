package com.stalemated.customtooltips.config;

import dev.isxander.yacl3.config.v2.api.SerialEntry;

import java.util.ArrayList;
import java.util.List;

public class ExternalBackgroundsConfig {

    @SerialEntry(comment = "List of specific external textures to include in the GUI dropdown. Example: botania:textures/gui/magic_bg.png")
    public List<String> textures = new ArrayList<>();

    @SerialEntry(comment = "List of external directories to scan for .png files and include in the GUI dropdown. Example: simplyswords:textures/item")
    public List<String> directories = new ArrayList<>();
}
