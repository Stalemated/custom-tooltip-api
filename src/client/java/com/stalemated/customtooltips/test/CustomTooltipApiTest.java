package com.stalemated.customtooltips.test;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.api.CustomTooltipApi;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;
import java.util.Arrays;

@Environment(EnvType.CLIENT)
public class CustomTooltipApiTest implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        // These are different examples on how to use the public API

        // Rainbow effect tooltip on diamond swords
        TooltipEntry rainbowSword = new TooltipEntry(
                "minecraft:diamond_sword",
                new ArrayList<>(Arrays.asList("✦ Legendary Sword ✦", "Forged in the Stars.")),
                TooltipEntry.TooltipStyle.RAINBOW,
                new ArrayList<>(),
                true, true, false, false, false,
                false, true,
                TooltipEntry.TooltipPosition.TOP, 0,
                0, 2L
        );

        // Renames the golden apple with a custom static gradient
        TooltipEntry godApple = new TooltipEntry(
                "minecraft:golden_apple",
                new ArrayList<>(Arrays.asList("Apple of the Gods")),
                TooltipEntry.TooltipStyle.STATIC_GRADIENT,
                new ArrayList<>(Arrays.asList("#FFD700", "#FF4500")),
                true, false, false, false, false,
                false, false,
                TooltipEntry.TooltipPosition.REPLACE_NAME, 0,
                0, 1L
        );

        // Adds a warning to all pickaxes (needs shift to show)
        TooltipEntry pickaxeWarning = new TooltipEntry(
                "#c:pickaxes",
                new ArrayList<>(Arrays.asList("Warning: Heavy Tool")),
                TooltipEntry.TooltipStyle.SOLID,
                new ArrayList<>(Arrays.asList("red")),
                false, false, false, false, false,
                true, true,
                TooltipEntry.TooltipPosition.BOTTOM, 0,
                0, 1L
        );

        CustomTooltipApi.registerTooltip(rainbowSword);
        CustomTooltipApi.registerTooltip(godApple);
        CustomTooltipApi.registerTooltip(pickaxeWarning);

        System.out.println("[CustomTooltipAPI] API test successful.");
    }
}