package net.honeyberries;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public final class FPSRenderer {
    private FPSRenderer() {}

    public static void render(GuiGraphics context, DeltaTracker delta) {
        Minecraft client = Minecraft.getInstance();

        // Use the Singleton Instance for the toggle check
        if (!FPSConfig.INSTANCE.enableFps) return;

        // Record stats
        FPSStats.recordFrame(System.nanoTime());

        // Respect F3 and other debug overlays
        if (!client.debugEntries.isOverlayVisible() && !client.options.hideGui) {
            context.pose().pushMatrix();

            // 1. Position and Scale using Singleton values
            // Note: Use translate(x, y, z) for clarity
            context.pose().translate(FPSConfig.INSTANCE.xOffset, FPSConfig.INSTANCE.yOffset);
            context.pose().scale(FPSConfig.INSTANCE.hudScale);

            String avgText = FPSStats.getDisplayStringAvg();
            String lowsText = FPSStats.getDisplayStringLows();

            // 2. Calculate Dynamic Dimensions
            int maxWidth = client.font.width(avgText);
            int totalHeight = 10;

            if (FPSConfig.INSTANCE.enableAdvancedStats) {
                maxWidth = Math.max(maxWidth, client.font.width(lowsText));
                totalHeight += 10;
            }

            // 3. Draw Background Box
            // Extracting alpha from the singleton's bgColor
            int bgColor = FPSConfig.INSTANCE.bgColor;
            if (((bgColor >> 24) & 0xFF) > 0) {
                context.fill(-2, -2, maxWidth + 2, totalHeight, bgColor);
            }

            // 4. Draw Strings using singleton settings
            int textColor = FPSConfig.INSTANCE.hudColor;
            boolean useShadow = FPSConfig.INSTANCE.enableShadow;

            context.drawString(client.font, avgText, 0, 0, textColor, useShadow);

            if (FPSConfig.INSTANCE.enableAdvancedStats) {
                context.drawString(client.font, lowsText, 0, 10, textColor, useShadow);
            }

            context.pose().popMatrix();
        }
    }
}