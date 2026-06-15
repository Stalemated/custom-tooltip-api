package com.stalemated.customtooltips.forge;

import com.stalemated.customtooltips.CustomTooltipApiClient;
import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.core.TooltipBackgroundManager;
import com.stalemated.customtooltips.core.background.BackgroundRenderStrategy;
import com.stalemated.customtooltips.core.background.BackgroundStrategyFactory;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = CustomTooltipApiClient.MOD_ID)
public class ForgeTooltipEvents {

    @SubscribeEvent
    public static void onRenderTooltipColor(RenderTooltipEvent.Color event) {
        TooltipEntry entry = TooltipBackgroundManager.getCurrentEntry();

        if (entry != null) {
            if (entry.hasCustomBackground()) {
                int width = 0;
                int height = 0;

                for (int i = 0; i < event.getComponents().size(); i++) {
                    TooltipComponent component = event.getComponents().get(i);
                    width = Math.max(width, component.getWidth(event.getFont()));
                    height += component.getHeight();
                }

                int bgX = event.getX() - 3;
                int bgY = event.getY() - 3;
                int bgWidth = width + 6;
                int bgHeight = height + 6;

                DrawContext context = event.getGraphics();
                BackgroundRenderStrategy strategy = BackgroundStrategyFactory.getStrategy(entry.backgroundType);

                int originalColor = event.getBackgroundStart();
                strategy.render(context, bgX, bgY, bgWidth, bgHeight, 400, originalColor, entry);

                event.setBackgroundStart(0x00000000);
                event.setBackgroundEnd(0x00000000);
            } else {
                event.setBackgroundStart(TooltipBackgroundManager.getBackgroundColorStart(event.getBackgroundStart()));
                event.setBackgroundEnd(TooltipBackgroundManager.getBackgroundColorEnd(event.getBackgroundEnd()));
            }
            event.setBorderStart(TooltipBackgroundManager.getBorderColorStart(event.getBorderStart()));
            event.setBorderEnd(TooltipBackgroundManager.getBorderColorEnd(event.getBorderEnd()));
        }
    }
}