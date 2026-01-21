package net.honeyberries;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.awt.Color;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Configuration manager for the FPS Display mod.
 * Handles loading, saving, and providing a GUI configuration screen using YACL3.
 *
 * Configuration is persisted to disk as JSON and accessed via the singleton INSTANCE.
 */
public class FPSConfig {
    /**
     * Inner class that holds all configuration data.
     * Acts as both the data model and the source of default values.
     */
    public static class Handler {
        /**
         * Default value: Enable FPS display on startup.
         */
        private static final boolean DEF_ENABLE = true;

        /**
         * Default value: Enable advanced statistics (1% and 0.1% lows).
         */
        private static final boolean DEF_ENABLE_ADVANCED = true;

        /**
         * Default value: Enable text shadow rendering.
         */
        private static final boolean DEF_ENABLE_SHADOW = true;

        /**
         * Default value: X offset from left edge of screen in pixels.
         */
        private static final int DEF_X = 2;

        /**
         * Default value: Y offset from top edge of screen in pixels.
         */
        private static final int DEF_Y = 2;

        /**
         * Default value: HUD scale multiplier (1.0 = normal size).
         */
        private static final float DEF_SCALE = 1.0f;

        /**
         * Default value: Text color in ARGB format (white, fully opaque).
         */
        private static final int DEF_TEXT_COLOR = 0xFFFFFFFF;

        /**
         * Default value: Background color in ARGB format (fully transparent).
         */
        private static final int DEF_BG_COLOR = 0x00000000;

        /**
         * Whether the FPS display is currently enabled.
         */
        public boolean enableFps = DEF_ENABLE;

        /**
         * Whether to show advanced statistics (1% and 0.1% low FPS).
         */
        public boolean enableAdvancedStats = DEF_ENABLE_ADVANCED;

        /**
         * Whether to render a shadow behind the text.
         */
        public boolean enableShadow = DEF_ENABLE_SHADOW;

        /**
         * X position offset from the left edge of the screen in pixels.
         */
        public int xOffset = DEF_X;

        /**
         * Y position offset from the top edge of the screen in pixels.
         */
        public int yOffset = DEF_Y;

        /**
         * Scale multiplier for the HUD (0.5 to 3.0).
         */
        public float hudScale = DEF_SCALE;

        /**
         * Text color in ARGB format (alpha, red, green, blue).
         */
        public int hudColor = DEF_TEXT_COLOR;

        /**
         * Background box color in ARGB format (alpha, red, green, blue).
         */
        public int bgColor = DEF_BG_COLOR;
    }

    /**
     * Singleton instance providing access to all configuration values.
     * Modify this instance to change settings, then call save() to persist changes.
     */
    public static Handler INSTANCE = new Handler();

    /**
     * Location of the configuration file on disk.
     */
    private static final File CONFIG_FILE = new File(Minecraft.getInstance().gameDirectory, "config/fps_display_config.json");

    /**
     * Gson instance for JSON serialization/deserialization with pretty printing.
     */
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Creates the configuration screen GUI using YACL3 (Yet Another Config Lib).
     *
     * The screen includes:
     * - General category: Toggle FPS display, advanced stats, and positioning
     * - Appearance category: Scale, colors, and text shadow
     *
     * All changes are bound to the INSTANCE and saved when the screen is closed.
     *
     * @param parent The parent screen to return to when closing the config screen
     * @return A Screen instance showing the configuration GUI
     */
    public static Screen createConfigScreen(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
            .title(Component.literal("FPS Display Settings"))
            .save(FPSConfig::save)
            .category(ConfigCategory.createBuilder()
                .name(Component.literal("General"))
                .group(OptionGroup.createBuilder()
                    .name(Component.literal("HUD Data"))
                    .option(buildBool("Enable FPS", "Show basic FPS counter", () -> Handler.DEF_ENABLE, () -> INSTANCE.enableFps, val -> INSTANCE.enableFps = val))
                    .option(buildBool("Advanced Stats", "Show 1% and 0.1% lows", () -> Handler.DEF_ENABLE_ADVANCED, () -> INSTANCE.enableAdvancedStats, val -> INSTANCE.enableAdvancedStats = val))
                    .build())
                .group(OptionGroup.createBuilder()
                    .name(Component.literal("Positioning"))
                    .option(buildIntSlider("X Offset", 0, 400, () -> Handler.DEF_X, () -> INSTANCE.xOffset, val -> INSTANCE.xOffset = val))
                    .option(buildIntSlider("Y Offset", 0, 300, () -> Handler.DEF_Y, () -> INSTANCE.yOffset, val -> INSTANCE.yOffset = val))
                    .build())
                .build())
            .category(ConfigCategory.createBuilder()
                .name(Component.literal("Appearance"))
                .option(buildFloatSlider("HUD Scale", 0.5f, 3.0f, () -> Handler.DEF_SCALE, () -> INSTANCE.hudScale, val -> INSTANCE.hudScale = val))
                .option(buildBool("Text Shadow", "Render a shadow behind text", () -> Handler.DEF_ENABLE_SHADOW, () -> INSTANCE.enableShadow, val -> INSTANCE.enableShadow = val))
                .option(buildColor("Text Color", () -> Handler.DEF_TEXT_COLOR, () -> INSTANCE.hudColor, val -> INSTANCE.hudColor = val))
                .option(buildColor("Background Color", () -> Handler.DEF_BG_COLOR, () -> INSTANCE.bgColor, val -> INSTANCE.bgColor = val))
                .build())
            .build()
            .generateScreen(parent);
    }

    /**
     * Helper method to build a color picker option for the config GUI.
     * Supports alpha channel for transparency.
     *
     * @param name Display name for the option
     * @param def Supplier providing the default value
     * @param get Supplier providing the current value
     * @param set Consumer to update the value
     * @return A configured Color option for YACL3
     */
    private static Option<Color> buildColor(String name, Supplier<Integer> def, Supplier<Integer> get, Consumer<Integer> set) {
        return Option.<Color>createBuilder()
            .name(Component.literal(name))
            .binding(new Color(def.get(), true), () -> new Color(get.get(), true), val -> set.accept(val.getRGB()))
            .controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(true))
            .build();
    }

    /**
     * Helper method to build a boolean toggle option for the config GUI.
     *
     * @param name Display name for the option
     * @param desc Description text shown when hovering over the option
     * @param def Supplier providing the default value
     * @param get Supplier providing the current value
     * @param set Consumer to update the value
     * @return A configured Boolean option for YACL3
     */
    private static Option<Boolean> buildBool(String name, String desc, Supplier<Boolean> def, Supplier<Boolean> get, Consumer<Boolean> set) {
        return Option.<Boolean>createBuilder()
            .name(Component.literal(name))
            .description(OptionDescription.of(Component.literal(desc)))
            .binding(def.get(), get, set)
            .controller(TickBoxControllerBuilder::create)
            .build();
    }

    /**
     * Helper method to build an integer slider option for the config GUI.
     *
     * @param name Display name for the option
     * @param min Minimum allowed value
     * @param max Maximum allowed value
     * @param def Supplier providing the default value
     * @param get Supplier providing the current value
     * @param set Consumer to update the value
     * @return A configured Integer option for YACL3 with slider controller
     */
    private static Option<Integer> buildIntSlider(String name, int min, int max, Supplier<Integer> def, Supplier<Integer> get, Consumer<Integer> set) {
        return Option.<Integer>createBuilder()
            .name(Component.literal(name))
            .binding(def.get(), get, set)
            .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(min, max).step(1))
            .build();
    }

    /**
     * Helper method to build a float slider option for the config GUI.
     *
     * @param name Display name for the option
     * @param min Minimum allowed value
     * @param max Maximum allowed value
     * @param def Supplier providing the default value
     * @param get Supplier providing the current value
     * @param set Consumer to update the value
     * @return A configured Float option for YACL3 with slider controller
     */
    private static Option<Float> buildFloatSlider(String name, float min, float max, Supplier<Float> def, Supplier<Float> get, Consumer<Float> set) {
        return Option.<Float>createBuilder()
            .name(Component.literal(name))
            .binding(def.get(), get, set)
            .controller(opt -> FloatSliderControllerBuilder.create(opt).range(min, max).step(0.1f))
            .build();
    }

    /**
     * Saves the current configuration to disk as JSON.
     * Called automatically when the config screen is closed.
     * The entire INSTANCE is serialized to preserve all settings.
     */
    public static void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(INSTANCE, writer); // Save the entire instance at once
        } catch (Exception e) { e.printStackTrace(); }
    }

    /**
     * Loads the configuration from disk.
     * If the config file doesn't exist, creates a new one with default values.
     * Called during client initialization.
     */
    public static void load() {
        if (!CONFIG_FILE.exists()) {
            save(); // Create default file if it doesn't exist
            return;
        }
        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            Handler loaded = GSON.fromJson(reader, Handler.class);
            if (loaded != null) INSTANCE = loaded; // Replace the whole instance
        } catch (Exception e) { e.printStackTrace(); }
    }
}