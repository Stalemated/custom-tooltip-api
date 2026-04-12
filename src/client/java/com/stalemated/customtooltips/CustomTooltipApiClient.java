package com.stalemated.customtooltips;

import com.stalemated.customtooltips.core.TooltipProcessor;
import com.stalemated.customtooltips.core.TooltipRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class CustomTooltipApiClient implements ClientModInitializer {

	public static final String MOD_ID = "customtooltips";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		ConfigManager.register();

		TooltipRegistry.reload();

		ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
			if (stack.isEmpty()) return;

			TooltipProcessor.processTooltipLines(stack, lines);
		});
	}
}