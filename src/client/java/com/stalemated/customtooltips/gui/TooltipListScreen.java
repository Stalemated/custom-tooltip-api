package com.stalemated.customtooltips.gui;

import com.stalemated.customtooltips.ConfigManager;
import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.gui.factories.ActionBarFactory;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
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
        int btnHeight = 20;
        int btnWidth = 150;
        int btnY = this.height - 28;

        this.listWidget = new TooltipListWidget(this.client, this.width, this.height, 55, this.height - 32, 25, this);
        this.addSelectableChild(this.listWidget);

        this.searchBox = ActionBarFactory.createSearchBox(this, this.textRenderer, this.searchText);
        this.addSelectableChild(this.searchBox);
        this.setInitialFocus(this.searchBox);

        this.listWidget.updateEntries(this.searchText);

        showToasts();

        ActionBarFactory.createActionButtons(this).forEach(this::addDrawableChild);

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("customtooltips.tooltip_list_screen.add_new_tooltip"), button -> {
            TooltipEntry newEntry = TooltipEntry.builder("")
                    .addLine("Default Text")
                    .colors("white")
                    .build();

            if (this.client != null) {
                this.client.setScreen(TooltipEditScreen.create(this, newEntry, true));
            }
        })
                .dimensions(this.width / 2 - 155, btnY, btnWidth, btnHeight)
                .build());

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.done"), button -> this.close())
                .dimensions(this.width / 2 + 5, btnY, btnWidth, btnHeight)
                .build());
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