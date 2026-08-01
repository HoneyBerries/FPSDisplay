package net.honeyberries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Shared constants for the FPS Display mod (mod id, logger).
 * This mod is client-only, so all actual initialization happens in {@link FPSDisplayClient}.
 */
public class FPSDisplay {
	/**
	 * The unique identifier for this mod.
	 * Used throughout the mod for registration, logging, and resource identification.
	 */
	public static final String MOD_ID = "fpsdisplay";

	/**
	 * Logger instance for this mod.
	 * Used to write text to the console and the log file.
	 * It is considered best practice to use your mod id as the logger's name
	 * so it's clear which mod wrote info, warnings, and errors.
	 */
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
}