package com.stalemated.customtooltips.core.background;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public record UvRect(float minU, float minV, float maxU, float maxV) {
    public static final UvRect FULL = new UvRect(0.0f, 0.0f, 1.0f, 1.0f);

    public static UvRect of(TextureAtlasSprite sprite) {
        return new UvRect(sprite.getU0(), sprite.getV0(), sprite.getU1(), sprite.getV1());
    }
}
