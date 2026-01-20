package net.honeyberries;

import java.util.Arrays;

public final class FPSStats {
    // Rolling window size for FPS stats (in nanoseconds)
    private static final long WINDOW_NS = 5_000_000_000L;          // 5 seconds
    // How often to update the displayed stats (in nanoseconds)
    private static final long UPDATE_INTERVAL_NS = 500_000_000L;   // 0.5 seconds
    // Maximum number of frame timestamps to keep (ring buffer size)
    private static final int CAPACITY = 3600;

    // Ring buffer for frame timestamps (in nanoseconds)
    private static final long[] timestamps = new long[CAPACITY];
    private static int head = 0;   // Index of the oldest timestamp
    private static int size = 0;   // Number of valid timestamps in buffer
    private static volatile long lastUpdateTime = 0; // Last time stats were updated (visible across threads)

    // Latest calculated stats (numeric only; strings formatted on demand)
    private static volatile double avgFps = 0.0;
    private static volatile double onePercentFps = 0.0;
    private static volatile double pointOnePercentFps = 0.0;

    private FPSStats() {
    }

    /**
     * Call this once per frame to record the current time and update stats if needed.
     * @param now Current time in nanoseconds (System.nanoTime())
     */
    public static synchronized void recordFrame(long now) {
        addTimestamp(now); // Add this frame's timestamp
        removeOlderThan(now - WINDOW_NS); // Remove frames outside the rolling window

        // Only recalculate stats periodically to reduce CPU usage
        if (now - lastUpdateTime > UPDATE_INTERVAL_NS) {
            calculateStats();
            lastUpdateTime = now;
        }
    }

    /**
     * @return Average FPS display string, formatted on demand
     */
    public static String getDisplayStringAvg() {
        return String.format("FPS: %.0f", avgFps);
    }

    /**
     * @return 1%/0.1% low FPS display string, formatted on demand
     */
    public static String getDisplayStringLows() {
        return String.format("1%% Low: %.0f | 0.1%% Low: %.0f",
                Math.max(0.0, onePercentFps),
                Math.max(0.0, pointOnePercentFps));
    }

    // Add a new timestamp to the ring buffer
    private static synchronized void addTimestamp(long t) {
        int tail = (head + size) % CAPACITY;
        timestamps[tail] = t;
        if (size < CAPACITY) {
            size++;
        } else {
            head = (head + 1) % CAPACITY; // Overwrite oldest if full
        }
    }

    // Remove timestamps older than the cutoff (keep only recent frames)
    private static synchronized void removeOlderThan(long cutoff) {
        while (size > 0 && timestamps[head] < cutoff) {
            head = (head + 1) % CAPACITY;
            size--;
        }
    }

    /**
     * Recalculate FPS stats and update display strings.
     * Uses a rolling window of recent frame times.
     * - Average FPS: total frames / total time
     * - 1% low: average FPS of the slowest 1% of frames
     * - 0.1% low: average FPS of the slowest 0.1% of frames
     */
    private static synchronized void calculateStats() {
        int nFrames = size;
        int nDur = nFrames - 1; // Number of frame intervals (durations)
        if (nDur <= 0) {
            avgFps = 0.0;
            onePercentFps = 0.0;
            pointOnePercentFps = 0.0;
            return; // Not enough data
        }

        // Compute durations (seconds) between consecutive frames
        double[] durations = new double[nDur];
        for (int i = 0; i < nDur; i++) {
            long t0 = timestamps[(head + i) % CAPACITY];
            long t1 = timestamps[(head + i + 1) % CAPACITY];
            durations[i] = (t1 - t0) / 1_000_000_000.0; // Convert ns to seconds
            if (durations[i] <= 0) durations[i] = 1e-9; // Guard against zero/negative
        }

        // Average FPS: total frames divided by total time in window
        long first = timestamps[head];
        long last = timestamps[(head + nFrames - 1) % CAPACITY];
        double totalTimeSec = (last - first) / 1_000_000_000.0;
        avgFps = (totalTimeSec > 0) ? (nDur / totalTimeSec) : 0.0;

        // Sort durations ascending (shortest to longest)
        Arrays.sort(durations);

        // 1% low: average FPS of the slowest 1% of frames
        int k1 = Math.max(1, (int) Math.ceil(nDur * 0.01));
        // 0.1% low: average FPS of the slowest 0.1% of frames
        int k01 = Math.max(1, (int) Math.ceil(nDur * 0.001));

        // Compute 1% and 0.1% low FPS using helper
        onePercentFps = avgWorstFps(durations, k1);
        pointOnePercentFps = avgWorstFps(durations, k01);
    }

    /**
     * Helper to compute average FPS for the worst k durations (lowest FPS frames).
     * @param durations Sorted array of frame durations (seconds), ascending
     * @param k Number of slowest frames to average
     * @return Average FPS for the slowest k frames
     */
    private static double avgWorstFps(double[] durations, int k) {
        int n = durations.length;
        if (n == 0 || k <= 0) return 0.0;
        double sum = 0.0;
        for (int i = n - k; i < n; i++) sum += durations[i];
        double avgDur = sum / k;
        return (avgDur > 0) ? (1.0 / avgDur) : 0.0;
    }
}
