package com.stalemated.customtooltips.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.stalemated.customtooltips.core.CurrentTooltip;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin {

    @WrapMethod(method = "renderTooltip(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V")
    private void customtooltips$renderWithEntry(Font font, ItemStack stack, int x, int y, Operation<Void> original) {
        CurrentTooltip.render(CurrentTooltip.entryFor(stack), () -> original.call(font, stack, x, y));
    }
}
