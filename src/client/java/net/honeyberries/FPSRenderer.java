package net.honeyberries;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Handles rendering of the FPS display HUD element on screen.
 * This class is responsible for drawing the FPS counter with customizable
 * position, scale, colors, and advanced statistics based on configuration.
 */
public final class FPSRenderer {
    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with only static methods.
     */
    private FPSRenderer() {}

    /**
     * Renders the FPS display on the screen.
     * Called every frame by the HUD rendering system.
     * <p>
     * This method:
     * 1. Checks if FPS display is enabled in config
     * 2. Records frame timing for statistics
     * 3. Respects F3 debug overlay and hidden GUI settings
     * 4. Draws background box (if configured with visible alpha)
     * 5. Draws FPS text with shadow (if enabled)
     * 6. Draws advanced statistics (if enabled)
     * <p>
     * All positioning, scaling, and colors are controlled by FPSConfig.INSTANCE.
     *
     * @param context The GuiGraphics context for rendering
     * @param delta Delta tracker for frame timing (unused in current implementation)
     */
    public static void render(GuiGraphics context, DeltaTracker delta) {
        Minecraft client = Minecraft.getInstance();

        // Use the Singleton Instance for the toggle check
        if (!FPSConfig.INSTANCE.enableFps) return;

        // Record stats
        FPSStats.recordFrame(System.nanoTime());

        boolean showFpsText = FPSConfig.INSTANCE.showFpsText;

        // Respect F3 and other debug overlays
        if (!client.debugEntries.isOverlayVisible() && !client.options.hideGui) {
            context.pose().pushMatrix();

            // 1. Position and Scale using Singleton values
            context.pose().translate(FPSConfig.INSTANCE.xOffset, FPSConfig.INSTANCE.yOffset);
            context.pose().scale(FPSConfig.INSTANCE.hudScale);

            String avgText = FPSStats.getDisplayStringAvg(showFpsText);
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