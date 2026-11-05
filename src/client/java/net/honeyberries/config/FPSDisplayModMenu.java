package net.honeyberries.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

/** Opens the YACL-generated config screen from ModMenu. */
public class FPSDisplayModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> FPSDisplayConfig.HANDLER.generateGui().generateScreen(parent);
    }
}
