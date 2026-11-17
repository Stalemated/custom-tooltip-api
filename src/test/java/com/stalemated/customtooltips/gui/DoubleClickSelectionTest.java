package com.stalemated.customtooltips.gui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DoubleClickSelectionTest {

    @Test
    void aSecondClickWithin250MsIsADoubleClick() {
        DoubleClickSelection selection = new DoubleClickSelection();

        assertFalse(selection.isDoubleClick(10_000));
        assertTrue(selection.isDoubleClick(10_249));
    }

    @Test
    void aSecondClickAfter250MsStartsOver() {
        DoubleClickSelection selection = new DoubleClickSelection();

        assertFalse(selection.isDoubleClick(10_000));
        assertFalse(selection.isDoubleClick(10_250));
        assertTrue(selection.isDoubleClick(10_300));
    }
}
