package com.trojan.proficiency.client.keybind;

import com.trojan.proficiency.client.screen.SkillScreen;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import org.lwjgl.glfw.GLFW;

public class KeybindHandler {

    private static final KeyMapping OPEN_SKILLS =
            new KeyMapping(
                    "key.proficiency.open_skills",
                    GLFW.GLFW_KEY_K,
                    "category.proficiency"
            );

    public static void register() {

        ClientTickEvents.END_CLIENT_TICK.register(client -> {

            while (OPEN_SKILLS.consumeClick()) {

                Minecraft.getInstance().setScreen(
                        new SkillScreen()
                );
            }
        });
    }
}