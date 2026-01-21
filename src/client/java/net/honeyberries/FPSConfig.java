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

public class FPSConfig {
    // This inner class holds all your data in one place
    public static class Handler {
        // 1. Define CONSTANT defaults (The "Factory Settings")
        private static final boolean DEF_ENABLE = true;
        private static final boolean DEF_ENABLE_ADVANCED = true;
        private static final boolean DEF_ENABLE_SHADOW = true;
        private static final int DEF_X = 2;
        private static final int DEF_Y = 2;
        private static final float DEF_SCALE = 1.0f;
        private static final int DEF_TEXT_COLOR = 0xFFFFFFFF;
        private static final int DEF_BG_COLOR = 0x00000000;

        // 2. Initialize the variables USING those defaults
        public boolean enableFps = DEF_ENABLE;
        public boolean enableAdvancedStats = DEF_ENABLE_ADVANCED;
        public boolean enableShadow = DEF_ENABLE_SHADOW;
        public int xOffset = DEF_X;
        public int yOffset = DEF_Y;
        public float hudScale = DEF_SCALE;
        public int hudColor = DEF_TEXT_COLOR;
        public int bgColor = DEF_BG_COLOR;
    }

    // This is the "Singleton" instance. Access it via FPSConfig.INSTANCE
    public static Handler INSTANCE = new Handler();

    private static final File CONFIG_FILE = new File(Minecraft.getInstance().gameDirectory, "config/fps_display_config.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

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

    // --- Helpers (Now pointing to Singleton Instance) ---
    private static Option<Color> buildColor(String name, Supplier<Integer> def, Supplier<Integer> get, Consumer<Integer> set) {
        return Option.<Color>createBuilder()
            .name(Component.literal(name))
            .binding(new Color(def.get(), true), () -> new Color(get.get(), true), val -> set.accept(val.getRGB()))
            .controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(true))
            .build();
    }

    private static Option<Boolean> buildBool(String name, String desc, Supplier<Boolean> def, Supplier<Boolean> get, Consumer<Boolean> set) {
        return Option.<Boolean>createBuilder()
            .name(Component.literal(name))
            .description(OptionDescription.of(Component.literal(desc)))
            .binding(def.get(), get, set)
            .controller(TickBoxControllerBuilder::create)
            .build();
    }

    private static Option<Integer> buildIntSlider(String name, int min, int max, Supplier<Integer> def, Supplier<Integer> get, Consumer<Integer> set) {
        return Option.<Integer>createBuilder()
            .name(Component.literal(name))
            .binding(def.get(), get, set)
            .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(min, max).step(1))
            .build();
    }

    private static Option<Float> buildFloatSlider(String name, float min, float max, Supplier<Float> def, Supplier<Float> get, Consumer<Float> set) {
        return Option.<Float>createBuilder()
            .name(Component.literal(name))
            .binding(def.get(), get, set)
            .controller(opt -> FloatSliderControllerBuilder.create(opt).range(min, max).step(0.1f))
            .build();
    }

    // --- Singleton Persistence ---
    public static void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(INSTANCE, writer); // Save the entire instance at once
        } catch (Exception e) { e.printStackTrace(); }
    }

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