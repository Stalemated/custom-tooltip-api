package com.stalemated.customtooltips.core;

import com.stalemated.lib.util.state.SharedTooltipState;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

import static com.stalemated.customtooltips.core.TooltipColors.UNCHANGED_OPACITY;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CurrentTooltipTest {

    @Test
    void aTooltipDrawnLaterKeepsTheLookItWasSetWith() {
        TooltipLook halfOpacity = new TooltipLook(null, 0.5f, null, UNCHANGED_OPACITY);
        AtomicReference<TooltipLook> lookWhileDrawing = new AtomicReference<>();
        AtomicReference<Runnable> later = new AtomicReference<>();

        CurrentTooltip.render(halfOpacity, () -> later.set(CurrentTooltip.drawLater(() -> lookWhileDrawing.set(CurrentTooltip.look()))));
        later.get().run();

        assertSame(halfOpacity, lookWhileDrawing.get());
        assertSame(TooltipLook.VANILLA, CurrentTooltip.look());
    }

    @Test
    void aTooltipDrawnLaterKeepsTheCustomSizeRequest() {
        AtomicBoolean customSizeWhileDrawing = new AtomicBoolean();
        AtomicReference<Runnable> later = new AtomicReference<>();

        SharedTooltipState.withCustomDimensions(() -> later.set(CurrentTooltip.drawLater(() -> customSizeWhileDrawing.set(SharedTooltipState.isCustomDimensionsRequested()))));
        later.get().run();

        assertTrue(customSizeWhileDrawing.get());
        assertFalse(SharedTooltipState.isCustomDimensionsRequested());
    }
}
