package com.stalemated.customtooltips.core.background.strategies;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.core.background.BackgroundRenderStrategy;
import com.stalemated.customtooltips.core.background.helper.DefaultBackgroundHelper;
import com.stalemated.customtooltips.core.background.helper.BlendHelper;
import net.minecraft.client.gui.DrawContext;
import com.stalemated.customtooltips.core.background.helper.AtlasRenderHelper;
import net.minecraft.client.texture.Sprite;

public class TextureBackgroundStrategy implements BackgroundRenderStrategy {
    @Override
    public void render(DrawContext context, int x, int y, int width, int height, int z, int defaultColor, TooltipEntry entry) {
        if (DefaultBackgroundHelper.renderDefaultBackground(context, x, y, width, height, z, defaultColor, entry) == 1) {
            return;
        }

        Sprite sprite = AtlasRenderHelper.getSprite(entry.backgroundTexture);
        if (sprite == null) return;

        BlendHelper.setupAtlasRendering(context, z, defaultColor);
        AtlasRenderHelper.drawSimple(context, sprite, x, y, width, height);
        BlendHelper.disableBlend(context);
    }
}