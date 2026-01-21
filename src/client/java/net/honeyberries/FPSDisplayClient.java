package net.honeyberries;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.resources.Identifier;

/**
 * Client-side initialization for the FPS Display mod.
 * Handles registration of the FPS HUD element and loading of configuration.
 */
public class FPSDisplayClient implements ClientModInitializer {
    /**
     * Called when the client mod is initialized.
     * Loads the configuration from disk and registers the FPS renderer
     * to be displayed before the vanilla hotbar element.
     */
    @Override
    public void onInitializeClient() {
        FPSConfig.load();
        HudElementRegistry.attachElementBefore(
                VanillaHudElements.HOTBAR,
                Identifier.fromNamespaceAndPath(FPSDisplay.MOD_ID, "fps_data"),
                FPSRenderer::render
        );
    }
}
