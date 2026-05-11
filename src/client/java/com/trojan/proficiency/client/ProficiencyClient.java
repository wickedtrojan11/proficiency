package com.trojan.proficiency.client;

import com.trojan.proficiency.client.keybind.KeybindHandler;

import net.fabricmc.api.ClientModInitializer;

public class ProficiencyClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        KeybindHandler.register();
    }
}