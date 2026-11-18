package com.stalemated.customtooltips.core.position;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.api.enums.TooltipPosition;
import com.stalemated.lib.util.math.MathUtils;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class LinePlacement {
    private static final int UNDER_THE_NAME = 1;

    private final List<Component> lines;
    private final NavigableMap<Integer, Integer> topLinesAt = new TreeMap<>();

    private LinePlacement(List<Component> lines) {
        this.lines = lines;
    }

    public static void placeAll(List<Component> lines, List<TooltipEntry> entries, Function<TooltipEntry, List<Component>> textOf) {
        LinePlacement placement = new LinePlacement(lines);
        for (TooltipEntry entry : entries) placement.place(textOf.apply(entry), entry);
    }

    public static Component heldItemName(Component name, List<TooltipEntry> entries, Function<TooltipEntry, List<Component>> textOf) {
        Component heldName = name;
        for (TooltipEntry entry : entries) heldName = nameAfter(heldName, textOf.apply(entry), entry);
        return heldName;
    }

    public static int lineOffset(TooltipEntry entry, int lineCount) {
        if (lineCount == 0) return 0;

        return switch (positionOf(entry)) {
            case TOP, REPLACE_NAME, REPLACE_LINE, APPEND, PREPEND -> MathUtils.clamp(entry.lineOffset, 0, lineCount - 1);
            case BOTTOM, REPLACE_ALL -> MathUtils.clamp(entry.lineOffset, -(lineCount - 1), 0);
        };
    }

    private static Component nameAfter(Component name, List<Component> text, TooltipEntry entry) {
        return switch (positionOf(entry)) {
            case REPLACE_NAME, REPLACE_ALL -> text.isEmpty() ? name : text.get(0);
            case APPEND -> entry.lineOffset == 0 ? append(name, text) : name;
            case PREPEND -> entry.lineOffset == 0 ? prepend(name, text) : name;
            case TOP, BOTTOM, REPLACE_LINE -> name;
        };
    }

    private static TooltipPosition positionOf(TooltipEntry entry) {
        return entry.position != null ? entry.position : TooltipPosition.BOTTOM;
    }

    private void place(List<Component> text, TooltipEntry entry) {
        switch (positionOf(entry)) {
            case TOP -> insertUnderTheName(text, entry);
            case BOTTOM -> insert(lines.size() + lineOffset(entry, lines.size()), text, entry.emptyLineBefore);
            case REPLACE_NAME -> replaceName(text);
            case REPLACE_LINE -> replaceLine(text, entry);
            case REPLACE_ALL -> replaceAll(text);
            case APPEND -> editLine(entry, line -> append(line, text));
            case PREPEND -> editLine(entry, line -> prepend(line, text));
        }
    }

    private void insertUnderTheName(List<Component> text, TooltipEntry entry) {
        int linesWithoutTopText = lines.size() - topLinesUpTo(Integer.MAX_VALUE);
        int anchor = Math.min(UNDER_THE_NAME, linesWithoutTopText) + lineOffset(entry, linesWithoutTopText);
        int inserted = insert(anchor + topLinesUpTo(anchor), text, entry.emptyLineBefore);
        topLinesAt.merge(anchor, inserted, Integer::sum);
    }

    private int topLinesUpTo(int anchor) {
        return topLinesAt.headMap(anchor, true).values().stream().mapToInt(Integer::intValue).sum();
    }

    private int insert(int index, List<Component> text, boolean emptyLineBefore) {
        int textIndex = index;
        if (emptyLineBefore) lines.add(textIndex++, Component.empty());
        lines.addAll(textIndex, text);
        return textIndex - index + text.size();
    }

    private void replaceName(List<Component> text) {
        if (text.isEmpty()) return;
        if (lines.isEmpty()) {
            lines.addAll(text);
            return;
        }

        replaceFrom(0, text);
        if (text.size() > 1) topLinesAt.merge(UNDER_THE_NAME, text.size() - 1, Integer::sum);
    }

    private void replaceLine(List<Component> text, TooltipEntry entry) {
        if (text.isEmpty() || lines.isEmpty()) return;

        int index = Math.min(lineOffset(entry, lines.size()) + 1, lines.size() - 1);
        if (index >= 1) replaceFrom(index, text);
    }

    private void replaceFrom(int index, List<Component> text) {
        lines.set(index, text.get(0));
        lines.addAll(index + 1, text.subList(1, text.size()));
    }

    private void replaceAll(List<Component> text) {
        lines.clear();
        lines.addAll(text.isEmpty() ? List.of(Component.literal("")) : text);
        topLinesAt.clear();
    }

    private void editLine(TooltipEntry entry, UnaryOperator<Component> edit) {
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
