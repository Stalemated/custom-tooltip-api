package com.stalemated.customtooltips.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.stalemated.customtooltips.core.TooltipProcessor;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Gui.class)
public abstract class GuiMixin {

    @WrapOperation(
            //? if fabric {
            method = "renderSelectedItemName(Lnet/minecraft/client/gui/GuiGraphics;)V",
            //?} else {
            /*method = "renderSelectedItemName(Lnet/minecraft/client/gui/GuiGraphics;I)V",
            *///?}
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getHoverName()Lnet/minecraft/network/chat/Component;"))
    private Component customtooltips$replaceHeldItemName(ItemStack stack, Operation<Component> original) {
        return TooltipProcessor.processHeldItemName(stack, original.call(stack));
    }
}
