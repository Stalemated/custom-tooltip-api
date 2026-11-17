package com.stalemated.customtooltips.core.position;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.api.enums.TooltipPosition;
import net.minecraft.network.chat.Component;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LinePlacementTest {

    @Test
    void topPutsTheTextUnderTheNameAfterTheOffset() {
        List<Component> lines = lines("name", "a", "b");

        LinePlacement.place(lines, lines("x", "y"), entry(TooltipPosition.TOP, 1));

        assertEquals(List.of("name", "a", "x", "y", "b"), strings(lines));
    }

    @Test
    void bottomCountsTheOffsetUpFromTheEndAndStopsUnderTheName() {
        List<Component> oneUp = lines("name", "a", "b");
        List<Component> tooFarUp = lines("name", "a", "b");

        LinePlacement.place(oneUp, lines("x"), entry(TooltipPosition.BOTTOM, -1));
        LinePlacement.place(tooFarUp, lines("x"), entry(TooltipPosition.BOTTOM, -10));

        assertEquals(List.of("name", "a", "x", "b"), strings(oneUp));
        assertEquals(List.of("name", "x", "a", "b"), strings(tooFarUp));
    }

    @Test
    void replaceLineReplacesTheLineUnderTheOffsetButNeverTheName() {
        List<Component> lines = lines("name", "a", "b");
        List<Component> onlyName = lines("name");

        LinePlacement.place(lines, lines("x", "y"), entry(TooltipPosition.REPLACE_LINE, 1));
        LinePlacement.place(onlyName, lines("x"), entry(TooltipPosition.REPLACE_LINE, 0));

        assertEquals(List.of("name", "a", "x", "y"), strings(lines));
        assertEquals(List.of("name"), strings(onlyName));
    }

    @Test
    void prependPutsTheTextBeforeTheLine() {
        List<Component> lines = lines("name", "a");

        LinePlacement.place(lines, lines("x", "y"), entry(TooltipPosition.PREPEND, 1));

        assertEquals(List.of("name", "x y a"), strings(lines));
    }

    @Test
    void anEmptyLineBeforeGoesAboveTheText() {
        List<Component> lines = lines("name", "a");
        TooltipEntry entry = entry(TooltipPosition.TOP, 0);
        entry.emptyLineBefore = true;

        LinePlacement.place(lines, lines("x"), entry);

        assertEquals(List.of("name", "", "x", "a"), strings(lines));
    }

    private static TooltipEntry entry(TooltipPosition position, int lineOffset) {
        TooltipEntry entry = new TooltipEntry();
        entry.position = position;
        entry.lineOffset = lineOffset;
        return entry;
    }

    private static List<Component> lines(String... texts) {
        return new ArrayList<>(Arrays.stream(texts).<Component>map(Component::literal).toList());
    }

    private static List<String> strings(List<Component> lines) {
        return lines.stream().map(Component::getString).toList();
    }
}
