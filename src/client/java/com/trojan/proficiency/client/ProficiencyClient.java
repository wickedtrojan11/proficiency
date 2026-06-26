package com.trojan.proficiency.client;

import com.trojan.proficiency.client.keybind.KeybindHandler;
import com.trojan.proficiency.client.screen.SolarComposterScreen;
import com.trojan.proficiency.menu.ModMenus;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;

public class ProficiencyClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        KeybindHandler.register();
        CropGrowthOverlay.register();
        AnimalHusbandryOverlay.register();
        MenuScreens.register(
                ModMenus.SOLAR_COMPOSTER,
                SolarComposterScreen::new
        );
    }
}
