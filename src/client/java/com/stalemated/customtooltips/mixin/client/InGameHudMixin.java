package com.stalemated.customtooltips.mixin.client;

import com.stalemated.customtooltips.ConfigManager;
import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.api.CustomTooltipApi;
import com.stalemated.customtooltips.config.TooltipConfig;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Redirect(method = "renderHeldItemTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getName()Lnet/minecraft/text/Text;"))
    private Text redirectRenderHeldItemName(ItemStack stack) {
        return getCustomTooltipName(stack, stack.getName());
    }

    private Text getCustomTooltipName(ItemStack stack, Text originalName) {
        if (stack == null || stack.isEmpty()) return originalName;

        TooltipConfig config = ConfigManager.getConfig();

        List<TooltipEntry> allEntries = new ArrayList<>();
        if (config != null && config.entries != null) {
            allEntries.addAll(config.entries);
        }
        if (CustomTooltipApi.getApiEntries() != null) {
            allEntries.addAll(CustomTooltipApi.getApiEntries());
        }

        if (allEntries.isEmpty()) return originalName;

        boolean shiftPressed = Screen.hasShiftDown();

        for (TooltipEntry entry : allEntries) {
            if (entry == null) continue;

            if (entry.matches(stack)) {

                if (entry.require_shift && !shiftPressed) { continue; }

                boolean shouldReplace = entry.position == TooltipEntry.TooltipPosition.REPLACE_NAME || entry.position == TooltipEntry.TooltipPosition.REPLACE_ALL;

                if (shouldReplace) {
                    List<Text> components = entry.getTextComponents();
                    if (!components.isEmpty()) {
                        return components.get(0);
                    }
                } else if (entry.position == TooltipEntry.TooltipPosition.APPEND && entry.getLineOffset(1) == 0) {
                    List<Text> components = entry.getTextComponents();
                    if (!components.isEmpty()) {
                        MutableText modifiedMessage = originalName.copy();

                        for (Text component : components) {
                            modifiedMessage.append(Text.literal(" ")).append(component);
                        }
                        return modifiedMessage;
                    }
                } else if (entry.position == TooltipEntry.TooltipPosition.PREPEND && entry.getLineOffset(1) == 0) {
                    List<Text> components = entry.getTextComponents();
                    if (!components.isEmpty()) {
                        MutableText modifiedMessage = Text.empty();

                        for (Text component : components) {
                            modifiedMessage.append(component).append(Text.literal(" "));
                        }
                        modifiedMessage.append(originalName);
                        return modifiedMessage;
                    }
                }
            }
        }

        return originalName;
    }
}