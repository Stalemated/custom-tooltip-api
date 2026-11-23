package com.stalemated.customtooltips.core.background;

public record TexturedQuad(int x, int y, int width, int height, float minU, float minV, float maxU, float maxV) {
    int right() {
        return x + width;
    }

    int bottom() {
        return y + height;
    }
}
