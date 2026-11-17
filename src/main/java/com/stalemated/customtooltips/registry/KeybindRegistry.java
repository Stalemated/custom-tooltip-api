package com.stalemated.customtooltips.registry;

import com.mojang.blaze3d.platform.InputConstants;
import com.stalemated.lib.client.ClientPlatformHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public final class KeybindRegistry {
    private static final String CATEGORY = "category.customtooltips.keys";

    public static final KeyMapping OPEN_CONFIG = new KeyMapping(
            "key.customtooltips.open_config",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            CATEGORY
    );
    public static final KeyMapping HOLD_KEY = new KeyMapping(
            "key.customtooltips.hold_key",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_SHIFT,
            CATEGORY
    );

    private KeybindRegistry() {
    }

    public static void register() {
        ClientPlatformHelper.registerKeyBinding(OPEN_CONFIG);
        ClientPlatformHelper.registerKeyBinding(HOLD_KEY);
    }
}
