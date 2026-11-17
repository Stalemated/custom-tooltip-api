package com.stalemated.customtooltips.gui.screen;

import com.stalemated.customtooltips.CustomTooltips;
import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.config.CustomTooltipsConfigRegistry;
import com.stalemated.customtooltips.gui.TooltipPreview;
import com.stalemated.customtooltips.util.CustomBackgroundManager;
import com.stalemated.customtooltips.util.CustomFontManager;
import com.stalemated.lib.compat.yacl.builder.YaclScreenBuilder;
import com.stalemated.lib.config.ConfigManager;
import com.stalemated.lib.util.color.VanillaTooltipColors;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.ItemStack;

public class TooltipEditScreen {

    public static Screen edit(Screen parent, TooltipEntry entry) {
        return create(parent, CustomTooltipsConfigRegistry.CONFIG.editListElement(config -> config.entries, entry, TooltipEditScreen::defaultEntry));
    }

    public static Screen add(Screen parent) {
        return create(parent, CustomTooltipsConfigRegistry.CONFIG.addListElement(config -> config.entries, TooltipEditScreen::defaultEntry));
    }

    private static TooltipEntry defaultEntry() {
        TooltipEntry entry = new TooltipEntry();
        entry.text = new ArrayList<>(List.of("Default Text"));
        return entry;
    }

    private static Screen create(Screen parent, ConfigManager<TooltipEntry> entryManager) {
        return builder(parent, entryManager, CustomFontManager.availableFonts, CustomBackgroundManager.availableBackgrounds).generateScreen();
    }

    private static void renderPreview(TooltipEntry preview, GuiGraphics context, int mouseX, int mouseY) {
        if (!Screen.hasControlDown()) return;
        TooltipPreview.render(preview, new ArrayList<>(preview.getTextComponents(ItemStack.EMPTY)), context, mouseX, mouseY);
    }

    static YaclScreenBuilder builder(Screen parent, ConfigManager<TooltipEntry> entryManager, List<String> fonts, List<String> backgrounds) {
        TooltipEntry preview = entryManager.cloneConfig(entryManager.get());
        TextColor white = TextColor.fromLegacyFormat(ChatFormatting.WHITE);
        Color vanillaBackground = new Color(VanillaTooltipColors.BACKGROUND, true);

        return YaclScreenBuilder.create(parent, Component.translatable("customtooltips.tooltip_edit_screen.title"), CustomTooltips.MOD_ID)
                .livePreview(entryManager, preview, TooltipEntry::invalidateCaches)
                .onRender((context, mouseX, mouseY, partialTick) -> renderPreview(preview, context, mouseX, mouseY))
                .category(entryManager, "tooltip", cat ->
                        cat.name(Component.translatable("customtooltips.tooltip_edit_screen.title"))
                        .group("target", grp -> grp.include("target"))
                        .listOption("text")
                        .group("style_colors", grp ->
                                grp.include("style")
                                .listElement("colors", 0, "primaryColor", white)
                                .listElement("colors", 1, "secondaryColor", white)
                        )
                        .group("position_animation", grp ->
                                grp.include("position", "lineOffset", "tickrate", "animationOffset", "reverseAnimation")
                        )
                        .group("formatting", grp ->
                                grp.collapsed(true)
                                .dropdown("font", fonts)
                                .include("bold", "italic", "underlined", "strikethrough", "obfuscated")
                        )
                        .group("conditions", grp ->
                                grp.include("requireKeybind", "emptyLineBefore", "hideVanillaLines",
                                            "showOnlyIfDamaged", "showOnlyIfEnchanted", "showOnlyIfUnbreakable")
                        )
                )
                .category(entryManager, "background", cat ->
                        cat.name(Component.translatable("customtooltips.tooltip_edit_screen.title_background"))
                        .group("background", grp ->
                                grp.include("backgroundType", "backgroundOpacity")
                                .listElement("backgroundColors", 0, "backgroundTopColor", vanillaBackground)
                                .listElement("backgroundColors", 1, "backgroundBottomColor", vanillaBackground)
                                .dropdown("backgroundTexture", backgrounds,
                                        s -> Component.literal(s.replace("custom_tooltip_api:textures/gui/tooltip_backgrounds/", ""))
                                )
                                .include("backgroundScale")
                        )
                        .group("border", grp ->
                                grp.include("borderOpacity")
                                .listElement("borderColors", 0, "borderTopColor", new Color(VanillaTooltipColors.BORDER_TOP, true))
                                .listElement("borderColors", 1, "borderBottomColor", new Color(VanillaTooltipColors.BORDER_BOTTOM, true))
                        )
                );
    }
}
