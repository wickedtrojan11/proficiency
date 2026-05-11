package com.trojan.proficiency.client.screen;

import com.trojan.proficiency.SkillManager;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class SkillScreen extends Screen {

    public SkillScreen() {
        super(Component.literal("Proficiency"));
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {

        renderBackground(graphics, mouseX, mouseY, delta);

        super.render(graphics, mouseX, mouseY, delta);

        int miningLevel =
                SkillManager.getMiningLevel(
                        minecraft.player.getUUID()
                );

        int woodcuttingLevel =
                SkillManager.getWoodcuttingLevel(
                        minecraft.player.getUUID()
                );

        graphics.drawString(
                font,
                "Proficiency",
                width / 2 - 40,
                40,
                0xFFFFFF
        );

        graphics.drawString(
                font,
                "Mining Level: " + miningLevel,
                width / 2 - 60,
                80,
                0xFFD700
        );

        graphics.drawString(
                font,
                "Woodcutting Level: " + woodcuttingLevel,
                width / 2 - 60,
                100,
                0x55FF55
        );
    }
}