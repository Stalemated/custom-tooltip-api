package com.stalemated.customtooltips.core;

import com.stalemated.customtooltips.ConfigManager;
import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.core.position.PositionStrategyFactory;
import com.stalemated.customtooltips.core.position.TooltipPositionStrategy;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

import static com.stalemated.customtooltips.registry.KeybindRegistry.holdKeyKeybind;

import java.util.List;

public class TooltipProcessor {

    public static void processTooltipLines(ItemStack stack, List<Text> lines) {
        if (lines.isEmpty()) return;

        if (ConfigManager.getConfig() != null && ConfigManager.getConfig().align_attribute_icons) {
            IconAligner.alignIcons(lines);
        }

        boolean holdKeyPressed = isHoldKeyPressed();
        boolean needsShiftPrompt = false;

        for (TooltipEntry entry : TooltipRegistry.getEntries()) {
            if (shouldNotProcessEntry(entry, stack)) continue;

            if (entry.require_shift && !holdKeyPressed) {
                needsShiftPrompt = true;
                continue;
            }

            TooltipPositionStrategy strategy = PositionStrategyFactory.get(entry.position);
            strategy.modifyTooltip(lines, entry.getTextComponents(stack), entry);
        }

        if (needsShiftPrompt) {
            Text keyName = holdKeyKeybind.getBoundKeyLocalizedText();
            lines.add(Text.translatable("customtooltips.tooltip_processor.shift_prompt", keyName).formatted(Formatting.DARK_GRAY));
        }
    }

    public static Text processHeldItemName(ItemStack stack, Text originalName) {
        if (TooltipRegistry.getEntries().isEmpty()) return originalName;

        boolean holdKeyPressed = isHoldKeyPressed();

        for (TooltipEntry entry : TooltipRegistry.getEntries()) {
            if (shouldNotProcessEntry(entry, stack)) continue;
            if (entry.require_shift && !holdKeyPressed) continue;

            TooltipPositionStrategy strategy = PositionStrategyFactory.get(entry.position);
            Text modified = strategy.modifyHeldItemName(originalName, entry.getTextComponents(stack), entry);
            
            if (modified != originalName) {
                return modified;
            }
        }

        return originalName;
    }

    private static boolean shouldNotProcessEntry(TooltipEntry entry, ItemStack stack) {
        if (entry == null || !entry.matches(stack)) return true;
        return ConfigManager.getConfig().disabled_entries.contains(entry.getIdentifier());
    }

    private static boolean isHoldKeyPressed() {
        if (holdKeyKeybind.isUnbound()) {
            return false;
        }

        InputUtil.Key boundKey = KeyBindingHelper.getBoundKeyOf(holdKeyKeybind);
        long windowHandle = MinecraftClient.getInstance().getWindow().getHandle();

        if (boundKey.getCategory() == InputUtil.Type.KEYSYM) {
            return InputUtil.isKeyPressed(windowHandle, boundKey.getCode());
        } else if (boundKey.getCategory() == InputUtil.Type.MOUSE) {
            return GLFW.glfwGetMouseButton(windowHandle, boundKey.getCode()) == GLFW.GLFW_PRESS;
        }
        
        return false;
    }
}