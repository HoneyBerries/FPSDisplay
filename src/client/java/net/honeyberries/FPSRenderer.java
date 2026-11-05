package net.honeyberries;

import net.honeyberries.config.FPSDisplayConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import org.spongepowered.asm.mixin.Unique;

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
     * All positioning, scaling, and colors are controlled by FPSDisplayConfig.config().
     *
     * @param context The GuiGraphics context for rendering
     * @param isHidden Whether the vanilla HUD is currently hidden (F1), read directly off the
     *                 {@code Hud} instance being rendered so it can't desync from another mod's view of it
     */
    public static void render(GuiGraphicsExtractor context, boolean isHidden) {
        Minecraft client = Minecraft.getInstance();

        FPSDisplayConfig cfg = FPSDisplayConfig.config();
        if (!cfg.enableFps) return;

        // Record stats
        FPSStats.recordFrame(System.nanoTime());

        boolean showFpsText = cfg.showFpsText;

        // Respect F3 and other debug overlays
        if (shouldShowHUD(isHidden)) {
            context.pose().pushMatrix();

            // 1. Position and Scale using Singleton values
            context.pose().translate(cfg.xOffset, cfg.yOffset);
            context.pose().scale(cfg.hudScale);

            String avgText = FPSStats.getDisplayStringAvg(showFpsText);
            String lowsText = FPSStats.getDisplayStringLows();

            // 2. Calculate Dynamic Dimensions
            int maxWidth = client.font.width(avgText);
            int textHeight = 10;

            if (cfg.enableAdvancedStats) {
                maxWidth = Math.max(maxWidth, client.font.width(lowsText));
                textHeight += 10;
            }

            boolean showGraph = cfg.enableFrametimeGraph;
            int totalHeight = textHeight;
            if (showGraph) {
                maxWidth = Math.max(maxWidth, cfg.graphWidth);
                totalHeight += 2 + cfg.graphHeight;
            }

            // 3. Draw Background Box
            // Extracting alpha from the singleton's bgColor
            int bgColor = cfg.bgColor.getRGB();
            if (((bgColor >> 24) & 0xFF) > 0) {
                context.fill(-2, -2, maxWidth + 2, totalHeight, bgColor);
            }

            // 4. Draw Strings using singleton settings
            int textColor = cfg.hudColor.getRGB();
                        boolean useShadow = cfg.enableShadow;

            renderText(context, client.font, avgText, 0, 0, textColor, useShadow);

            if (cfg.enableAdvancedStats) {
                renderText(context, client.font, lowsText, 0, 10, textColor, useShadow);
            }

            // 5. Draw Frametime Graph
            if (showGraph) {
                renderFrametimeGraph(context, textHeight + 2);
            }

            context.pose().popMatrix();
        }
    }


    /**
     * Draws the scrolling frametime bar graph.
     * Each bar is one pixel wide; height is proportional to frametime clamped to graphMaxMs.
     * Bars are color-coded: graphColor below 16.7 ms, yellow up to 33.3 ms, red above.
     *
     * @param context Graphics context
     * @param yStart  Y coordinate of the top edge of the graph area
     */
    @Unique
    private static void renderFrametimeGraph(GuiGraphicsExtractor context, int yStart) {
        FPSDisplayConfig cfg = FPSDisplayConfig.config();
        int graphWidth = cfg.graphWidth;
        int graphHeight = cfg.graphHeight;
        float maxMs = cfg.graphMaxMs;
        int goodColor = cfg.graphColor.getRGB();
        float yellowMs = cfg.yellowThresholdMs();
        float redMs = cfg.redThresholdMs();
        int yellowColor = cfg.yellowColor.getRGB();
        int redColor = cfg.redColor.getRGB();

        float[] samples = FPSStats.getRecentFrametimes(graphWidth);
        int yBase = yStart + graphHeight;
        int xStart = graphWidth - samples.length; // right-align so newest bar is at the right edge

        for (int i = 0; i < samples.length; i++) {
            float ft = samples[i];
            int barHeight = Math.round(Math.clamp(ft / maxMs * graphHeight, 1, graphHeight));

            int x = xStart + i;
            int color = ft < yellowMs ? goodColor : (ft < redMs ? yellowColor : redColor);
            context.fill(x, yBase - barHeight, x + 1, yBase, color);
        }
    }

    /**
     * Renders a text string at the given position. Scaling is already applied by the caller's
     * pose transform, so no extra scale is applied here.
     */
    @Unique
    private static void renderText(GuiGraphicsExtractor graphics, Font textRenderer, String text, int x, int y, int color, boolean shadowed) {
        graphics.text(textRenderer, text, x, y, color, shadowed);
    }


    /** Determines whether the FPS HUD should be rendered based on game state and settings.
     * This method checks:
     <p> 1. If the current screen is a LevelLoadingScreen (don't show HUD during loading)
     <p> 2. If the player's GUI is hidden (don't show HUD if GUI is hidden)
     <p> 3. If the F3 debug overlay is open (don't show HUD when the F3 debug screen is open)
     *
     * @param isHidden Whether the vanilla HUD is currently hidden (F1)
     * @return true if the HUD should be shown, false otherwise
     */
    public static boolean shouldShowHUD(boolean isHidden) {
        Minecraft client = Minecraft.getInstance();

        // Note: DebugScreenOverlay#showDebugScreen() also returns true whenever ANY individual
        // debug entry is set to "always on" (persisted in debug-profile.json), not just when F3
        // is actually open. That made the HUD silently vanish the moment any debug entry got
        // toggled on, with nothing in the log. debugEntries.isOverlayVisible() reflects only the
        // F3 screen's own open/closed state, matching what a user would expect "F3 is open" to mean.
        return !(client.gui.screen() instanceof LevelLoadingScreen) &&
               !isHidden &&
               !client.debugEntries.isOverlayVisible();
    }
}
