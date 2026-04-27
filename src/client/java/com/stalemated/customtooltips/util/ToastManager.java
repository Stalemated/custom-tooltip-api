package com.stalemated.customtooltips.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;

public class ToastManager {

    public static void showKeybindMissingToast() {
        addToast(SystemToast.Type.TUTORIAL_HINT,
                Text.translatable("customtooltips.toast.keybind_missing.title"),
                Text.translatable("customtooltips.toast.keybind_missing.desc")
        );
    }

    public static void showBrokenConfigToast() {
        addToast(SystemToast.Type.PACK_LOAD_FAILURE,
                Text.translatable("customtooltips.toast.config_backup.title"),
                Text.translatable("customtooltips.toast.config_backup.desc")
        );
    }

    public static void showInvalidColorToast() {
        addToast(SystemToast.Type.PACK_LOAD_FAILURE,
                Text.translatable("customtooltips.toast.color.invalid.title"),
                Text.translatable("customtooltips.toast.color.invalid.desc")
        );
    }

    public static void showDuplicatedToast(String targetName) {
        addToast(SystemToast.Type.TUTORIAL_HINT,
                Text.translatable("customtooltips.toast.duplicated.title"),
                Text.translatable("customtooltips.toast.duplicated.desc", targetName)
        );
    }

    private static void addToast(SystemToast.Type type, Text title, Text description) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null && client.getToastManager() != null) {
            SystemToast.add(client.getToastManager(), type, title, description);
        }
    }
}