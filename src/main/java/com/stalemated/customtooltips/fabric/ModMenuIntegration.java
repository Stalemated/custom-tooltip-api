package com.stalemated.customtooltips.fabric;

//? if fabric {
import com.stalemated.customtooltips.gui.screen.TooltipListScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() { return TooltipListScreen::new; }
}
//?}
