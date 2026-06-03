package com.stalemated.customtooltips.core;

import com.stalemated.customtooltips.ConfigManager;
import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.core.dimensions.TooltipDimensionManager;
import com.stalemated.customtooltips.core.position.PositionStrategyFactory;
import com.stalemated.customtooltips.core.position.TooltipPositionStrategy;
import com.stalemated.customtooltips.mixin.client.KeyBindingAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

import static com.stalemated.customtooltips.registry.KeybindRegistry.holdKeyKeybind;

import java.util.List;
import java.util.Objects;

public class TooltipProcessor {

    public static void processTooltipLines(ItemStack stack, List<Text> lines) {
        if (lines.isEmpty()) return;
        TooltipDimensionManager.nextTooltipIsItem = true;

        if (ConfigManager.getConfig() != null && ConfigManager.getConfig().align_attribute_icons) {
            IconAligner.alignIcons(lines);
        }

        boolean shouldHideVanilla = false;
        for (TooltipEntry entry : TooltipRegistry.getEntries()) {
            if (!shouldNotProcessEntry(entry, stack) && entry.hide_vanilla_lines) {
                shouldHideVanilla = true;
                break;
            }
        }
        if (shouldHideVanilla && lines.size() > 1) {
            lines.subList(1, lines.size()).clear();
        }

        boolean holdKeyPressed = isHoldKeyPressed();
        boolean needsShiftPrompt = false;

        for (TooltipEntry entry : TooltipRegistry.getEntries()) {
            if (shouldNotProcessEntry(entry, stack)) continue;

            if (entry.require_keybind && !holdKeyPressed) {
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
            if (entry.require_keybind && !holdKeyPressed) continue;

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
        if (ConfigManager.getConfig().disabled_entries.contains(entry.getIdentifier())) return true;

        if (entry.show_only_if_damaged && !stack.isDamaged()) return true;
        if (entry.show_only_if_enchanted && !stack.hasEnchantments()) return true;
        return entry.show_only_if_unbreakable && !(stack.hasNbt() && Objects.requireNonNull(stack.getNbt()).getBoolean("Unbreakable"));
    }

    public static boolean isHoldKeyPressed() {
        if (holdKeyKeybind.isUnbound()) {
            return false;
        }

        InputUtil.Key boundKey = ((KeyBindingAccessor) holdKeyKeybind).getBoundKey();
        long windowHandle = MinecraftClient.getInstance().getWindow().getHandle();

        if (boundKey.getCategory() == InputUtil.Type.KEYSYM) {
            return InputUtil.isKeyPressed(windowHandle, boundKey.getCode());
        } else if (boundKey.getCategory() == InputUtil.Type.MOUSE) {
            return GLFW.glfwGetMouseButton(windowHandle, boundKey.getCode()) == GLFW.GLFW_PRESS;
        }
        
        return false;
    }
}