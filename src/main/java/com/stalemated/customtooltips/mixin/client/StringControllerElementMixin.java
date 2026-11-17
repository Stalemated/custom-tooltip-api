package com.stalemated.customtooltips.mixin.client;

import com.stalemated.customtooltips.gui.DoubleClickSelection;
import com.stalemated.lib.mixin.RequiresMod;
import dev.isxander.yacl3.gui.controllers.string.StringControllerElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@RequiresMod("yet_another_config_lib_v3")
@Mixin(StringControllerElement.class)
public abstract class StringControllerElementMixin {

    @Unique
    private final DoubleClickSelection customtooltips$doubleClick = new DoubleClickSelection();

    @Shadow
    protected abstract boolean doSelectAll();

    @Inject(method = "mouseClicked", at = @At("RETURN"))
    private void customtooltips$selectAllOnDoubleClick(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ() && this.customtooltips$doubleClick.selectsAll()) this.doSelectAll();
    }
}
