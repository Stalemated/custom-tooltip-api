package com.stalemated.customtooltips;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//? if forge {
/*import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
*///?} elif neoforge {
/*import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
*///?}

//? if !fabric {
/*@Mod(CustomTooltips.MOD_ID)
*///?}
public final class CustomTooltips {
    public static final String MOD_ID = "customtooltips";
    public static final String ASSETS_NAMESPACE = "custom-tooltip-api";
    public static final String PACK_NAMESPACE = "custom_tooltip_api";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    //? if forge {
    /*public CustomTooltips() {
        if (FMLEnvironment.dist == Dist.CLIENT) CustomTooltipsClient.init();
    }
    *///?} elif neoforge {
    /*public CustomTooltips(Dist dist) {
        if (dist == Dist.CLIENT) CustomTooltipsClient.init();
    }
    *///?} else {
    private CustomTooltips() {
    }
    //?}
}
