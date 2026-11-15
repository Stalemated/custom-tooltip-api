package com.stalemated.customtooltips.core.background.strategies;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.core.background.BackgroundRenderStrategy;
import com.stalemated.customtooltips.core.background.helper.DefaultBackgroundHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import com.stalemated.customtooltips.core.background.helper.BlendHelper;
import com.stalemated.customtooltips.core.background.helper.AtlasRenderHelper;

public class TextureBackgroundStrategy implements BackgroundRenderStrategy {
    @Override
    public void render(GuiGraphics context, int x, int y, int width, int height, int z, int defaultColor, TooltipEntry entry) {
        if (DefaultBackgroundHelper.renderDefaultBackground(context, x, y, width, height, z, defaultColor, entry) == 1) {
            return;
        }

        TextureAtlasSprite sprite = AtlasRenderHelper.getSprite(entry.backgroundTexture);
        if (AtlasRenderHelper.isMissingSprite(sprite)) {
            BlendHelper.setupStandaloneRendering(context, AtlasRenderHelper.getRawTextureId(entry.backgroundTexture), z, defaultColor);
            AtlasRenderHelper.drawSimpleStandalone(context, x, y, width, height);
            BlendHelper.disableBlend(context);
            return;
        }

        BlendHelper.setupAtlasRendering(context, z, defaultColor);
        AtlasRenderHelper.drawSimple(context, sprite, x, y, width, height);
        BlendHelper.disableBlend(context);
    }
}