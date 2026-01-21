package net.honeyberries;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

/**
 * ModMenu integration for the FPS Display mod.
 * Provides a config screen accessible from the ModMenu mods list.
 */
public class FPSDisplayModMenu implements ModMenuApi {

    /**
     * Provides the configuration screen factory for ModMenu.
     * When a player clicks the config button in ModMenu, this returns
     * a factory that creates the FPS Display configuration screen.
     *
     * @return A factory that creates the config screen with the given parent screen
     */
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return FPSConfig::createConfigScreen;
    }
}
