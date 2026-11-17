package com.stalemated.customtooltips.core.position;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.api.enums.TooltipPosition;
import com.stalemated.lib.util.math.MathUtils;
import java.util.List;
import java.util.function.UnaryOperator;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class LinePlacement {

    private LinePlacement() {
    }

    public static void place(List<Component> lines, List<Component> text, TooltipEntry entry) {
        switch (positionOf(entry)) {
            case TOP -> insert(lines, text, Math.min(1, lines.size()) + lineOffset(entry, lines.size()), entry.emptyLineBefore);
            case BOTTOM -> insert(lines, text, lines.size() + lineOffset(entry, lines.size()), entry.emptyLineBefore);
            case REPLACE_NAME -> replaceName(lines, text);
            case REPLACE_LINE -> replaceLine(lines, text, entry);
            case REPLACE_ALL -> replaceAll(lines, text);
            case APPEND -> editLine(lines, entry, line -> append(line, text));
            case PREPEND -> editLine(lines, entry, line -> prepend(line, text));
        }
    }

    public static Component heldItemName(Component name, List<Component> text, TooltipEntry entry) {
        return switch (positionOf(entry)) {
            case REPLACE_NAME, REPLACE_ALL -> text.isEmpty() ? name : text.get(0);
            case APPEND -> entry.lineOffset == 0 ? append(name, text) : name;
            case PREPEND -> entry.lineOffset == 0 ? prepend(name, text) : name;
            case TOP, BOTTOM, REPLACE_LINE -> name;
        };
    }

    public static int lineOffset(TooltipEntry entry, int lineCount) {
        if (lineCount == 0) return 0;

        return switch (positionOf(entry)) {
            case TOP, REPLACE_NAME, REPLACE_LINE, APPEND, PREPEND -> MathUtils.clamp(entry.lineOffset, 0, lineCount - 1);
            case BOTTOM, REPLACE_ALL -> MathUtils.clamp(entry.lineOffset, -(lineCount - 1), 0);
        };
    }

    private static TooltipPosition positionOf(TooltipEntry entry) {
        return entry.position != null ? entry.position : TooltipPosition.BOTTOM;
    }

    private static void insert(List<Component> lines, List<Component> text, int index, boolean emptyLineBefore) {
        int textIndex = index;
        if (emptyLineBefore) lines.add(textIndex++, Component.empty());
        lines.addAll(textIndex, text);
    }

    private static void replaceName(List<Component> lines, List<Component> text) {
        if (text.isEmpty()) return;
        if (lines.isEmpty()) lines.addAll(text);
        else replaceFrom(lines, 0, text);
    }

    private static void replaceLine(List<Component> lines, List<Component> text, TooltipEntry entry) {
        if (text.isEmpty() || lines.isEmpty()) return;

        int index = Math.min(lineOffset(entry, lines.size()) + 1, lines.size() - 1);
        if (index >= 1) replaceFrom(lines, index, text);
    }

    private static void replaceFrom(List<Component> lines, int index, List<Component> text) {
        lines.set(index, text.get(0));
        lines.addAll(index + 1, text.subList(1, text.size()));
    }

    private static void replaceAll(List<Component> lines, List<Component> text) {
        lines.clear();
        lines.addAll(text.isEmpty() ? List.of(Component.literal("")) : text);
    }

    private static void editLine(List<Component> lines, TooltipEntry entry, UnaryOperator<Component> edit) {
        if (lines.isEmpty()) return;

        int index = lineOffset(entry, lines.size());
        lines.set(index, edit.apply(lines.get(index)));
    }

    private static MutableComponent append(Component line, List<Component> text) {
        MutableComponent appended = line.copy();
        for (Component part : text) appended.append(Component.literal(" ")).append(part);
        return appended;
    }

    private static MutableComponent prepend(Component line, List<Component> text) {
        if (text.isEmpty()) return line.copy();

        MutableComponent prepended = Component.empty();
        for (Component part : text) prepended.append(part).append(Component.literal(" "));
        return prepended.append(line);
    }
}
