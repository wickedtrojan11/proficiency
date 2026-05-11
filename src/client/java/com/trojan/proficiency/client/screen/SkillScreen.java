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

    private String createProgressBar(int current, int max) {

        int totalBars = 10;

        int filledBars =
                (int)((double) current / max * totalBars);

        StringBuilder bar = new StringBuilder();

        for (int i = 0; i < totalBars; i++) {

            if (i < filledBars) {
                bar.append("█");
            } else {
                bar.append("░");
            }
        }

        return bar.toString();
    }

    @Override
    public void render(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float delta
    ) {

        renderBackground(graphics, mouseX, mouseY, delta);

        super.render(graphics, mouseX, mouseY, delta);

        int miningLevel =
                SkillManager.getMiningLevel(
                        minecraft.player.getUUID()
                );

        int miningXp =
                SkillManager.getMiningXp(
                        minecraft.player.getUUID()
                );

        int miningXpRequired =
                SkillManager.getMiningXpRequired(
                        minecraft.player.getUUID()
                );

        int woodcuttingLevel =
                SkillManager.getWoodcuttingLevel(
                        minecraft.player.getUUID()
                );

        int woodcuttingXp =
                SkillManager.getWoodcuttingXp(
                        minecraft.player.getUUID()
                );

        int woodcuttingXpRequired =
                SkillManager.getWoodcuttingXpRequired(
                        minecraft.player.getUUID()
                );

        graphics.drawString(
                font,
                "Proficiency",
                width / 2 - 40,
                40,
                0xFFFFFF
        );

        // Mining

        graphics.drawString(
                font,
                "Mining Level: " + miningLevel,
                width / 2 - 60,
                80,
                0xFFD700
        );

        graphics.drawString(
                font,
                createProgressBar(
                        miningXp,
                        miningXpRequired
                )
                        + " "
                        + miningXp
                        + "/"
                        + miningXpRequired,
                width / 2 - 60,
                92,
                0xAAAAAA
        );

        // Woodcutting

        graphics.drawString(
                font,
                "Woodcutting Level: " + woodcuttingLevel,
                width / 2 - 60,
                120,
                0x55FF55
        );

        graphics.drawString(
                font,
                createProgressBar(
                        woodcuttingXp,
                        woodcuttingXpRequired
                )
                        + " "
                        + woodcuttingXp
                        + "/"
                        + woodcuttingXpRequired,
                width / 2 - 60,
                132,
                0xAAAAAA
        );
    }
}