package com.stalemated.customtooltips.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;

public final class TooltipListToasts {
    //? if >=1.20.4 {
    /*private static final SystemToast.SystemToastId HINT = new SystemToast.SystemToastId();
    private static final SystemToast.SystemToastId BROKEN_CONFIG = SystemToast.SystemToastId.PACK_LOAD_FAILURE;
    private static final SystemToast.SystemToastId SAVE_FAILED = SystemToast.SystemToastId.WORLD_ACCESS_FAILURE;
    *///?} else {
    private static final SystemToast.SystemToastIds HINT = SystemToast.SystemToastIds.TUTORIAL_HINT;
    private static final SystemToast.SystemToastIds BROKEN_CONFIG = SystemToast.SystemToastIds.PACK_LOAD_FAILURE;
    private static final SystemToast.SystemToastIds SAVE_FAILED = SystemToast.SystemToastIds.WORLD_ACCESS_FAILURE;
    //?}

    private TooltipListToasts() {
    }

    public static void showKeybindMissingToast() {
        addHint(
                Component.translatable("customtooltips.toast.keybind_missing.title"),
                Component.translatable("customtooltips.toast.keybind_missing.desc")
        );
    }

    public static void showBrokenConfigToast() {
        SystemToast.add(Minecraft.getInstance().getToasts(), BROKEN_CONFIG,
                Component.translatable("customtooltips.toast.config_backup.title"),
                Component.translatable("customtooltips.toast.config_backup.desc")
        );
    }

    public static void showDuplicatedToast(Component targetName) {
        addHint(
                Component.translatable("customtooltips.toast.duplicated.title"),
                Component.translatable("customtooltips.toast.duplicated.desc", targetName)
        );
    }

    public static void showCopiedToast(Component targetName) {
        addHint(
                Component.translatable("customtooltips.toast.copied.title"),
                Component.translatable("customtooltips.toast.copied.desc", targetName)
        );
    }

    public static void showPastedToast(Component targetName) {
        addHint(
                Component.translatable("customtooltips.toast.pasted.title"),
                Component.translatable("customtooltips.toast.pasted.desc", targetName)
        );
    }

    public static void showSaveFailedToast() {
        SystemToast.addOrUpdate(Minecraft.getInstance().getToasts(), SAVE_FAILED,
                Component.translatable("customtooltips.toast.save_failed.title"),
                Component.translatable("customtooltips.toast.save_failed.desc")
        );
    }

    private static void addHint(Component title, Component description) {
        SystemToast.add(Minecraft.getInstance().getToasts(), HINT, title, description);
    }
}
