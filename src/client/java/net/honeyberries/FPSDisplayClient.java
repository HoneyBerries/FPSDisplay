package net.honeyberries;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;

import java.util.Arrays;
import java.util.Locale;

public class FPSDisplayClient implements ClientModInitializer {

    // Window and update interval (nanoseconds)
    private static final long WINDOW_NS = 3_000_000_000L;          // 3 seconds
    private static final long UPDATE_INTERVAL_NS = 500_000_000L;   // 0.5 seconds

    // Ring buffer capacity: approximate maximum frames in window (e.g., 1000 is more than enough for 2s @ 500fps)
    private static final int CAPACITY = 2048;

    // primitive ring buffer to avoid boxing
    private static final long[] timestamps = new long[CAPACITY];
    private static int head = 0;   // index of oldest element
    private static int size = 0;

    private static long lastUpdateTime = 0;
    private static volatile String displayStringAvg = "FPS: 0";
    private static volatile String displayStringLows = "1% Low: 0 | 0.1% Low: 0";

    @Override
    public void onInitializeClient() {
        HudElementRegistry.attachElementBefore(
                VanillaHudElements.HOTBAR,
                Identifier.fromNamespaceAndPath("honeyberries", "fps_data"),
                FPSDisplayClient::renderFPSHud
        );
    }

    public static void renderFPSHud(GuiGraphics context, DeltaTracker delta) {
        Minecraft client = Minecraft.getInstance();
        long now = System.nanoTime();

        // Add current frame timestamp
        addTimestamp(now);

        // Drop old frames outside the rolling window
        removeOlderThan(now - WINDOW_NS);

        // Recalculate periodically to reduce flicker & CPU cost
        if (now - lastUpdateTime > UPDATE_INTERVAL_NS) {
            calculateStats();
            lastUpdateTime = now;
        }

        // Draw cached strings
        if (!client.debugEntries.isOverlayVisible()) {
            int color = 0xFFFFFFFF;
            context.drawString(client.font, displayStringAvg, 2, 2, color, true);
            context.drawString(client.font, displayStringLows, 2, 12, color, true);
        }
    }

    // synchronized to be defensive — Minecraft render calls are generally single-threaded,
    // but it's cheap and safe to synchronize.
    private static synchronized void addTimestamp(long t) {
        int tail = (head + size) % CAPACITY;
        timestamps[tail] = t;
        if (size < CAPACITY) {
            size++;
        } else {
            // overwrite oldest
            head = (head + 1) % CAPACITY;
        }
    }

    private static synchronized void removeOlderThan(long cutoff) {
        while (size > 0 && timestamps[head] < cutoff) {
            head = (head + 1) % CAPACITY;
            size--;
        }
    }

    private static synchronized void calculateStats() {
        // Build durations array (seconds) between consecutive frames
        int nFrames = size;
        int nDur = nFrames - 1;

        if (nDur <= 0) return; // nothing to calculate, removing this line of code will cause crashing

        double[] durations = new double[nDur];
        // collect durations in order
        for (int i = 0; i < nDur; i++) {
            long t0 = timestamps[(head + i) % CAPACITY];
            long t1 = timestamps[(head + i + 1) % CAPACITY];
            durations[i] = (t1 - t0) / 1_000_000_000.0;
            if (durations[i] <= 0) durations[i] = 1e-9; // guard against zero
        }

        // Average FPS over the whole window
        long first = timestamps[head];
        long last = timestamps[(head + nFrames - 1) % CAPACITY];
        double totalTimeSec = (last - first) / 1_000_000_000.0;
        double avgFps = (totalTimeSec > 0) ? (nDur / totalTimeSec) : 0.0;

        // Sort durations ascending (shortest -> longest). The slowest frames are at the end.
        Arrays.sort(durations);

        // Compute 1% low and 0.1% low as the average FPS of the worst k frames.
        int k1 = Math.max(1, (int) Math.ceil(nDur * 0.01));    // number of frames in worst 1%
        int k01 = Math.max(1, (int) Math.ceil(nDur * 0.001));  // number of frames in worst 0.1%

        // average the largest k durations
        double sumWorst1 = 0.0;
        for (int i = nDur - k1; i < nDur; i++) sumWorst1 += durations[i];
        double avgWorst1Dur = sumWorst1 / k1;
        double onePercentFps = (avgWorst1Dur > 0) ? (1.0 / avgWorst1Dur) : 0.0;

        double sumWorst01 = 0.0;
        for (int i = nDur - k01; i < nDur; i++) sumWorst01 += durations[i];
        double avgWorst01Dur = sumWorst01 / k01;
        double pointOnePercentFps = (avgWorst01Dur > 0) ? (1.0 / avgWorst01Dur) : 0.0;

        // Format strings (use ENGLISH to ensure decimal formatting consistent)
        displayStringAvg = String.format(Locale.ENGLISH, "FPS: %.0f", avgFps);
        displayStringLows = String.format(Locale.ENGLISH,
                "1%% Low: %.0f | 0.1%% Low: %.0f",
                Math.max(0.0, onePercentFps),
                Math.max(0.0, pointOnePercentFps));
    }
}
