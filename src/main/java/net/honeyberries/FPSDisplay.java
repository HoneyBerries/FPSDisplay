package net.honeyberries;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main mod initialization class for FPS Display.
 * This class handles the initialization of the FPS Display mod on the server/common side.
 */
public class FPSDisplay implements ModInitializer {
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

	/**
	 * Called when the mod is initialized.
	 * This code runs as soon as Minecraft is in a mod-load-ready state.
	 * Note: Some things (like resources) may still be uninitialized at this point.
	 */
	@Override
	public void onInitialize() {

		LOGGER.info("FPS Display initialized!");
	}
}