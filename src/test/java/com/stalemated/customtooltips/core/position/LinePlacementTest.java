package com.stalemated.customtooltips.core.position;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.api.enums.TooltipPosition;
import net.minecraft.network.chat.Component;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LinePlacementTest {

    @Test
    void topPutsTheTextUnderTheNameAfterTheOffset() {
        List<Component> lines = lines("name", "a", "b");

        place(lines, lines("x", "y"), entry(TooltipPosition.TOP, 1));

        assertEquals(List.of("name", "a", "x", "y", "b"), strings(lines));
    }

    @Test
    void bottomCountsTheOffsetUpFromTheEndAndStopsUnderTheName() {
        List<Component> oneUp = lines("name", "a", "b");
        List<Component> tooFarUp = lines("name", "a", "b");

        place(oneUp, lines("x"), entry(TooltipPosition.BOTTOM, -1));
        place(tooFarUp, lines("x"), entry(TooltipPosition.BOTTOM, -10));

        assertEquals(List.of("name", "a", "x", "b"), strings(oneUp));
        assertEquals(List.of("name", "x", "a", "b"), strings(tooFarUp));
    }

    @Test
    void replaceLineReplacesTheLineUnderTheOffsetButNeverTheName() {
        List<Component> lines = lines("name", "a", "b");
        List<Component> onlyName = lines("name");

        place(lines, lines("x", "y"), entry(TooltipPosition.REPLACE_LINE, 1));
        place(onlyName, lines("x"), entry(TooltipPosition.REPLACE_LINE, 0));

        assertEquals(List.of("name", "a", "x", "y"), strings(lines));
        assertEquals(List.of("name"), strings(onlyName));
    }

    @Test
    void prependPutsTheTextBeforeTheLine() {
        List<Component> lines = lines("name", "a");

        place(lines, lines("x", "y"), entry(TooltipPosition.PREPEND, 1));

        assertEquals(List.of("name", "x y a"), strings(lines));
    }

    @Test
    void anEmptyLineBeforeGoesAboveTheText() {
        List<Component> lines = lines("name", "a");
        TooltipEntry entry = entry(TooltipPosition.TOP, 0);
        entry.emptyLineBefore = true;

        place(lines, lines("x"), entry);

        assertEquals(List.of("name", "", "x", "a"), strings(lines));
    }

    @Test
    void topEntriesShowInListOrder() {
        TooltipEntry first = entry(TooltipPosition.TOP, 0);
        TooltipEntry second = entry(TooltipPosition.TOP, 0);
        TooltipEntry afterOneLine = entry(TooltipPosition.TOP, 1);
        Map<TooltipEntry, List<Component>> texts = Map.of(first, lines("x1", "x2"), second, lines("y"), afterOneLine, lines("z"));
        List<Component> lines = lines("name", "a", "b");

        LinePlacement.placeAll(lines, List.of(first, second, afterOneLine), texts::get);

        assertEquals(List.of("name", "x1", "x2", "y", "a", "z", "b"), strings(lines));
    }

    @Test
    void theHeldItemNameEndsLikeTheFirstLineOfTheTooltip() {
        TooltipEntry apiName = entry(TooltipPosition.REPLACE_NAME, 0);
        TooltipEntry userName = entry(TooltipPosition.REPLACE_NAME, 0);
        TooltipEntry suffix = entry(TooltipPosition.APPEND, 0);
        Map<TooltipEntry, List<Component>> texts = Map.of(apiName, lines("api"), userName, lines("mine"), suffix, lines("+1"));

        Component name = LinePlacement.heldItemName(Component.literal("sword"), List.of(apiName, userName, suffix), texts::get);

        assertEquals("mine +1", name.getString());
    }

    private static void place(List<Component> lines, List<Component> text, TooltipEntry entry) {
        LinePlacement.placeAll(lines, List.of(entry), placed -> text);
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
