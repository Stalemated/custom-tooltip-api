package com.stalemated.customtooltips.forge;

//? if forge {
/*import com.stalemated.customtooltips.CustomTooltips;
import com.stalemated.customtooltips.TooltipEntry;
import com.stalemated.customtooltips.core.CurrentTooltip;
import com.stalemated.customtooltips.core.TooltipColors;
import com.stalemated.customtooltips.core.background.TooltipBackgroundPainter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = CustomTooltips.MOD_ID)
public class ForgeTooltipEvents {

    @SubscribeEvent
    public static void onRenderTooltipColor(RenderTooltipEvent.Color event) {
        TooltipEntry entry = CurrentTooltip.entry();

        if (entry != null) {
            if (entry.hasCustomBackground()) {
                int width = 0;
                int height = 0;

                for (int i = 0; i < event.getComponents().size(); i++) {
                    ClientTooltipComponent component = event.getComponents().get(i);
                    width = Math.max(width, component.getWidth(event.getFont()));
                    height += component.getHeight();
                }

                int bgX = event.getX() - 3;
                int bgY = event.getY() - 3;
                int bgWidth = width + 6;
                int bgHeight = height + 6;

                GuiGraphics context = event.getGraphics();
                TooltipBackgroundPainter.paint(context, entry, bgX, bgY, bgWidth, bgHeight, 400, event.getBackgroundStart());

                event.setBackgroundStart(0x00000000);
                event.setBackgroundEnd(0x00000000);
            } else {
                event.setBackgroundStart(TooltipColors.backgroundStart(entry, event.getBackgroundStart()));
                event.setBackgroundEnd(TooltipColors.backgroundEnd(entry, event.getBackgroundEnd()));
            }
            event.setBorderStart(TooltipColors.borderStart(entry, event.getBorderStart()));
            event.setBorderEnd(TooltipColors.borderEnd(entry, event.getBorderEnd()));
        }
    }
}
*///?}
