package com.stalemated.customtooltips;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//? if forge {
/*import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
*///?}

//? if forge {
/*@Mod(CustomTooltips.MOD_ID)
*///?}
public final class CustomTooltips {
    public static final String MOD_ID = "customtooltips";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    //? if forge {
    /*public CustomTooltips() {
        if (FMLEnvironment.dist == Dist.CLIENT) CustomTooltipsClient.init();
    }
    *///?} else {
    private CustomTooltips() {
    }
    //?}
}
