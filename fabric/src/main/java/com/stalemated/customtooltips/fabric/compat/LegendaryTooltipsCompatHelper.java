package com.stalemated.customtooltips.fabric.compat;

import com.anthonyhilyard.legendarytooltips.config.LegendaryTooltipsConfig;
import com.anthonyhilyard.legendarytooltips.tooltip.ItemModelComponent;
import com.anthonyhilyard.legendarytooltips.tooltip.PaddingComponent;
import com.anthonyhilyard.legendarytooltips.tooltip.TooltipDecor;
import com.stalemated.customtooltips.ConfigManager;
import com.stalemated.customtooltips.core.dimensions.overflow.TitleOverflowStrategyFactory;
import com.stalemated.customtooltips.core.dimensions.overflow.components.IndentedTextTooltipComponent;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.client.render.VertexConsumerProvider;
import org.joml.Matrix4f;

import java.util.List;
import java.util.Collections;

public class LegendaryTooltipsCompatHelper {
    private static ItemStack capturedStack = null;

    public static void setCapturedStack(ItemStack stack) {
        capturedStack = stack;
    }

    public static ItemStack getCapturedStack() {
        return capturedStack;
    }

    public static void clearCapturedStack() {
        capturedStack = null;
    }

    public static void injectLegendaryComponents(List<TooltipComponent> components, TextRenderer textRenderer, int maxTitleWidth) {
        if (!FabricLoader.getInstance().isModLoaded("legendarytooltips")) return;
        
        if (capturedStack != null && !capturedStack.isEmpty()) {
            try {
                LTInjector.inject(components, capturedStack, textRenderer, maxTitleWidth);
            } catch (Throwable ignored) {}
        }
    }

    private static int lastTooltipX = 0;
    private static int lastTooltipY = 0;
    private static int lastTooltipWidth = 0;

    public static void setTooltipPosition(int x, int y, int width) {
        lastTooltipX = x;
        lastTooltipY = y;
        lastTooltipWidth = width;
    }

    public static void drawLegendarySeparator(DrawContext context, List<TooltipComponent> components) {
        if (!FabricLoader.getInstance().isModLoaded("legendarytooltips")) return;
        
        if (capturedStack != null && !capturedStack.isEmpty() && components.size() >= 2) {
            try {
                LTInjector.drawSeparator(context, components);
            } catch (Throwable ignored) {}
        }
    }

    private static class LTInjector {
        static void inject(List<TooltipComponent> components, ItemStack stack, TextRenderer textRenderer, int maxTitleWidth) {
            if (LegendaryTooltipsConfig.showModelForItem(stack)) {
                if (components.isEmpty()) return;
                
                TooltipComponent originalTitle = components.get(0);

                List<TooltipComponent> processedTitleComponents;
                if (ConfigManager.getConfig().custom_tooltip_dimensions) {
                    processedTitleComponents = TitleOverflowStrategyFactory.getStrategy().processComponentPhase(Collections.singletonList(originalTitle), textRenderer, maxTitleWidth);
                } else {
                    processedTitleComponents = Collections.singletonList(originalTitle);
                }

                ItemModelComponent modelComponent = new ItemModelComponent(stack);
                TooltipComponent wrapper = new LegendaryWrapperComponent(modelComponent, processedTitleComponents);

                components.set(0, wrapper);

                PaddingComponent padding = new PaddingComponent(6);
                components.add(1, padding);
            }
        }

        static void drawSeparator(DrawContext context, List<TooltipComponent> components) {
            int color = 0xFF996922; // Default color fallback
            
            TooltipDecor.drawSeparator(context.getMatrices(), lastTooltipX - 2, lastTooltipY + components.get(0).getHeight(), lastTooltipWidth, color);
        }
    }

    private record LegendaryWrapperComponent(TooltipComponent modelComponent, List<TooltipComponent> titleComponents) implements TooltipComponent {

        private int getExtraWidth() {
            return 22;
        }

        private int getTitleHeight() {
            int totalTitleHeight = 0;
            if (titleComponents != null) {
                for (TooltipComponent component : titleComponents) {
                    totalTitleHeight += component.getHeight();
                }
            }
            return totalTitleHeight;
        }

        private int getFirstLineHeight() {
            if (titleComponents != null && !titleComponents.isEmpty()) {
                return titleComponents.get(0).getHeight();
            }
            return 10;
        }

        @Override
        public int getHeight() {
            int yOffset = Math.max(0, (getExtraWidth() - getFirstLineHeight()) / 2);
            return Math.max(22, yOffset + getTitleHeight());
        }

        @Override
        public int getWidth(TextRenderer textRenderer) {
            int maxTitleWidth = 0;
            if (titleComponents != null) {
                for (TooltipComponent component : titleComponents) {
                    int compWidth = component.getWidth(textRenderer);
                    if (!(component instanceof IndentedTextTooltipComponent)) {
                        compWidth += getExtraWidth() + 2;
                    }
                    maxTitleWidth = Math.max(maxTitleWidth, compWidth);
                }
            }
            return maxTitleWidth;
        }

        @Override
        public void drawText(TextRenderer textRenderer, int x, int y, Matrix4f matrix, VertexConsumerProvider.Immediate vertexConsumers) {
            int yOffset = Math.max(0, (getExtraWidth() - getFirstLineHeight()) / 2);
            int currentY = y + yOffset;

            if (titleComponents != null) {
                for (TooltipComponent component : titleComponents) {
                    int drawX = x;
                    if (!(component instanceof IndentedTextTooltipComponent)) {
                        drawX += getExtraWidth() + 2;
                    }
                    component.drawText(textRenderer, drawX, currentY, matrix, vertexConsumers);
                    currentY += component.getHeight();
                }
            }
        }

        @Override
        public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
            modelComponent.drawItems(textRenderer, x, y, context);

            int yOffset = Math.max(0, (getExtraWidth() - getFirstLineHeight()) / 2);
            int currentY = y + yOffset;

            if (titleComponents != null) {
                for (TooltipComponent component : titleComponents) {
                    int drawX = x;
                    if (!(component instanceof IndentedTextTooltipComponent)) {
                        drawX += getExtraWidth() + 2;
                    }
                    component.drawItems(textRenderer, drawX, currentY, context);
                    currentY += component.getHeight();
                }
            }
        }
    }
}
