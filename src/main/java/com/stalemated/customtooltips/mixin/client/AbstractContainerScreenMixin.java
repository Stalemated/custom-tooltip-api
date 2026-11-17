package com.stalemated.customtooltips.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.core.CurrentTooltip;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {

    @Shadow @Nullable protected Slot hoveredSlot;

    @WrapMethod(method = "renderTooltip")
    private void customtooltips$renderWithEntry(GuiGraphics context, int x, int y, Operation<Void> original) {
        TooltipEntry entry = this.hoveredSlot != null && this.hoveredSlot.hasItem() ? CurrentTooltip.entryFor(this.hoveredSlot.getItem()) : null;
        CurrentTooltip.render(entry, () -> original.call(context, x, y));
    }
}
