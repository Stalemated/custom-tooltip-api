package com.stalemated.customtooltips.test;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.api.CustomTooltipApi;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.List;

import static com.stalemated.customtooltips.CustomTooltipApiClient.LOGGER;

/**
 * A test class demonstrating how to use the {@link CustomTooltipApi} as a developer.
 * This initializer registers several example tooltips dynamically during the client startup.
 */
@Environment(EnvType.CLIENT)
public class CustomTooltipApiTest implements ClientModInitializer {

    /**
     * Called on the client initialization phase.
     * Showcases different ways to build and register custom tooltips using the Fluent Builder pattern.
     */
    @Override
    public void onInitializeClient() {

        // Rainbow effect tooltip on diamond swords
        TooltipEntry.builder("minecraft:diamond_sword")
                .addLine("✦ Legendary Sword ✦")
                .addLine("Forged in the Stars.")
                .style(TooltipEntry.TooltipStyle.RAINBOW)
                .bold(true)
                .italic(true)
                .position(TooltipEntry.TooltipPosition.TOP)
                .tickrate(35)
                .register();

        TooltipEntry.builder("minecraft:diamond_sword")
                .addLine("Eternal Item")
                .style(TooltipEntry.TooltipStyle.RAINBOW)
                .bold(true)
                .emptyLineBefore(true)
                .position(TooltipEntry.TooltipPosition.BOTTOM)
                .register();

        // Renames the golden apple with a custom static gradient
        TooltipEntry.builder("minecraft:golden_apple")
                .addLine("Apple of the Gods")
                .style(TooltipEntry.TooltipStyle.STATIC_GRADIENT)
                .colors(List.of("#FFD700", "#FF4500"))
                .bold(true)
                .position(TooltipEntry.TooltipPosition.REPLACE_NAME)
                .register();

        /*
         Adds a warning to all pickaxes (needs shift to show)
         This is an example of a tooltip that was built but not registered to the API
         The developer can now choose to register it whenever they want by calling
         CustomTooltipApi.registerTooltip(entry);
        */
        TooltipEntry pickaxeWarning = TooltipEntry.builder("#c:pickaxes")
                .addLine("Warning: §eHeavy §rTool")
                .style(TooltipEntry.TooltipStyle.SOLID)
                .colors("red")
                .requireShift(true)
                .emptyLineBefore(true)
                .build();

        CustomTooltipApi.registerTooltip(pickaxeWarning);

        LOGGER.info("API test successful.");
    }
}