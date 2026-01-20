package net.honeyberries;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public final class FPSRenderer {
    private FPSRenderer() {
    }

    public static void render(GuiGraphics context, DeltaTracker delta) {
        Minecraft client = Minecraft.getInstance();
        FPSStats.recordFrame(System.nanoTime());

        if (!client.debugEntries.isOverlayVisible()) {
            int color = 0xFFFFFFFF;

            context.pose().pushMatrix();
            context.pose().scale(1.0f);

            context.drawString(client.font, FPSStats.getDisplayStringAvg(), 1, 1, color, true);
            context.drawString(client.font, FPSStats.getDisplayStringLows(), 1, 10, color, true);

            context.pose().popMatrix();
        }
    }
}
