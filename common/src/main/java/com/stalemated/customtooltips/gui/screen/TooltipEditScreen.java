package com.stalemated.customtooltips.gui.screen;

import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.config.ConfigManager;
import com.stalemated.customtooltips.gui.helper.RenderGuiTooltipHelper;
import com.stalemated.customtooltips.util.CustomBackgroundManager;
import com.stalemated.customtooltips.util.CustomFontManager;
import com.stalemated.lib.compat.yacl.builder.YaclScreenBuilder;
import com.stalemated.lib.config.manager.LocalConfigManager;
import com.stalemated.lib.util.color.ColorUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class TooltipEditScreen {

    public static TooltipEntry previewEntry = null;

    public static void renderPreview(Screen screen, DrawContext context, int mouseX, int mouseY, float tickDelta) {
        if (previewEntry != null && screen.getTitle().contains(Text.translatable("customtooltips.tooltip_edit_screen.title"))) {
            if (Screen.hasControlDown()) {
                List<Text> previewLines = new ArrayList<>(previewEntry.getTextComponents(ItemStack.EMPTY));
                RenderGuiTooltipHelper.renderGuiTooltip(previewEntry, previewLines, context, mouseX, mouseY);
            }
        }
    }

    public static Screen create(Screen parent, TooltipEntry entry, boolean isNew) {
        previewEntry = entry.copy();

        LocalConfigManager<TooltipEntry> entryManager = LocalConfigManager.forInstance(entry, () -> {
            TooltipEntry def = new TooltipEntry();
            def.text = new ArrayList<>(List.of("Default text"));
            return def;
        }, saved -> {
            if (isNew && !ConfigManager.getConfig().entries.contains(saved)) {
                ConfigManager.getConfig().entries.add(saved);
            }
            saved.invalidateCaches();
            ConfigManager.save();
            if (parent instanceof TooltipListScreen listScreen) {
                listScreen.listWidget.updateEntries(listScreen.searchBox.getText());
            }
        });

        return YaclScreenBuilder.create(parent, Text.translatable("customtooltips.tooltip_edit_screen.title"), "customtooltips")
                .livePreview(entryManager, previewEntry, TooltipEntry::invalidateCaches)
                .category(entryManager, cat ->
                        cat.name(Text.translatable("customtooltips.tooltip_edit_screen.title"))
                        .group("target", grp -> grp.include("target"))
                        .listOption("text")
                        .group("style_colors", grp ->
                                grp.include("style")
                                .listElement("colors", 0, "primaryColor", ColorUtils.resolveTextColor("white"))
                                .listElement("colors", 1, "secondaryColor", ColorUtils.resolveTextColor("white"))
                        )
                        .group("position_animation", grp ->
                                grp.include("position", "lineOffset", "tickrate", "animationOffset", "reverseAnimation")
                        )
                        .group("formatting", grp ->
                                grp.collapsed(true)
                                .dropdown("font", CustomFontManager.availableFonts)
                                .include("bold", "italic", "underlined", "strikethrough", "obfuscated")
                        )
                        .group("conditions", grp ->
                                grp.include("requireKeybind", "emptyLineBefore", "hideVanillaLines",
                                            "showOnlyIfDamaged", "showOnlyIfEnchanted", "showOnlyIfUnbreakable")
                        )
                )
                .category(entryManager, cat ->
                        cat.name(Text.translatable("customtooltips.tooltip_edit_screen.title_background"))
                        .group("background", grp ->
                                grp.include("backgroundType", "backgroundOpacity")
                                .listElement("backgroundColors", 0, "backgroundTopColor", ColorUtils.parseToAWT("#F0100010"))
                                .listElement("backgroundColors", 1, "backgroundBottomColor", ColorUtils.parseToAWT("#F0100010"))
                                .dropdown("backgroundTexture", CustomBackgroundManager.availableBackgrounds,
                                        s -> Text.literal(s.replace("custom_tooltip_api:textures/gui/tooltip_backgrounds/", ""))
                                )
                                .include("backgroundScale")
                        )
                        .group("border", grp ->
                                grp.include("borderOpacity")
                                .listElement("borderColors", 0, "borderTopColor", ColorUtils.parseToAWT("#505000FF"))
                                .listElement("borderColors", 1, "borderBottomColor", ColorUtils.parseToAWT("#5028007F"))
                        )
                )
                .generateScreen();
    }
}
