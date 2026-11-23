package com.stalemated.customtooltips.mixin.client;

import com.stalemated.customtooltips.gui.DoubleClickSelection;
import com.stalemated.lib.mixin.RequiresMod;
import dev.isxander.yacl3.gui.controllers.string.StringControllerElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
//? if <26.1 {
import org.spongepowered.asm.mixin.Unique;
//?}
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//? if >=26.1 {
/*import net.minecraft.client.input.MouseButtonEvent;
*///?}

@RequiresMod("yet_another_config_lib_v3")
@Mixin(StringControllerElement.class)
public abstract class StringControllerElementMixin {

    //? if <26.1 {
    @Unique
    private final DoubleClickSelection customtooltips$doubleClick = new DoubleClickSelection();
    //?}

    @Shadow
    protected abstract boolean doSelectAll();

    //? if >=26.1 {
    /*@Inject(method = "mouseClicked", at = @At("RETURN"))
    private void customtooltips$selectAllOnDoubleClick(MouseButtonEvent event, boolean doubleClick, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ() && doubleClick && DoubleClickSelection.isEnabled()) this.doSelectAll();
    }
    *///?} else {
    //? if >=1.21 {
    /*@Inject(method = "onMouseClicked", at = @At("RETURN"))
    *///?} else {
    @Inject(method = "mouseClicked", at = @At("RETURN"))
    //?}
    private void customtooltips$selectAllOnDoubleClick(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ() && this.customtooltips$doubleClick.selectsAll()) this.doSelectAll();
    }
    //?}
}
