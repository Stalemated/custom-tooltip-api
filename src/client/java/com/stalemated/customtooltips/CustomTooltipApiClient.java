package com.stalemated.customtooltips;

import com.stalemated.customtooltips.core.IconAligner;
import com.stalemated.customtooltips.core.TooltipProcessor;
import com.stalemated.customtooltips.core.TooltipRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
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

		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			@Override
			public Identifier getFabricId() {
				return new Identifier(MOD_ID, "icon_cache_clearer");
			}

			@Override
			public void reload(ResourceManager manager) {
				IconAligner.clearCache();
				LOGGER.info("Icon aligner cache cleared due to resource pack reload.");
			}
		});

		ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
			if (stack.isEmpty()) return;

			TooltipProcessor.processTooltipLines(stack, lines);
		});
	}
}