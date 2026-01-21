package net.honeyberries.mixin.client;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Example mixin for client-side code injection.
 * This mixin targets the Minecraft client and demonstrates how to inject code
 * at the beginning of a method.
 * <p>
 * Currently unused by the FPS Display mod but kept as a reference example.
 */
@Mixin(Minecraft.class)
public class ExampleClientMixin {
	/**
	 * Injects code at the start of Minecraft.run().
	 * This demonstrates mixin injection but doesn't currently perform any action.
	 *
	 * @param info Callback information provided by the mixin system
	 */
	@Inject(at = @At("HEAD"), method = "run")
	private void init(CallbackInfo info) {
		// This code is injected into the start of Minecraft.run()V
	}
}

