package com.stalemated.customtooltips.gui;

import com.stalemated.customtooltips.CustomTooltips;
import com.stalemated.lib.util.ResourceIds;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public enum GuiIcon {
    SHIFT_PROMPT("\uDAC1\uDF22"),
    DOUBLE_CLICK("\uDAC1\uDF23"),
    ALIGN_ICONS("\uDAC1\uDF24"),
    CLOCK("\uDAC1\uDF25"),
    DISABLED("\uDAC1\uDF26"),
    DELETE("\uDAC1\uDF27"),
    EDIT("\uDAC1\uDF28"),
    DUPLICATE("\uDAC1\uDF29"),
    ENABLED("\uDAC1\uDF30"),
    CODE("\uDAC1\uDF31"),
    COPY("\uDAC1\uDF32"),
    PASTE("\uDAC1\uDF33");

    private static final ResourceLocation FONT = ResourceIds.of(CustomTooltips.ASSETS_NAMESPACE, "icons");

    private final String glyph;

    GuiIcon(String glyph) {
        this.glyph = glyph;
    }

    public MutableComponent text() {
        return Component.literal(this.glyph).withStyle(style -> style.withFont(FONT));
    }
}
