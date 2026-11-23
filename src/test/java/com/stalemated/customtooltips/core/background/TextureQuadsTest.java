package com.stalemated.customtooltips.core.background;

import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TextureQuadsTest {

    @Test
    void nineSliceFillsTheBackgroundExactly() {
        List<TexturedQuad> quads = TextureQuads.nineSlice(UvRect.FULL, 10, 20, 100, 40, 100);

        int area = quads.stream().mapToInt(quad -> quad.width() * quad.height()).sum();
        assertEquals(100 * 40, area);
        assertTrue(quads.stream().allMatch(quad -> quad.x() >= 10 && quad.right() <= 110 && quad.y() >= 20 && quad.bottom() <= 60));
    }

    @Test
    void repeatingTilesCutTheLastTileAtTheEdge() {
        List<TexturedQuad> quads = TextureQuads.repeating(UvRect.FULL, 0, 0, 100, 64, 100);

        TexturedQuad last = quads.get(quads.size() - 1);
        assertEquals(2, quads.size());
        assertEquals(64, last.x());
        assertEquals(36, last.width());
        assertEquals(36 / 64.0f, last.maxU(), 1.0e-6f);
    }
}
