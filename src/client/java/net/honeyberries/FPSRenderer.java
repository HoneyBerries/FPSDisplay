package net.honeyberries;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Unique;

import java.util.Objects;

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
     * All positioning, scaling, and colors are controlled by FPSConfig.getInstance().
     *
     * @param context The GuiGraphics context for rendering
     * @param delta Delta tracker for frame timing (unused in current implementation)
     */
    public static void render(GuiGraphicsExtractor context, DeltaTracker delta) {
        Minecraft client = Minecraft.getInstance();

        // Use the Singleton Instance for the toggle check
        if (!FPSConfig.getInstance().enableFps) return;

        // Record stats
        FPSStats.recordFrame(System.nanoTime());

        boolean showFpsText = FPSConfig.getInstance().showFpsText;

        // Respect F3 and other debug overlays
        if (shouldShowHUD()) {
            context.pose().pushMatrix();

            // 1. Position and Scale using Singleton values
            context.pose().translate(FPSConfig.getInstance().xOffset, FPSConfig.getInstance().yOffset);
            context.pose().scale(FPSConfig.getInstance().hudScale);

            String avgText = FPSStats.getDisplayStringAvg(showFpsText);
            String lowsText = FPSStats.getDisplayStringLows();

            // 2. Calculate Dynamic Dimensions
            int maxWidth = client.font.width(avgText);
            int totalHeight = 10;

            if (FPSConfig.getInstance().enableAdvancedStats) {
                maxWidth = Math.max(maxWidth, client.font.width(lowsText));
                totalHeight += 10;
            }

            // 3. Draw Background Box
            // Extracting alpha from the singleton's bgColor
            int bgColor = FPSConfig.getInstance().bgColor;
            if (((bgColor >> 24) & 0xFF) > 0) {
                context.fill(-2, -2, maxWidth + 2, totalHeight, bgColor);
            }

            // 4. Draw Strings using singleton settings
            int textColor = FPSConfig.getInstance().hudColor;
            float scale = FPSConfig.getInstance().hudScale;
            boolean useShadow = FPSConfig.getInstance().enableShadow;

            renderText(context, client.font, avgText, 0, 0, textColor, scale, useShadow);

            if (FPSConfig.getInstance().enableAdvancedStats) {
                renderText(context, client.font, lowsText, 0, 10, textColor, scale, useShadow);
            }

            context.pose().popMatrix();
        }
    }


    /**
     * Renders a text string with specified properties onto the GUI graphics context.
     *
     * This method supports optional scaling and shadow rendering. When scaling is applied,
     * the rendering coordinates are adjusted to account for the transformation.
     *
     * @param graphics The graphics context used for rendering the text*/
    @Unique
    private static void renderText(GuiGraphicsExtractor graphics, Font textRenderer, String text, int x, int y, int color, float scale, boolean shadowed) {
        if (scale != 1.0f) {
            Matrix3x2fStack matrixStack = graphics.pose();
            matrixStack.pushMatrix();
            matrixStack.translate(x, y);
            matrixStack.scale(scale, scale);
            matrixStack.translate(-x, -y);
            graphics.text(textRenderer, text, x, y, color, shadowed);
            matrixStack.popMatrix();
        }
        else {
            graphics.text(textRenderer, text, x, y, color, shadowed);
        }
    }


    /** Determines whether the FPS HUD should be rendered based on game state and settings.
     * This method checks:
     <p> 1. If the current screen is a LevelLoadingScreen (don't show HUD during loading)
     <p> 2. If the player's GUI is hidden (don't show HUD if GUI is hidden)
     <p> 3. If the debug overlay is active (don't show HUD when the F3 debug screen is open)
     *
     * @return true if the HUD should be shown, false otherwise
     */
    public static boolean shouldShowHUD() {
        Minecraft client = Minecraft.getInstance();
        // Logic: Don't hide if (F1 is pressed AND I am NOT a spectator)
        boolean hideGuiCheck = !(client.options.hideGui);

        return !(client.screen instanceof LevelLoadingScreen) &&
               hideGuiCheck &&
               !client.getDebugOverlay().showDebugScreen();
    }
}