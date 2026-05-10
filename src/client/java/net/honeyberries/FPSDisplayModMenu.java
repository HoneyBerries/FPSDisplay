package net.honeyberries;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import com.mojang.blaze3d.platform.Window;

import java.awt.Color;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * ModMenu integration for FPS Display.
 * <p>
 * Provides a configuration screen accessible from the ModMenu mods list.
 * The screen is built using YACL3 (Yet Another Config Lib) and includes:
 * <ul>
 *   <li>General category with HUD toggles and positioning settings</li>
 *   <li>Appearance category with scale, colors, and shadow options</li>
 * </ul>
 * <p>
 * All changes made in the config screen are automatically bound to {@link FPSConfig} values
 * and persisted to disk when the screen closes.
 *
 * @see FPSConfig
 */
public class FPSDisplayModMenu implements ModMenuApi {
    /**
     * Provides the configuration screen factory for ModMenu.
     * <p>
     * When a player clicks the config button in ModMenu, this factory is used
     * to create the FPS Display configuration screen.
     *
     * @return A factory that creates the config screen with the given parent screen
     */
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return this::createConfigScreen;
    }

    /**
     * Creates and returns the FPS Display configuration screen.
     * <p>
     * The screen includes two categories:
     * <ul>
     *   <li><strong>General:</strong> HUD enablement, advanced stats toggle, text display, positioning</li>
     *   <li><strong>Appearance:</strong> Scale, colors (text and background), shadow</li>
     * </ul>
     * <p>
     * All options are bound directly to {@link FPSConfig} and changes are saved
     * automatically when the screen closes via {@link FPSConfig#save()}.
     *
     * @param parent The parent screen to return to when closing the config screen
     * @return A fully configured YACL3 config screen
     */
    private Screen createConfigScreen(Screen parent) {
        Window window = Minecraft.getInstance().getWindow();
        int maxX = window.getGuiScaledWidth();
        int maxY = window.getGuiScaledHeight();

        return YetAnotherConfigLib.createBuilder()
            .title(Component.translatable("config.fpsdisplay.title"))
            .category(ConfigCategory.createBuilder()
                .name(Component.translatable("config.fpsdisplay.category.general"))
                .group(OptionGroup.createBuilder()
                    .name(Component.translatable("config.fpsdisplay.group.hud"))
                    .option(createBoolOption(
                        "config.fpsdisplay.enable_fps",
                        FPSConfig.DEF_ENABLE_FPS,
                        () -> FPSConfig.getInstance().enableFps,
                        v -> FPSConfig.getInstance().enableFps = v))
                    .option(createBoolOption(
                        "config.fpsdisplay.enable_advanced_stats",
                        FPSConfig.DEF_ENABLE_ADVANCED_STATS,
                        () -> FPSConfig.getInstance().enableAdvancedStats,
                        v -> FPSConfig.getInstance().enableAdvancedStats = v))
                    .option(createBoolOption(
                        "config.fpsdisplay.show_fps_text",
                        FPSConfig.DEF_SHOW_FPS_TEXT,
                        () -> FPSConfig.getInstance().showFpsText,
                        v -> FPSConfig.getInstance().showFpsText = v))
                    .build())
                .group(OptionGroup.createBuilder()
                    .name(Component.translatable("config.fpsdisplay.group.positioning"))
                    .option(createIntSliderOption(
                        "config.fpsdisplay.x_offset",
                        FPSConfig.DEF_X,
                        0, maxX,
                        () -> FPSConfig.getInstance().xOffset,
                        v -> FPSConfig.getInstance().xOffset = v))
                    .option(createIntSliderOption(
                        "config.fpsdisplay.y_offset",
                        FPSConfig.DEF_Y,
                        0, maxY,
                        () -> FPSConfig.getInstance().yOffset,
                        v -> FPSConfig.getInstance().yOffset = v))
                    .build())
                .group(OptionGroup.createBuilder()
                    .name(Component.translatable("config.fpsdisplay.group.frametime_graph"))
                    .option(createBoolOption(
                        "config.fpsdisplay.enable_frametime_graph",
                        FPSConfig.DEF_ENABLE_FRAMETIME_GRAPH,
                        () -> FPSConfig.getInstance().enableFrametimeGraph,
                        v -> FPSConfig.getInstance().enableFrametimeGraph = v))
                    .option(createIntSliderOption(
                        "config.fpsdisplay.graph_width",
                        FPSConfig.DEF_GRAPH_WIDTH,
                        50, 500,
                        () -> FPSConfig.getInstance().graphWidth,
                        v -> FPSConfig.getInstance().graphWidth = v))
                    .option(createIntSliderOption(
                        "config.fpsdisplay.graph_height",
                        FPSConfig.DEF_GRAPH_HEIGHT,
                        10, 120,
                        () -> FPSConfig.getInstance().graphHeight,
                        v -> FPSConfig.getInstance().graphHeight = v))
                    .option(createFloatSliderOption(
                        "config.fpsdisplay.graph_max_ms",
                        FPSConfig.DEF_GRAPH_MAX_MS,
                        5.0f, 200.0f, 0.5f,
                        () -> FPSConfig.getInstance().graphMaxMs,
                        v -> FPSConfig.getInstance().graphMaxMs = v))
                    .option(createColorOption(
                        "config.fpsdisplay.graph_color",
                        FPSConfig.DEF_GRAPH_COLOR,
                        () -> FPSConfig.getInstance().graphColor,
                        v -> FPSConfig.getInstance().graphColor = v))
                    .option(createFpsThresholdSliderOption(
                        "config.fpsdisplay.yellow_threshold_fps",
                        FPSConfig.DEF_YELLOW_THRESHOLD_MS,
                            () -> FPSConfig.getInstance().yellowThresholdMs,
                        v -> FPSConfig.getInstance().yellowThresholdMs = v))
                    .option(createFpsThresholdSliderOption(
                        "config.fpsdisplay.red_threshold_fps",
                        FPSConfig.DEF_RED_THRESHOLD_MS,
                            () -> FPSConfig.getInstance().redThresholdMs,
                        v -> FPSConfig.getInstance().redThresholdMs = v))
                    .option(createColorOption(
                        "config.fpsdisplay.yellow_color",
                        FPSConfig.DEF_YELLOW_COLOR,
                        () -> FPSConfig.getInstance().yellowColor,
                        v -> FPSConfig.getInstance().yellowColor = v))
                    .option(createColorOption(
                        "config.fpsdisplay.red_color",
                        FPSConfig.DEF_RED_COLOR,
                        () -> FPSConfig.getInstance().redColor,
                        v -> FPSConfig.getInstance().redColor = v))
                    .build())
                .build())
            .category(ConfigCategory.createBuilder()
                .name(Component.translatable("config.fpsdisplay.category.appearance"))
                .option(createFloatSliderOption(
                    "config.fpsdisplay.hud_scale",
                    FPSConfig.DEF_HUD_SCALE,
                    0.5f, 3.0f, 0.1f,
                    () -> FPSConfig.getInstance().hudScale,
                    v -> FPSConfig.getInstance().hudScale = v))
                .option(createBoolOption(
                    "config.fpsdisplay.text_shadow",
                    FPSConfig.DEF_ENABLE_SHADOW,
                    () -> FPSConfig.getInstance().enableShadow,
                    v -> FPSConfig.getInstance().enableShadow = v))
                .option(createColorOption(
                    "config.fpsdisplay.text_color",
                    FPSConfig.DEF_HUD_COLOR,
                    () -> FPSConfig.getInstance().hudColor,
                    v -> FPSConfig.getInstance().hudColor = v))
                .option(createColorOption(
                    "config.fpsdisplay.bg_color",
                    FPSConfig.DEF_BG_COLOR,
                    () -> FPSConfig.getInstance().bgColor,
                    v -> FPSConfig.getInstance().bgColor = v))
                .build())
            .save(FPSConfig::save)
            .build()
            .generateScreen(parent);
    }

    /**
     * Creates a boolean toggle option for the config screen.
     * <p>
     * This helper creates a simple on/off toggle that displays the option name and
     * description from translation keys.
     *
     * @param nameKey Translation key for the option name (description key is assumed to be {@code nameKey + ".description"})
     * @param defaultValue The default value for this option
     * @param getter Supplies the current value from the config
     * @param setter Accepts new values to update the config
     * @return A configured boolean option ready to be added to the config screen
     */
    private static Option<Boolean> createBoolOption(String nameKey, boolean defaultValue, java.util.function.Supplier<Boolean> getter, java.util.function.Consumer<Boolean> setter) {
        return Option.<Boolean>createBuilder()
            .name(Component.translatable(nameKey))
            .description(OptionDescription.of(Component.translatable(nameKey + ".description")))
            .binding(defaultValue, getter, setter)
            .controller(BooleanControllerBuilder::create)
            .build();
    }

    /**
     * Creates an integer slider option for the config screen.
     * <p>
     * This helper creates a slider that allows selecting an integer value within a specified range.
     * The slider increments by 1 step.
     *
     * @param nameKey Translation key for the option name (description key is assumed to be {@code nameKey + ".description"})
     * @param defaultValue The default value for this option
     * @param min The minimum allowed value
     * @param max The maximum allowed value
     * @param getter Supplies the current value from the config
     * @param setter Accepts new values to update the config
     * @return A configured integer slider option ready to be added to the config screen
     */
    private static Option<Integer> createIntSliderOption(String nameKey, int defaultValue, int min, int max, java.util.function.Supplier<Integer> getter, java.util.function.Consumer<Integer> setter) {
        return Option.<Integer>createBuilder()
            .name(Component.translatable(nameKey))
            .description(OptionDescription.of(Component.translatable(nameKey + ".description")))
            .binding(defaultValue, getter, setter)
            .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(min, max).step(1))
            .build();
    }

    /**
     * Creates a float slider option for the config screen.
     * <p>
     * This helper creates a slider that allows selecting a floating-point value within a specified range.
     *
     * @param nameKey Translation key for the option name (description key is assumed to be {@code nameKey + ".description"})
     * @param defaultValue The default value for this option
     * @param min The minimum allowed value
     * @param max The maximum allowed value
     * @param step The step increment for the slider
     * @param getter Supplies the current value from the config
     * @param setter Accepts new values to update the config
     * @return A configured float slider option ready to be added to the config screen
     */
    private static Option<Float> createFloatSliderOption(String nameKey, float defaultValue, float min, float max, float step, java.util.function.Supplier<Float> getter, java.util.function.Consumer<Float> setter) {
        return Option.<Float>createBuilder()
            .name(Component.translatable(nameKey))
            .description(OptionDescription.of(Component.translatable(nameKey + ".description")))
            .binding(defaultValue, getter, setter)
            .controller(opt -> FloatSliderControllerBuilder.create(opt).range(min, max).step(step))
            .build();
    }

    /**
     * Creates a FPS-based threshold slider that converts to/from milliseconds.
     * <p>
     * This helper displays FPS values to the user (e.g., 60 FPS) but stores
     * the equivalent milliseconds in the config (e.g., 16.7 ms).
     *
     * @param nameKey   Translation key for the option name
     * @param defaultMs The default value in milliseconds
     * @param getter    Supplies the current value in milliseconds from config
     * @param setter    Accepts new values in milliseconds to update config
     * @return A configured FPS slider option ready to be added to the config screen
     */
    private static Option<Float> createFpsThresholdSliderOption(String nameKey, float defaultMs, Supplier<Float> getter, Consumer<Float> setter) {
        float defaultFps = 1000f / defaultMs;
        return Option.<Float>createBuilder()
            .name(Component.translatable(nameKey))
            .description(OptionDescription.of(Component.translatable(nameKey + ".description")))
            .binding(defaultFps,
                () -> 1000f / getter.get(),
                fps -> setter.accept(1000f / fps))
            .controller(opt -> FloatSliderControllerBuilder.create(opt).range(10f, 240f).step(10f))
            .build();
    }

    /**
     * Creates a color picker option for the config screen.
     * <p>
     * This helper creates a color picker that supports the full ARGB spectrum,
     * allowing players to customize colors with alpha transparency.
     *
     * @param nameKey Translation key for the option name (description key is assumed to be {@code nameKey + ".description"})
     * @param defaultValue The default color value in ARGB format
     * @param getter Supplies the current color value from the config
     * @param setter Accepts new color values to update the config
     * @return A configured color picker option ready to be added to the config screen
     */
    private static Option<Color> createColorOption(String nameKey, int defaultValue, java.util.function.Supplier<Integer> getter, java.util.function.Consumer<Integer> setter) {
        return Option.<Color>createBuilder()
            .name(Component.translatable(nameKey))
            .description(OptionDescription.of(Component.translatable(nameKey + ".description")))
            .binding(new Color(defaultValue, true), () -> new Color(getter.get(), true), v -> setter.accept(v.getRGB()))
            .controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(true))
            .build();
    }
}
