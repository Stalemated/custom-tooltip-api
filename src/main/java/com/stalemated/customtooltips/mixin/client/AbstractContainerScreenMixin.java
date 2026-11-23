package com.stalemated.customtooltips.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.stalemated.customtooltips.core.CurrentTooltip;
import com.stalemated.customtooltips.core.TooltipLook;
//? if >=26.1 {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
*///?} else {
import net.minecraft.client.gui.GuiGraphics;
//?}
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {

    @Shadow @Nullable protected Slot hoveredSlot;

    //? if >=26.1 {
    /*@WrapMethod(method = "extractTooltip")
    private void customtooltips$renderWithEntry(GuiGraphicsExtractor context, int x, int y, Operation<Void> original) {
    *///?} else {
    @WrapMethod(method = "renderTooltip")
    private void customtooltips$renderWithEntry(GuiGraphics context, int x, int y, Operation<Void> original) {
    //?}
        TooltipLook look = this.hoveredSlot != null && this.hoveredSlot.hasItem() ? CurrentTooltip.lookFor(this.hoveredSlot.getItem()) : TooltipLook.VANILLA;
        CurrentTooltip.render(look, () -> original.call(context, x, y));
    }
}
