package net.honeyberries.config;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.AutoGen;
import dev.isxander.yacl3.config.v2.api.autogen.ColorField;
import dev.isxander.yacl3.config.v2.api.autogen.FloatSlider;
import dev.isxander.yacl3.config.v2.api.autogen.IntSlider;
import dev.isxander.yacl3.config.v2.api.autogen.MasterTickBox;
import dev.isxander.yacl3.config.v2.api.autogen.TickBox;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.honeyberries.FPSDisplay;
import net.minecraft.resources.Identifier;

import java.awt.Color;

/**
 * Configuration for FPS Display, built on YACL's annotation-based {@code @AutoGen} system.
 * <p>
 * The config screen is generated from the annotations below. All labels live in
 * {@code en_us.json} under {@code yacl3.config.fpsdisplay:config.*}; the {@code checkConfigLangKeys}
 * Gradle task fails the build if a field, group or category is missing its text.
 * <p>
 * Field names must be unique across the class because option lang keys are scoped by field name only.
 * The config is stored at {@code config/fpsdisplay.json}.
 */
public class FPSDisplayConfig {
    public static final ConfigClassHandler<FPSDisplayConfig> HANDLER = ConfigClassHandler
            .createBuilder(FPSDisplayConfig.class)
            .id(Identifier.fromNamespaceAndPath(FPSDisplay.MOD_ID, "config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("fpsdisplay.json"))
                    .build())
            .build();

    /** @return the live config instance. */
    public static FPSDisplayConfig config() {
        return HANDLER.instance();
    }

    /** Saves the config off the render/tick thread. */
    public static void saveAsync() {
        Thread.startVirtualThread(HANDLER::save);
    }

    /** Frametime in ms below which a frame is "good". */
    public float yellowThresholdMs() {
        return 1000f / yellowThresholdFps;
    }

    /** Frametime in ms at or above which a frame is "bad". */
    public float redThresholdMs() {
        return 1000f / redThresholdFps;
    }

    private static final String GENERAL = "general";
    private static final String APPEARANCE = "appearance";

    // ── HUD ───────────────────────────────────────────────────────────────────

    @SerialEntry
    @AutoGen(category = GENERAL, group = "hud")
    @TickBox
    public boolean enableFps = true;

    @SerialEntry
    @AutoGen(category = GENERAL, group = "hud")
    @TickBox
    public boolean enableAdvancedStats = false;

    @SerialEntry
    @AutoGen(category = GENERAL, group = "hud")
    @TickBox
    public boolean showFpsText = true;

    // ── Positioning ───────────────────────────────────────────────────────────

    @SerialEntry
    @AutoGen(category = GENERAL, group = "positioning")
    @IntSlider(min = 0, max = 200, step = 1)
    public int xOffset = 2;

    @SerialEntry
    @AutoGen(category = GENERAL, group = "positioning")
    @IntSlider(min = 0, max = 200, step = 1)
    public int yOffset = 2;

    // ── Frametime graph ───────────────────────────────────────────────────────

    @SerialEntry
    @AutoGen(category = GENERAL, group = "frametime_graph")
    @MasterTickBox({"graphWidth", "graphHeight", "graphMaxMs", "graphColor",
            "yellowThresholdFps", "redThresholdFps", "yellowColor", "redColor"})
    public boolean enableFrametimeGraph = false;

    @SerialEntry
    @AutoGen(category = GENERAL, group = "frametime_graph")
    @IntSlider(min = 50, max = 500, step = 1)
    public int graphWidth = 100;

    @SerialEntry
    @AutoGen(category = GENERAL, group = "frametime_graph")
    @IntSlider(min = 10, max = 120, step = 1)
    public int graphHeight = 20;

    @SerialEntry
    @AutoGen(category = GENERAL, group = "frametime_graph")
    @FloatSlider(min = 5.0f, max = 200.0f, step = 0.5f, format = "%.1f")
    public float graphMaxMs = 50.0f;

    @SerialEntry
    @AutoGen(category = GENERAL, group = "frametime_graph")
    @ColorField(allowAlpha = true)
    public Color graphColor = new Color(0xFF00FF00, true);

    @SerialEntry
    @AutoGen(category = GENERAL, group = "frametime_graph")
    @IntSlider(min = 10, max = 240, step = 10)
    public int yellowThresholdFps = 60;

    @SerialEntry
    @AutoGen(category = GENERAL, group = "frametime_graph")
    @IntSlider(min = 10, max = 240, step = 10)
    public int redThresholdFps = 30;

    @SerialEntry
    @AutoGen(category = GENERAL, group = "frametime_graph")
    @ColorField(allowAlpha = true)
    public Color yellowColor = new Color(0xFFFFFF00, true);

    @SerialEntry
    @AutoGen(category = GENERAL, group = "frametime_graph")
    @ColorField(allowAlpha = true)
    public Color redColor = new Color(0xFFFF0000, true);

    // ── Text appearance ───────────────────────────────────────────────────────

    @SerialEntry
    @AutoGen(category = APPEARANCE, group = "text")
    @FloatSlider(min = 0.5f, max = 3.0f, step = 0.1f, format = "%.1f")
    public float hudScale = 1.0f;

    @SerialEntry
    @AutoGen(category = APPEARANCE, group = "text")
    @TickBox
    public boolean enableShadow = false;

    @SerialEntry
    @AutoGen(category = APPEARANCE, group = "text")
    @ColorField(allowAlpha = true)
    public Color hudColor = new Color(0xFFFFFFFF, true);

    @SerialEntry
    @AutoGen(category = APPEARANCE, group = "text")
    @ColorField(allowAlpha = true)
    public Color bgColor = new Color(0x00000000, true);
}
