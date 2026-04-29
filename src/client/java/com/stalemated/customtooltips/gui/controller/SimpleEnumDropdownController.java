package com.stalemated.customtooltips.gui.controller;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ValueFormatter;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.dropdown.EnumDropdownController;
import dev.isxander.yacl3.gui.controllers.dropdown.EnumDropdownControllerElement;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.stream.Stream;

public class SimpleEnumDropdownController<E extends Enum<E>> extends EnumDropdownController<E> {

    public SimpleEnumDropdownController(Option<E> option, ValueFormatter<E> formatter) {
        super(option, formatter);
    }

    @Override
    protected @NotNull Stream<String> getValidEnumConstants(String value) {
        return this.getAllowedValues().stream();
    }

    @Override
    public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
        return new EnumDropdownControllerElement<>(this, screen, widgetDimension) {
            @Override
            public boolean charTyped(char chr, int modifiers) {
                return false;
            }

            @Override
            public void setFocused(boolean focused) {
                if (!focused) {
                    this.unfocus();
                }
            }

            @Override
            public void unfocus() {
                if (this.isDropdownVisible() && this.dropdownWidget() != null) {
                    int index = this.dropdownWidget().selectedIndex();
                    if (index >= 0 && index < SimpleEnumDropdownController.this.getAllowedValues().size()) {
                        this.inputField = SimpleEnumDropdownController.this.getAllowedValues().get(index);
                    }
                    this.removeDropdownWidget();
                }
                super.unfocus();
            }

            @Override
            public void removeDropdownWidget() {
                this.screen.clearPopupControllerWidget();
                this.dropdownVisible = false;
                this.dropdownWidget = null;
            }

            @Override
            public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
                if (this.isDropdownVisible()) {
                    switch (keyCode) {
                        case 258: // Tab
                            if (Screen.hasShiftDown()) {
                                this.dropdownWidget().selectPreviousEntry();
                            } else {
                                this.dropdownWidget().selectNextEntry();
                            }
                            return true;
                        case 264: // Down Arrow
                            this.dropdownWidget().selectNextEntry();
                            return true;
                        case 265: // Up Arrow
                            this.dropdownWidget().selectPreviousEntry();
                            return true;
                        case 257: // Enter
                        case 335: // Numpad Enter
                        case 32:  // Space
                            this.unfocus();
                            return true;
                        case 256: // Esc
                            this.removeDropdownWidget();
                            return true;
                    }
                }
                return false;
            }

            @Override
            public boolean modifyInput(Consumer<StringBuilder> builder) {
                return false;
            }

            @Override
            public boolean doSelectAll() {
                return false;
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (this.isMouseOver(mouseX, mouseY)) {
                    this.setFocused(true);

                    if (!this.isDropdownVisible()) {
                        this.createDropdownWidget();
                    } else {
                        this.removeDropdownWidget();
                    }
                    return true;
                }
                return false;
            }

            @Override
            public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
                return false;
            }
        };
    }
}