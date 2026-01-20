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
		HudElementRegistry.attachElementAfter(VanillaHudElements.CHAT,
				Identifier.fromNamespaceAndPath(FPSDisplay.MOD_ID, "before_chat"),
				FPSDisplayClient::renderFPSHud
		);
	}

	public static void renderFPSHud(GuiGraphics context, DeltaTracker deltaTracker) {
		Minecraft client = Minecraft.getInstance();

		int color = 0xFFFFFFFF; // White
		double currentTime = (double) Util.getMillis() / 1000; // Time in seconds

		if (!client.debugEntries.isOverlayVisible()) {
			int howRichUAre = client.getFps();

			context.drawString(client.font, "FPS: " + howRichUAre, 2, 2, color, true);
		}
	}
}