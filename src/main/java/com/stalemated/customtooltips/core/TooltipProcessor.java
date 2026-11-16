package com.stalemated.customtooltips.core;

import com.stalemated.customtooltips.config.CustomTooltipsConfigRegistry;
import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.core.position.PositionStrategyFactory;
import com.stalemated.customtooltips.core.position.TooltipPositionStrategy;
import com.stalemated.lib.util.input.KeyBindingUtil;

import static com.stalemated.customtooltips.registry.KeybindRegistry.holdKeyKeybind;

import java.util.List;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class TooltipProcessor {

    public static void processTooltipLines(ItemStack stack, List<Component> lines) {
        if (lines.isEmpty()) return;

        if (CustomTooltipsConfigRegistry.CONFIG.get().align_attribute_icons) {
            IconAligner.alignIcons(lines);
        }

        boolean shouldHideVanilla = false;
        for (TooltipEntry entry : TooltipRegistry.getEntries()) {
            if (!shouldNotProcessEntry(entry, stack) && entry.hideVanillaLines) {
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

            if (entry.requireKeybind && !holdKeyPressed) {
                needsShiftPrompt = true;
                continue;
            }

            TooltipPositionStrategy strategy = PositionStrategyFactory.get(entry.position);
            strategy.modifyTooltip(lines, entry.getTextComponents(stack), entry);
        }

        if (needsShiftPrompt) {
            Component keyName = holdKeyKeybind.getTranslatedKeyMessage();
            lines.add(Component.translatable("customtooltips.tooltip_processor.shift_prompt", keyName).withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    public static Component processHeldItemName(ItemStack stack, Component originalName) {
        if (TooltipRegistry.getEntries().isEmpty()) return originalName;

        boolean holdKeyPressed = KeyBindingUtil.isKeyDownInGui(holdKeyKeybind);

        for (TooltipEntry entry : TooltipRegistry.getEntries()) {
            if (shouldNotProcessEntry(entry, stack)) continue;
            if (entry.requireKeybind && !holdKeyPressed) continue;

            TooltipPositionStrategy strategy = PositionStrategyFactory.get(entry.position);
            Component modified = strategy.modifyHeldItemName(originalName, entry.getTextComponents(stack), entry);
            
            if (modified != originalName) {
                return modified;
            }
        }

        return originalName;
    }

    private static boolean shouldNotProcessEntry(TooltipEntry entry, ItemStack stack) {
        if (entry == null || !entry.matches(stack)) return true;
        if (CustomTooltipsConfigRegistry.CONFIG.get().disabled_entries.contains(entry.getIdentifier())) return true;

        if (entry.showOnlyIfDamaged && !stack.isDamaged()) return true;
        if (entry.showOnlyIfEnchanted && !stack.isEnchanted()) return true;
        return entry.showOnlyIfUnbreakable && !(stack.hasTag() && Objects.requireNonNull(stack.getTag()).getBoolean("Unbreakable"));
    }
}