package com.stalemated.customtooltips;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.Locale;

public class TooltipEntry {
    public String target = "";
    public String text = "Default tooltip text";
    public String color = "gray";
    public boolean bold = false;
    public boolean italic = false;
    public boolean require_shift = false;
    public boolean empty_line_before = false;

    public TooltipEntry() {}

    public TooltipEntry(String target, String text, String color, boolean bold, boolean italic, boolean require_shift, boolean empty_line_before) {
        this.target = target;
        this.text = text;
        this.color = color;
        this.bold = bold;
        this.italic = italic;
        this.require_shift = require_shift;
        this.empty_line_before = empty_line_before;
    }

    public boolean matches(ItemStack stack) {
        if (target == null || target.isEmpty()) return false;

        try {
            if (target.startsWith("#")) {
                // Handles item tags (#c:weapons, etc...)
                Identifier id = new Identifier(target.substring(1));
                TagKey<Item> tagKey = TagKey.of(RegistryKeys.ITEM, id);
                return stack.isIn(tagKey);
            } else {
                // Handles item ids
                Identifier id = new Identifier(target);
                return Registries.ITEM.getId(stack.getItem()).equals(id);
            }
        } catch (Exception e) {
            return false;
        }
    }

    public Text getTextComponent() {
        MutableText textComponent = Text.literal(this.text);
        Style style = Style.EMPTY;

        if (this.color != null && !this.color.isEmpty()) {
            if (this.color.startsWith("#")) {
                // Handles hex colors
                style = style.withColor(TextColor.parse(this.color));
            } else {
                // Handles Minecraft formatting colors
                Formatting format = Formatting.byName(this.color.toLowerCase(Locale.ROOT));
                if (format != null) {
                    style = style.withFormatting(format);
                } else {
                    style = style.withColor(TextColor.parse(this.color)); // Fallback
                }
            }
        }

        if (this.bold) style = style.withBold(true);
        if (this.italic) style = style.withItalic(true);

        return textComponent.setStyle(style);
    }
}