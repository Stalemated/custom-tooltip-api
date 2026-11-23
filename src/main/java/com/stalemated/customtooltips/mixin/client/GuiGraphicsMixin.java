package com.stalemated.customtooltips.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.stalemated.customtooltips.core.CurrentTooltip;
import net.minecraft.client.gui.Font;
//? if >=26.1 {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
*///?} else {
import net.minecraft.client.gui.GuiGraphics;
//?}
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
//? if >=1.21.6 {
/*import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.injection.At;
*///?}

//? if >=26.1 {
/*@Mixin(GuiGraphicsExtractor.class)
*///?} else {
@Mixin(GuiGraphics.class)
//?}
public abstract class GuiGraphicsMixin {

    //? if >=1.21.6 {
    /*@WrapMethod(method = "setTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V")
    private void customtooltips$renderWithEntry(Font font, ItemStack stack, int x, int y, Operation<Void> original) {
        CurrentTooltip.render(CurrentTooltip.lookFor(stack), () -> original.call(font, stack, x, y));
    }
    *///?} else {
    @WrapMethod(method = "renderTooltip(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V")
    private void customtooltips$renderWithEntry(Font font, ItemStack stack, int x, int y, Operation<Void> original) {
        CurrentTooltip.render(CurrentTooltip.lookFor(stack), () -> original.call(font, stack, x, y));
    }
    //?}

    //? if >=26.1 {
    /*@WrapOperation(method = "setTooltipForNextFrameInternal", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;deferredTooltip:Ljava/lang/Runnable;", opcode = Opcodes.PUTFIELD))
    private void customtooltips$keepLookUntilDrawn(GuiGraphicsExtractor graphics, Runnable tooltip, Operation<Void> original) {
        original.call(graphics, CurrentTooltip.drawLater(tooltip));
    }
    *///?} elif >=1.21.6 {
    /*@WrapOperation(method = "setTooltipForNextFrameInternal", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/GuiGraphics;deferredTooltip:Ljava/lang/Runnable;", opcode = Opcodes.PUTFIELD))
    private void customtooltips$keepLookUntilDrawn(GuiGraphics graphics, Runnable tooltip, Operation<Void> original) {
        original.call(graphics, CurrentTooltip.drawLater(tooltip));
    }
    *///?}
}
