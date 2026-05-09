package net.honeyberries;

import java.util.Arrays;

/**
 * Tracks and calculates FPS statistics over a rolling time window.
 * Uses a single ring buffer of frame-to-frame deltas (in nanoseconds) for both
 * the frametime graph and all FPS metrics (average, 1% low, 0.1% low).
 * <p>
 * All methods are thread-safe and use synchronized access to internal data.
 */
public final class FPSStats {
    /** Rolling window size in nanoseconds (5 seconds). */
    private static final long WINDOW_NS = 5_000_000_000L;

    /** Minimum interval between stats recalculations in nanoseconds (0.5 seconds). */
    private static final long UPDATE_INTERVAL_NS = 500_000_000L;

    /** Ring buffer capacity — enough for ~1 minute at 60 FPS or ~12 seconds at 300 FPS. */
    private static final int CAPACITY = 3600;

    /** Ring buffer of frame-to-frame intervals in nanoseconds. */
    private static final long[] frametimeNs = new long[CAPACITY];

    /** Index of the oldest entry in the ring buffer. */
    private static int head = 0;

    /** Number of valid entries in the ring buffer. */
    private static int size = 0;

    /** Running sum of all nanosecond deltas currently in the buffer. */
    private static long totalWindowNs = 0;

    /** Timestamp of the previous frame for computing deltas. */
    private static long lastFrameNs = 0;

    private static volatile long lastUpdateTime = 0;
    private static volatile double avgFps = 0.0;
    private static volatile double onePercentFps = 0.0;
    private static volatile double pointOnePercentFps = 0.0;

    private FPSStats() {}

    /**
     * Records a new frame and updates statistics if needed.
     * Call once per frame with the current {@link System#nanoTime()} value.
     *
     * @param now Current time in nanoseconds
     */
    public static synchronized void recordFrame(long now) {
        if (lastFrameNs > 0) {
            long dt = now - lastFrameNs;
            if (dt > 0) addDelta(dt);
        }
        lastFrameNs = now;

        if (now - lastUpdateTime > UPDATE_INTERVAL_NS) {
            calculateStats();
            lastUpdateTime = now;
        }
    }

    /** Formatted average FPS string, optionally prefixed with "FPS: ". */
    public static String getDisplayStringAvg(boolean showFpsText) {
        return showFpsText
                ? String.format("FPS: %.0f", avgFps)
                : String.format("%.0f", avgFps);
    }

    /** Formatted 1% / 0.1% low FPS string. */
    public static String getDisplayStringLows() {
        return String.format("1%% Low: %.0f | 0.1%% Low: %.0f",
                Math.max(0.0, onePercentFps),
                Math.max(0.0, pointOnePercentFps));
    }

    /**
     * Returns the most recent {@code count} frametime samples as milliseconds, oldest first.
     * The returned array may be shorter than {@code count} if fewer samples exist.
     *
     * @param count Maximum number of samples to return
     * @return Ordered array of frametime values in milliseconds
     */
    public static synchronized float[] getRecentFrametimes(int count) {
        int n = Math.min(count, size);
        float[] result = new float[n];
        int start = (head + size - n + CAPACITY) % CAPACITY;
        for (int i = 0; i < n; i++) {
            result[i] = frametimeNs[(start + i) % CAPACITY] / 1_000_000.0f;
        }
        return result;
    }

    /**
     * Adds a delta to the ring buffer, evicting the oldest entry if full,
     * then trims the window so the total time stays within {@link #WINDOW_NS}.
     */
    private static void addDelta(long ns) {
        if (size == CAPACITY) {
            totalWindowNs -= frametimeNs[head];
            head = (head + 1) % CAPACITY;
            size--;
        }
        frametimeNs[(head + size) % CAPACITY] = ns;
        size++;
        totalWindowNs += ns;

        while (size > 1 && totalWindowNs > WINDOW_NS) {
            totalWindowNs -= frametimeNs[head];
            head = (head + 1) % CAPACITY;
            size--;
        }
    }

    private static synchronized void calculateStats() {
        int n = size;
        if (n <= 0) {
            avgFps = 0.0;
            onePercentFps = 0.0;
            pointOnePercentFps = 0.0;
            return;
        }

        double[] durations = new double[n];
        for (int i = 0; i < n; i++) {
            durations[i] = frametimeNs[(head + i) % CAPACITY] / 1_000_000_000.0;
            if (durations[i] <= 0) durations[i] = 1e-9;
        }

        double totalSec = totalWindowNs / 1_000_000_000.0;
        avgFps = (totalSec > 0) ? (n / totalSec) : 0.0;

        Arrays.sort(durations);

        int k1  = Math.max(1, (int) Math.ceil(n * 0.01));
        int k01 = Math.max(1, (int) Math.ceil(n * 0.001));
        onePercentFps      = avgWorstFps(durations, k1);
        pointOnePercentFps = avgWorstFps(durations, k01);
    }

    private static double avgWorstFps(double[] durations, int k) {
        int n = durations.length;
        if (n == 0 || k <= 0) return 0.0;
        double sum = 0.0;
        for (int i = n - k; i < n; i++) sum += durations[i];
        double avgDur = sum / k;
        return (avgDur > 0) ? (1.0 / avgDur) : 0.0;
    }
}
