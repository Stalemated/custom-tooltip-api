package com.stalemated.customtooltips.gui;

import com.stalemated.customtooltips.ConfigManager;
import com.stalemated.customtooltips.gui.factories.ListScreenUIFactory;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import com.stalemated.customtooltips.util.ToastManager;
import net.minecraft.text.Text;

import static com.stalemated.customtooltips.CustomTooltipApiClient.openConfigKeybind;

import java.util.List;

public class TooltipListScreen extends Screen {
    private final Screen parent;
    public TooltipListWidget listWidget;
    public TextFieldWidget searchBox;
    private static boolean hasShownKeybindToast = false;
    private String searchText = "";
    private List<Text> activeTooltip = null;
    public static boolean showApiEntries = false;

    public TooltipListScreen(Screen parent) {
        super(Text.translatable("customtooltips.tooltip_list_screen.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.listWidget = new TooltipListWidget(this.client, this.width, this.height, 55, this.height - 32, 25, this);
        this.addSelectableChild(this.listWidget);

        this.searchBox = ListScreenUIFactory.createSearchBox(this, this.textRenderer, this.searchText);
        this.addSelectableChild(this.searchBox);
        this.setInitialFocus(this.searchBox);

        this.listWidget.updateEntries(this.searchText);

        showToasts();

        ListScreenUIFactory.createActionButtons(this).forEach(this::addDrawableChild);
        ListScreenUIFactory.createFooterButtons(this).forEach(this::addDrawableChild);
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public void setHoveredTooltip(List<Text> tooltip) {
        this.activeTooltip = tooltip;
    }

    private void showToasts() {
        if (!hasShownKeybindToast) {
            if (this.client != null && this.client.options != null && openConfigKeybind.isUnbound()) {
                ToastManager.showKeybindMissingToast();
            }
            hasShownKeybindToast = true;
        }

        if (ConfigManager.configLoadFailed) {
            ToastManager.showBrokenConfigToast();
            ConfigManager.configLoadFailed = false;
        }
    }

    // Overrides

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.activeTooltip = null;
        this.listWidget.render(context, mouseX, mouseY, delta);
        this.searchBox.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
        
        if (this.activeTooltip != null) {
            context.drawTooltip(this.textRenderer, this.activeTooltip, mouseX, mouseY);
        }
    }

    @Override
    public void tick() {
        if (this.searchBox != null) {
            this.searchBox.tick();
        }
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(this.parent);
        }
    }
}