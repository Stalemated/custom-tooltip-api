package com.stalemated.customtooltips.core;

import com.mojang.blaze3d.systems.RenderSystem;
import com.stalemated.lib.util.input.KeyBindingUtil;

import static com.stalemated.customtooltips.registry.KeybindRegistry.HOLD_KEY;

public final class HoldKey {
    private HoldKey() {
    }

    public static boolean isDown() {
        return RenderSystem.isOnRenderThread() && KeyBindingUtil.isKeyDownInGui(HOLD_KEY);
    }
}
