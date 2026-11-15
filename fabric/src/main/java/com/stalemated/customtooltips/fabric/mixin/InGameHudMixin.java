package com.stalemated.customtooltips.fabric.mixin;

import com.stalemated.customtooltips.core.TooltipProcessor;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Gui.class)
public abstract class InGameHudMixin {

    @Redirect(method = "renderSelectedItemName", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getHoverName()Lnet/minecraft/network/chat/Component;"))
    private Component redirectRenderHeldItemName(ItemStack stack) {
        return TooltipProcessor.processHeldItemName(stack, stack.getHoverName());
    }
}