package com.stalemated.customtooltips.core;

import com.stalemated.customtooltips.TooltipEntry;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class TooltipProcessor {

    private static int lineSize;

    public static void processTooltipLines(ItemStack stack, List<Text> lines) {
        if (TooltipRegistry.getEntries().isEmpty() || lines.isEmpty()) return;

        boolean shiftPressed = Screen.hasShiftDown();
        boolean needsShiftPrompt = false;

        for (TooltipEntry entry : TooltipRegistry.getEntries()) {
            if (entry == null || !entry.matches(stack)) continue;

            if (entry.require_shift && !shiftPressed) {
                needsShiftPrompt = true;
                continue;
            }

            if (entry.position == TooltipEntry.TooltipPosition.REPLACE_NAME) {
                lines.set(0, entry.getTextComponents().get(0));
                insertLines(lines, entry.getTextComponents(), 1, 1);

            } else if (entry.position == TooltipEntry.TooltipPosition.REPLACE_ALL) {
                lines.clear();
                lines.addAll(entry.getTextComponents());

            } else if (entry.position == TooltipEntry.TooltipPosition.APPEND) {
                int insertIndex = entry.getLineOffset(lines.size());
                if (isValidIndex(insertIndex, lines)) {
                    lines.set(insertIndex, appendToLine(lines.get(insertIndex), entry.getTextComponents(), " ", ""));
                }

            } else if (entry.position == TooltipEntry.TooltipPosition.PREPEND) {
                int insertIndex = entry.getLineOffset(lines.size());
                if (isValidIndex(insertIndex, lines)) {
                    lines.set(insertIndex, appendToLine(lines.get(insertIndex), entry.getTextComponents(), "", " "));
                }

            } else {
                int baseIndex = (entry.position == TooltipEntry.TooltipPosition.TOP) ? 1 : lines.size();
                int insertIndex = baseIndex + entry.getLineOffset(lines.size());

                if (entry.empty_line_before) {
                    lines.add(insertIndex, Text.empty());
                    insertIndex++;
                }
                insertLines(lines, entry.getTextComponents(), insertIndex, 0);
            }
            lineSize = lines.size();
        }

        if (needsShiftPrompt) {
            lines.add(Text.translatable("customtooltips.tooltip_processor.shift_prompt").formatted(Formatting.DARK_GRAY));
        }
    }

    public static Text processHeldItemName(ItemStack stack, Text originalName) {
        if (TooltipRegistry.getEntries().isEmpty()) return originalName;

        boolean shiftPressed = Screen.hasShiftDown();

        for (TooltipEntry entry : TooltipRegistry.getEntries()) {
            if (entry == null || !entry.matches(stack)) continue;
            if (entry.require_shift && !shiftPressed) continue;

            boolean altersTopLine = (entry.position == TooltipEntry.TooltipPosition.REPLACE_NAME ||
                    entry.position == TooltipEntry.TooltipPosition.REPLACE_ALL);

            if (altersTopLine) {
                List<Text> components = entry.getTextComponents();
                if (!components.isEmpty()) return components.get(0);

            } else if (entry.position == TooltipEntry.TooltipPosition.APPEND && entry.getLineOffset(lineSize) == 0) {
                return appendToLine(originalName, entry.getTextComponents(), " ", "");

            } else if (entry.position == TooltipEntry.TooltipPosition.PREPEND && entry.getLineOffset(lineSize) == 0) {
                return appendToLine(originalName, entry.getTextComponents(), "", " ");
            }
        }

        return originalName;
    }

    private static boolean isValidIndex(int index, List<Text> lines) {
        return index >= 0 && index < lines.size();
    }

    private static void insertLines(List<Text> destination, List<Text> source, int startIndex, int sourceOffset) {
        int currentIndex = startIndex;
        for (int i = sourceOffset; i < source.size(); i++) {
            if (currentIndex > destination.size()) currentIndex = destination.size();
            destination.add(currentIndex, source.get(i));
            currentIndex++;
        }
    }

    private static MutableText appendToLine(Text baseLine, List<Text> components, String prefix, String suffix) {
        if (components.isEmpty()) return baseLine.copy();

        MutableText modified = suffix.equals(" ") ? Text.empty() : baseLine.copy();
        for (Text component : components) {
            if (!prefix.isEmpty()) modified.append(Text.literal(prefix));
            modified.append(component);
            if (!suffix.isEmpty()) modified.append(Text.literal(suffix));
        }
        if (suffix.equals(" ")) modified.append(baseLine);

        return modified;
    }
}