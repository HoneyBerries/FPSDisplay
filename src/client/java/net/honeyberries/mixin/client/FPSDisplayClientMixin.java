package net.honeyberries.mixin.client;

import net.honeyberries.FPSRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Hud.class)
public class FPSDisplayClientMixin {

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    public void renderFPS(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        FPSRenderer.render(graphics, deltaTracker);
    }
}