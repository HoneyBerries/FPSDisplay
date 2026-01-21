package net.honeyberries;

import java.util.Arrays;

/**
 * Tracks and calculates FPS (Frames Per Second) statistics over a rolling time window.
 * This class maintains a ring buffer of frame timestamps and calculates:
 * - Average FPS
 * - 1% low FPS (average FPS of the slowest 1% of frames)
 * - 0.1% low FPS (average FPS of the slowest 0.1% of frames)
 * <p>
 * All methods are thread-safe and use synchronized access to internal data.
 */
public final class FPSStats {
    /**
     * Rolling window size for FPS statistics in nanoseconds (2 seconds).
     * Only frames within this time window are included in calculations.
     */
    private static final long WINDOW_NS = 2_000_000_000L;

    /**
     * How often to update the displayed statistics in nanoseconds (0.5 seconds).
     * This reduces CPU usage by avoiding recalculation every frame.
     */
    private static final long UPDATE_INTERVAL_NS = 500_000_000L;

    /**
     * Maximum number of frame timestamps to keep in the ring buffer.
     * Supports tracking up to 3600 frames (sufficient for ~1 minute at 60 FPS).
     */
    private static final int CAPACITY = 3600;

    /**
     * Ring buffer storing frame timestamps in nanoseconds.
     * Uses a circular buffer pattern with head and size pointers.
     */
    private static final long[] timestamps = new long[CAPACITY];

    /**
     * Index of the oldest timestamp in the ring buffer.
     * Used with size to determine valid data range.
     */
    private static int head = 0;

    /**
     * Number of valid timestamps currently in the buffer.
     * Can be less than CAPACITY if the buffer isn't full yet.
     */
    private static int size = 0;

    /**
     * Last time statistics were recalculated (in nanoseconds).
     * Volatile to ensure visibility across threads.
     */
    private static volatile long lastUpdateTime = 0;

    /**
     * Latest calculated average FPS value.
     * Volatile to ensure visibility across threads.
     */
    private static volatile double avgFps = 0.0;

    /**
     * Latest calculated 1% low FPS value (average FPS of slowest 1% of frames).
     * Volatile to ensure visibility across threads.
     */
    private static volatile double onePercentFps = 0.0;

    /**
     * Latest calculated 0.1% low FPS value (average FPS of slowest 0.1% of frames).
     * Volatile to ensure visibility across threads.
     */
    private static volatile double pointOnePercentFps = 0.0;

    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with only static methods.
     */
    private FPSStats() {
    }

    /**
     * Records a new frame timestamp and updates statistics if needed.
     * Call this once per frame to track FPS metrics.
     * <p>
     * This method:
     * 1. Adds the current frame's timestamp to the ring buffer
     * 2. Removes frames outside the rolling window
     * 3. Recalculates statistics if UPDATE_INTERVAL_NS has passed
     *
     * @param now Current time in nanoseconds (typically from System.nanoTime())
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
     * Gets a formatted string displaying the average FPS.
     * The string is formatted on demand to avoid storing stale text.
     *
     * @return A string in the format "FPS: XX" where XX is the average FPS rounded to nearest integer
     */
    public static String getDisplayStringAvg() {
        return String.format("FPS: %.0f", avgFps);
    }

    /**
     * Gets a formatted string displaying the 1% and 0.1% low FPS values.
     * The string is formatted on demand to avoid storing stale text.
     * Negative values are clamped to 0.
     *
     * @return A string in the format "1% Low: XX | 0.1% Low: YY" where XX and YY are FPS values
     */
    public static String getDisplayStringLows() {
        return String.format("1%% Low: %.0f | 0.1%% Low: %.0f",
                Math.max(0.0, onePercentFps),
                Math.max(0.0, pointOnePercentFps));
    }

    /**
     * Adds a new timestamp to the ring buffer.
     * If the buffer is full, the oldest timestamp is overwritten.
     *
     * @param t The timestamp to add (in nanoseconds)
     */
    private static synchronized void addTimestamp(long t) {
        int tail = (head + size) % CAPACITY;
        timestamps[tail] = t;
        if (size < CAPACITY) {
            size++;
        } else {
            head = (head + 1) % CAPACITY; // Overwrite oldest if full
        }
    }

    /**
     * Removes timestamps older than the specified cutoff time from the ring buffer.
     * This maintains the rolling window by discarding frames that are too old.
     *
     * @param cutoff Timestamp in nanoseconds; frames older than this are removed
     */
    private static synchronized void removeOlderThan(long cutoff) {
        while (size > 0 && timestamps[head] < cutoff) {
            head = (head + 1) % CAPACITY;
            size--;
        }
    }

    /**
     * Recalculates all FPS statistics using the current rolling window of frame timestamps.
     * <p>
     * This method calculates:
     * - Average FPS: Total frames divided by total time in the window
     * - 1% low FPS: Average FPS of the slowest 1% of frames
     * - 0.1% low FPS: Average FPS of the slowest 0.1% of frames
     * <p>
     * The algorithm:
     * 1. Computes duration (in seconds) between consecutive frames
     * 2. Sorts durations to identify the slowest frames
     * 3. Calculates average FPS for the worst k% of frames
     * <p>
     * Low% FPS metrics help identify performance stutters that average FPS might hide.
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
     * Calculates the average FPS for the worst (slowest) k frames.
     * Used to compute 1% and 0.1% low FPS metrics.
     *
     * @param durations Sorted array of frame durations in seconds (ascending order)
     * @param k Number of slowest frames to average (from the end of the sorted array)
     * @return Average FPS for the slowest k frames, or 0.0 if invalid input
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
