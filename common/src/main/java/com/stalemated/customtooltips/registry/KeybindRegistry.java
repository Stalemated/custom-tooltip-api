package com.stalemated.customtooltips.registry;

import com.mojang.blaze3d.platform.InputConstants;
import com.stalemated.lib.helper.PlatformHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class KeybindRegistry {
    public static KeyMapping openConfigKeybind;
    public static KeyMapping holdKeyKeybind;

    public static void register() {
        openConfigKeybind = new KeyMapping(
                "key.customtooltips.open_config",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "category.customtooltips.keys"
        );

        holdKeyKeybind = new KeyMapping(
                "key.customtooltips.hold_key",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_LEFT_SHIFT,
                "category.customtooltips.keys"
        );

        PlatformHelper.registerKeyBinding(openConfigKeybind);
        PlatformHelper.registerKeyBinding(holdKeyKeybind);
    }
}
