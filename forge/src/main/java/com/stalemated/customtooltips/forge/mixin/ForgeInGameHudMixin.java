package com.stalemated.customtooltips.forge.mixin;

import com.stalemated.customtooltips.core.TooltipProcessor;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InGameHud.class)
public abstract class ForgeInGameHudMixin {

    @Redirect(method = "renderSelectedItemName", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getName()Lnet/minecraft/text/Text;"))
    private Text redirectRenderHeldItemName(ItemStack stack) {
        return TooltipProcessor.processHeldItemName(stack, stack.getName());
    }
}