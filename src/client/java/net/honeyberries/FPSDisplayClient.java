package net.honeyberries;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

public class FPSDisplayClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		FPSDisplay.LOGGER.info("FPS Display Mod Initialized");
		HudElementRegistry.attachElementBefore(VanillaHudElements.HOTBAR,
				Identifier.fromNamespaceAndPath(FPSDisplay.MOD_ID, "FPS Data"),
				FPSDisplayClient::renderFPSHud
		);
	}

	public static void renderFPSHud(GuiGraphics context, DeltaTracker deltaTracker) {
		Minecraft client = Minecraft.getInstance();

		int color = 0xFFFFFFFF; // White
        long frameTimeNs = client.getFrameTimeNs();

		if (!client.debugEntries.isOverlayVisible()) {
			double fps = 1_000_000_000.0 / frameTimeNs;

			context.drawString(
					client.font,
					"FPS: %.0f".formatted(fps),
					2, 2, color, true
			);

			context.drawString(
					client.font,
					"FrameTime (ms): %.1f".formatted(frameTimeNs / 1_000_000.0),
					2, 12, color, true
			);

		}
	}
}