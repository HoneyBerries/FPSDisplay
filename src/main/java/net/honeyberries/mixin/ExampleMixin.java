package net.honeyberries.mixin;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Example mixin for server-side code injection.
 * This mixin targets MinecraftServer and demonstrates how to inject code
 * at the beginning of a method.
 * <p>
 * Currently unused by the FPS Display mod but kept as a reference example.
 */
@Mixin(MinecraftServer.class)
public class ExampleMixin {
	/**
	 * Injects code at the start of MinecraftServer.loadLevel().
	 * This demonstrates mixin injection but doesn't currently perform any action.
	 *
	 * @param info Callback information provided by the mixin system
	 */
	@Inject(at = @At("HEAD"), method = "loadLevel")
	private void init(CallbackInfo info) {
		// This code is injected into the start of MinecraftServer.loadLevel()V
	}
}

