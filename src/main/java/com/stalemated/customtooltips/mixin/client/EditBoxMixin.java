package com.stalemated.customtooltips.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.stalemated.customtooltips.gui.DoubleClickSelection;
import net.minecraft.client.gui.components.EditBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EditBox.class)
public abstract class EditBoxMixin {

    @Unique
    private final DoubleClickSelection customtooltips$doubleClick = new DoubleClickSelection();

    @WrapOperation(method = "onClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/EditBox;moveCursorTo(I)V"))
    private void customtooltips$selectAllOnDoubleClick(EditBox editBox, int cursor, Operation<Void> original) {
        if (!this.customtooltips$doubleClick.selectsAll()) {
            original.call(editBox, cursor);
            return;
        }
        editBox.setCursorPosition(0);
        editBox.setHighlightPos(editBox.getValue().length());
    }
}
