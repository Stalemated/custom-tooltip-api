package com.stalemated.customtooltips.gui.screen;

import com.stalemated.customtooltips.config.CustomTooltipsConfigRegistry;
import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.gui.TooltipListToasts;
import com.stalemated.customtooltips.gui.TooltipPreview;
import com.stalemated.customtooltips.gui.widget.TooltipListWidget;

import static com.stalemated.customtooltips.registry.KeybindRegistry.OPEN_CONFIG;

import java.util.List;
//? if >=26.1 {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
*///?} else {
import net.minecraft.client.gui.GuiGraphics;
//?}
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class TooltipListScreen extends Screen {
    private final Screen parent;
    private TooltipListWidget listWidget;
    private EditBox searchBox;
    private static boolean hasShownKeybindToast = false;
    private String searchText = "";
    private TooltipEntry activeTooltip = null;
    private List<Component> activeTooltipText = null;
    private static boolean showApiEntries = false;

    public TooltipListScreen(Screen parent) {
        super(Component.translatable("customtooltips.tooltip_list_screen.title"));
        this.parent = parent;
    }

    public static boolean showsApiEntries() {
        return showApiEntries;
    }

    static void toggleApiEntries() {
        showApiEntries = !showApiEntries;
    }

    @Override
    protected void init() {
        this.listWidget = new TooltipListWidget(this.minecraft, this.width, this.height, 55, this.height - 32, 25, this);
        this.addRenderableWidget(this.listWidget);

        this.searchBox = TooltipListControls.createSearchBox(this, this.font, this.searchText);
        this.addRenderableWidget(this.searchBox);
        this.setInitialFocus(this.searchBox);

        refreshList();

        showToasts();

        TooltipListControls.createActionButtons(this).forEach(this::addRenderableWidget);
        TooltipListControls.createFooterButtons(this).forEach(this::addRenderableWidget);
    }

    void search(String searchText) {
        this.searchText = searchText;
        refreshList();
    }

    public void refreshList() {
        this.listWidget.updateEntries(this.searchText);
    }

    public void setHoveredTooltip(List<Component> tooltipText, TooltipEntry tooltip) {
        this.activeTooltip = tooltip;
        this.activeTooltipText = tooltipText;
    }

    private void showToasts() {
        if (!hasShownKeybindToast) {
            if (OPEN_CONFIG.isUnbound()) {
                TooltipListToasts.showKeybindMissingToast();
            }
            hasShownKeybindToast = true;
        }

        if (CustomTooltipsConfigRegistry.consumeLoadFailure()) {
            TooltipListToasts.showBrokenConfigToast();
        }
    }

    // Overrides

    //? if >=26.1 {
    /*@Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        this.activeTooltipText = null;

        super.extractRenderState(context, mouseX, mouseY, delta);
        context.centeredText(this.font, this.title, this.width / 2, 8, 0xFFFFFFFF);

        if (this.activeTooltipText != null) {
            TooltipPreview.render(this.activeTooltip, this.activeTooltipText, context, mouseX, mouseY);
        }
    }
    *///?} else {
    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        this.activeTooltipText = null;

        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredString(this.font, this.title, this.width / 2, 8, 0xFFFFFFFF);
        
        if (this.activeTooltipText != null) {
            TooltipPreview.render(this.activeTooltip, this.activeTooltipText, context, mouseX, mouseY);
        }
    }
    //?}

    //? if <1.20.4 {
    @Override
    public void tick() {
        if (this.searchBox != null) {
            this.searchBox.tick();
        }
    }
    //?}

    @Override
    public void onClose() {
        if (this.minecraft != null) {
            this.minecraft.setScreen(this.parent);
        }
    }
}