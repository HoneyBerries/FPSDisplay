package net.honeyberries;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

/**
 * Client-side initialization for the FPS Display mod.
 * Handles registration of the FPS HUD element and loading of configuration.
 */
public class FPSDisplayClient implements ClientModInitializer {
    public static KeyMapping TOGGLE_HUD_KEY;

    /**
     * Called when the client mod is initialized.
     * Loads the configuration from disk and registers the FPS renderer
     * to be displayed before the vanilla hotbar element.
     */
    @Override
    public void onInitializeClient() {
        FPSConfig.load();

        KeyMapping.Category fpsdisplayCategory = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath(FPSDisplay.MOD_ID, "keybinds")
        );

        TOGGLE_HUD_KEY = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "key.fpsdisplay.toggle_hud",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            fpsdisplayCategory
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (TOGGLE_HUD_KEY.consumeClick()) {
                FPSConfig.getInstance().enableFps = !FPSConfig.getInstance().enableFps;
                FPSConfig.save();
            }
        });
    }
}
