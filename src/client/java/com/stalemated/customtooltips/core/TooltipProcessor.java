package com.stalemated.customtooltips.core;

import com.stalemated.customtooltips.ConfigManager;
import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.core.position.PositionStrategyFactory;
import com.stalemated.customtooltips.core.position.TooltipPositionStrategy;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class TooltipProcessor {

    public static void processTooltipLines(ItemStack stack, List<Text> lines) {
        if (lines.isEmpty()) return;

        if (ConfigManager.getConfig() != null && ConfigManager.getConfig().align_attribute_icons) {
            IconAligner.alignIcons(lines);
        }

        boolean shiftPressed = Screen.hasShiftDown();
        boolean needsShiftPrompt = false;

        for (TooltipEntry entry : TooltipRegistry.getEntries()) {
            if (shouldNotProcessEntry(entry, stack)) continue;

            if (entry.require_shift && !shiftPressed) {
                needsShiftPrompt = true;
                continue;
            }

            TooltipPositionStrategy strategy = PositionStrategyFactory.get(entry.position);
            strategy.modifyTooltip(lines, entry.getTextComponents(), entry);
        }

        if (needsShiftPrompt) {
            lines.add(Text.translatable("customtooltips.tooltip_processor.shift_prompt").formatted(Formatting.DARK_GRAY));
        }
    }

    public static Text processHeldItemName(ItemStack stack, Text originalName) {
        if (TooltipRegistry.getEntries().isEmpty()) return originalName;

        boolean shiftPressed = Screen.hasShiftDown();

        for (TooltipEntry entry : TooltipRegistry.getEntries()) {
            if (shouldNotProcessEntry(entry, stack)) continue;
            if (entry.require_shift && !shiftPressed) continue;

            TooltipPositionStrategy strategy = PositionStrategyFactory.get(entry.position);
            Text modified = strategy.modifyHeldItemName(originalName, entry.getTextComponents(), entry);
            
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
}