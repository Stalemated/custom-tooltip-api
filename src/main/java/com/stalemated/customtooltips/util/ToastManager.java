package com.stalemated.customtooltips.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;

public class ToastManager {

    public static void showKeybindMissingToast() {
        addToast(SystemToast.SystemToastIds.TUTORIAL_HINT,
                Component.translatable("customtooltips.toast.keybind_missing.title"),
                Component.translatable("customtooltips.toast.keybind_missing.desc")
        );
    }

    public static void showBrokenConfigToast() {
        addToast(SystemToast.SystemToastIds.PACK_LOAD_FAILURE,
                Component.translatable("customtooltips.toast.config_backup.title"),
                Component.translatable("customtooltips.toast.config_backup.desc")
        );
    }

    public static void showInvalidColorToast() {
        addToast(SystemToast.SystemToastIds.PACK_LOAD_FAILURE,
                Component.translatable("customtooltips.toast.color.invalid.title"),
                Component.translatable("customtooltips.toast.color.invalid.desc")
        );
    }

    public static void showDuplicatedToast(String targetName) {
        addToast(SystemToast.SystemToastIds.TUTORIAL_HINT,
                Component.translatable("customtooltips.toast.duplicated.title"),
                Component.translatable("customtooltips.toast.duplicated.desc", targetName)
        );
    }

    private static void addToast(SystemToast.SystemToastIds type, Component title, Component description) {
        SystemToast.add(Minecraft.getInstance().getToasts(), type, title, description);
    }

    public static void showCopiedToast(String targetName) {
        addToast(SystemToast.SystemToastIds.TUTORIAL_HINT,
                Component.translatable("customtooltips.toast.copied.title"),
                Component.translatable("customtooltips.toast.copied.desc", targetName)
        );
    }

    public static void showPastedToast(String targetName) {
        addToast(SystemToast.SystemToastIds.TUTORIAL_HINT,
                Component.translatable("customtooltips.toast.pasted.title"),
                Component.translatable("customtooltips.toast.pasted.desc", targetName)
        );
    }
}