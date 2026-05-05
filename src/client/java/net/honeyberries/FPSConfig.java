package net.honeyberries;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Configuration manager for the FPS Display mod.
 * <p>
 * This class handles:
 * <ul>
 *   <li>Loading configuration from disk (JSON format)</li>
 *   <li>Saving configuration changes</li>
 *   <li>Providing access to all config values via {@link #getInstance()}</li>
 *   <li>Defining default values for all settings</li>
 * </ul>
 * <p>
 * The configuration is stored at {@code config/fpsdisplay_config.json} in the game directory.
 * All settings are persisted to disk automatically when {@link #save()} is called.
 * <p>
 * Thread-safe for reading config values, but modifications should be followed by a call to {@link #save()}.
 */
public class FPSConfig {
    /** JSON serializer for loading and saving config. */
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /** Path to the configuration file in the game's config directory. */
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("fpsdisplay_config.json");

    /** Singleton instance holding the current configuration. */
    private static FPSConfig INSTANCE = new FPSConfig();

    // Default values for all configuration options
    /** Default: Enable FPS display on startup. */
    public static final boolean DEF_ENABLE_FPS = true;

    /** Default: Enable advanced statistics (1% and 0.1% lows). */
    public static final boolean DEF_ENABLE_ADVANCED_STATS = false;

    /** Default: Show the "FPS" text before the numerical value. */
    public static final boolean DEF_SHOW_FPS_TEXT = true;

    /** Default: Enable text shadow rendering. */
    public static final boolean DEF_ENABLE_SHADOW = false;

    /** Default: X offset from left edge of screen in pixels. */
    public static final int DEF_X = 2;

    /** Default: Y offset from top edge of screen in pixels. */
    public static final int DEF_Y = 2;

    /** Default: HUD scale multiplier (1.0 = normal size). */
    public static final float DEF_HUD_SCALE = 1.0f;

    /** Default: Text color in ARGB format (white, fully opaque). */
    public static final int DEF_HUD_COLOR = 0xFFFFFFFF;

    /** Default: Background color in ARGB format (fully transparent). */
    public static final int DEF_BG_COLOR = 0x00000000;

    // Configuration fields
    /** Whether the FPS display is currently enabled. */
    public boolean enableFps = DEF_ENABLE_FPS;

    /** Whether to show advanced statistics (1% and 0.1% low FPS). */
    public boolean enableAdvancedStats = DEF_ENABLE_ADVANCED_STATS;

    /** Whether to show the "FPS" text before the numerical value. */
    public boolean showFpsText = DEF_SHOW_FPS_TEXT;

    /** Whether to render a shadow behind the text. */
    public boolean enableShadow = DEF_ENABLE_SHADOW;

    /** X position offset from the left edge of the screen in pixels. */
    public int xOffset = DEF_X;

    /** Y position offset from the top edge of the screen in pixels. */
    public int yOffset = DEF_Y;

    /** Scale multiplier for the HUD (0.5 to 3.0). */
    public float hudScale = DEF_HUD_SCALE;

    /** Text color in ARGB format (alpha, red, green, blue). */
    public int hudColor = DEF_HUD_COLOR;

    /** Background box color in ARGB format (alpha, red, green, blue). */
    public int bgColor = DEF_BG_COLOR;

    /**
     * Gets the singleton configuration instance.
     * <p>
     * All configuration values are accessed through this instance.
     * Modifications to the returned instance should be followed by a call to {@link #save()}.
     *
     * @return The current configuration instance
     */
    public static FPSConfig getInstance() {
        return INSTANCE;
    }

    /**
     * Loads the configuration from disk.
     * <p>
     * This method:
     * <ul>
     *   <li>Reads the JSON config file from disk</li>
     *   <li>Deserializes it into a FPSConfig instance</li>
     *   <li>Replaces the singleton INSTANCE with loaded values</li>
     *   <li>Creates a default config file if none exists</li>
     *   <li>Logs errors if loading fails, using defaults instead</li>
     * </ul>
     * <p>
     * Should be called once during client initialization before using config values.
     * If the file doesn't exist, a new config with default values is created.
     *
     * @see #save()
     */
    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                String json = Files.readString(CONFIG_PATH);
                FPSConfig loaded = GSON.fromJson(json, FPSConfig.class);
                INSTANCE = loaded != null ? loaded : new FPSConfig();
                FPSDisplay.LOGGER.info("Config loaded from {}", CONFIG_PATH);
            } catch (IOException | JsonParseException e) {
                FPSDisplay.LOGGER.error("Failed to load config, using defaults", e);
            }
        } else {
            save();
        }
    }

    /**
     * Saves the current configuration to disk.
     * <p>
     * This method:
     * <ul>
     *   <li>Creates the config directory if it doesn't exist</li>
     *   <li>Serializes the singleton INSTANCE to JSON</li>
     *   <li>Writes the JSON to disk with pretty printing</li>
     *   <li>Logs errors if saving fails</li>
     * </ul>
     * <p>
     * Should be called whenever config values are modified to persist changes.
     * This is automatically called by the config screen when it closes.
     *
     * @see #load()
     */
    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            String json = GSON.toJson(INSTANCE);
            Files.writeString(CONFIG_PATH, json);
            FPSDisplay.LOGGER.info("Config saved to {}", CONFIG_PATH);
        } catch (IOException e) {
            FPSDisplay.LOGGER.error("Failed to save config", e);
        }
    }
}
