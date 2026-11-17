package com.stalemated.customtooltips.core;

import com.stalemated.customtooltips.config.CustomTooltipsConfigRegistry;
import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.core.position.LinePlacement;
import com.stalemated.customtooltips.gui.GuiIcon;

import static com.stalemated.customtooltips.registry.KeybindRegistry.HOLD_KEY;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class TooltipProcessor {

    public static void processTooltipLines(ItemStack stack, List<Component> lines) {
        if (lines.isEmpty()) return;

        if (CustomTooltipsConfigRegistry.CONFIG.get().align_attribute_icons) {
            IconAligner.alignIcons(lines);
        }

        MatchedEntries matched = TooltipRegistry.activeEntries().match(stack, HoldKey.isDown());
        if (matched.hidesVanillaLines() && lines.size() > 1) {
            lines.subList(1, lines.size()).clear();
        }

        for (TooltipEntry entry : matched.shown()) {
            LinePlacement.place(lines, entry.getTextComponents(stack), entry);
        }

        if (matched.waitingForKey()) {
            Component keyName = HOLD_KEY.getTranslatedKeyMessage();
            lines.add(Component.empty()
                    .append(GuiIcon.SHIFT_PROMPT.text())
                    .append(" ")
                    .append(Component.translatable("customtooltips.tooltip_processor.shift_prompt", keyName))
                    .withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    public static Component processHeldItemName(ItemStack stack, Component originalName) {
        ActiveEntries activeEntries = TooltipRegistry.activeEntries();
        if (activeEntries.isEmpty()) return originalName;

        for (TooltipEntry entry : activeEntries.match(stack, HoldKey.isDown()).shown()) {
            Component modified = LinePlacement.heldItemName(originalName, entry.getTextComponents(stack), entry);
            
            if (modified != originalName) {
                return modified;
            }
        }

        return originalName;
    }
}