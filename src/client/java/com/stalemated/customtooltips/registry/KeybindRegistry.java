package com.stalemated.customtooltips.registry;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeybindRegistry {
    public static KeyBinding openConfigKeybind;
    public static KeyBinding holdKeyKeybind;

    public static void register() {
        openConfigKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.customtooltips.open_config",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "category.customtooltips.keys"
        ));

        holdKeyKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.customtooltips.hold_key",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_LEFT_SHIFT,
                "category.customtooltips.keys"
        ));
    }
}
