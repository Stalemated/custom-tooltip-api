package com.stalemated.customtooltips.fabric.mixin.compat.tierify;

import com.stalemated.customtooltips.fabric.compat.LegendaryTooltipsCompatHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Screen.class)
public class CaptureItemStackMixin {
    @Inject(method = "getTooltipFromItem", at = @At("HEAD"))
    private static void customtooltips$captureTooltipStack(MinecraftClient client, ItemStack stack, CallbackInfoReturnable<List<Text>> cir) {
        LegendaryTooltipsCompatHelper.setCapturedStack(stack);
    }
}
