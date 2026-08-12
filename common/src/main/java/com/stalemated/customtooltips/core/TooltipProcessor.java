package com.stalemated.customtooltips.core;

import com.stalemated.customtooltips.ConfigManager;
import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.core.position.PositionStrategyFactory;
import com.stalemated.customtooltips.core.position.TooltipPositionStrategy;
import com.stalemated.lib.util.input.KeyBindingUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static com.stalemated.customtooltips.registry.KeybindRegistry.holdKeyKeybind;

import java.util.List;

public class TooltipProcessor {

    public static void processTooltipLines(ItemStack stack, List<Text> lines) {
        if (lines.isEmpty()) return;

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

        boolean holdKeyPressed = KeyBindingUtil.isKeyDownInGui(holdKeyKeybind);
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

        boolean holdKeyPressed = KeyBindingUtil.isKeyDownInGui(holdKeyKeybind);

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

    public static boolean shouldNotProcessEntry(TooltipEntry entry, ItemStack stack) {
        if (entry == null || !entry.matches(stack)) return true;
        if (ConfigManager.getConfig().disabled_entries.contains(entry.getIdentifier())) return true;

        return !entry.areItemConditionsMet(stack);
    }
}