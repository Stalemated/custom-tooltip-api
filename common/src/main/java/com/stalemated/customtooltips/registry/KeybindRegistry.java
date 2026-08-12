package com.stalemated.customtooltips.registry;

import com.stalemated.lib.helper.PlatformHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeybindRegistry {
    public static KeyBinding openConfigKeybind;
    public static KeyBinding holdKeyKeybind;

    public static void register() {
        openConfigKeybind = new KeyBinding(
                "key.customtooltips.open_config",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "category.customtooltips.keys"
        );

        holdKeyKeybind = new KeyBinding(
                "key.customtooltips.hold_key",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_LEFT_SHIFT,
                "category.customtooltips.keys"
        );

        PlatformHelper.INSTANCE.registerKeyBinding(openConfigKeybind);
        PlatformHelper.INSTANCE.registerKeyBinding(holdKeyKeybind);
    }
}
