package com.stalemated.customtooltips.test;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.api.CustomTooltipApi;
import com.stalemated.customtooltips.api.enums.TooltipPosition;
import com.stalemated.customtooltips.api.enums.TooltipStyle;
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
        CustomTooltipApi.builder("minecraft:diamond_sword")
                .text(List.of("✦ Legendary Sword ✦", "Forged in the Stars."))
                .style(TooltipStyle.RAINBOW)
                .bold(true)
                .italic(true)
                .position(TooltipPosition.TOP)
                .tickrate(35)
                .register();

        CustomTooltipApi.builder("minecraft:diamond_sword")
                .addLine("Eternal Item")
                .style(TooltipStyle.RAINBOW)
                .bold(true)
                .emptyLineBefore(true)
                .position(TooltipPosition.BOTTOM)
                .register();

        // Renames the golden apple with a custom static gradient
        CustomTooltipApi.builder("minecraft:golden_apple")
                .addLine("Apple of the Gods")
                .style(TooltipStyle.STATIC_GRADIENT)
                .colors(List.of("#FFD700", "#FF4500"))
                .bold(true)
                .position(TooltipPosition.REPLACE_NAME)
                .register();

        // Example of a Dynamic Text Provider reading item state in real-time
        CustomTooltipApi.builder("minecraft:iron_sword")
                .dynamicText(stack -> {
                    if (stack.isDamageable()) {
                        int remaining = stack.getMaxDamage() - stack.getDamage();
                        return List.of("Durability: " + remaining + " / " + stack.getMaxDamage());
                    }
                    return List.of("This item is Unbreakable");
                })
                .style(TooltipStyle.BREATHING_GRADIENT)
                .colors("#00FF00", "#004400")
                .register();

        /*
         Adds a warning to all pickaxes (needs shift to show)
         This is an example of a tooltip that was built but not registered to the API
         The developer can now choose to register it whenever they want by calling
         CustomTooltipApi.registerTooltip(entry);
        */
        TooltipEntry pickaxeWarning = CustomTooltipApi.builder("#c:pickaxes")
                .addLine("Warning: §eHeavy §rTool")
                .style(TooltipStyle.SOLID)
                .colors("red")
                .requireKeybind(true)
                .emptyLineBefore(true)
                .build();

        CustomTooltipApi.registerTooltip(pickaxeWarning);

        // Example of a tooltip that embeds a translation key inside of it.
        CustomTooltipApi.builder("minecraft:stone_sword")
                .addLine(CustomTooltipApi.translate("cta.item.test"))
                .style(TooltipStyle.SLIDE_GRADIENT)
                .colors("#00DE00", "FA2299")
                .register();

        LOGGER.info("API test successful.");
    }
}