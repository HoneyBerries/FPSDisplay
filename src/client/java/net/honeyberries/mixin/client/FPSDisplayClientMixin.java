package net.honeyberries.mixin.client;

import net.honeyberries.FPSRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class FPSDisplayClientMixin {

    // Inject at the TAIL (end) of the render method.
    // This ensures it runs even if the game skipped the hotbar/crosshair due to spectator mode.
    @Inject(method = "extractRenderState", at = @At("TAIL"))
    public void renderFPS(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        FPSRenderer.render(graphics, deltaTracker);
    }
}