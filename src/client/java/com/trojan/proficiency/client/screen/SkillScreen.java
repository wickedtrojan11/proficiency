package com.trojan.proficiency.client.screen;

import com.trojan.proficiency.SkillManager;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class SkillScreen extends Screen {
    private int selectedSkill = 0;
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
    public boolean keyPressed(
            int keyCode,
            int scanCode,
            int modifiers
    ) {

        // Up arrow
        if (keyCode == 265) {

            selectedSkill--;

            if (selectedSkill < 0) {
                selectedSkill = 1;
            }

            return true;
        }

        // Down arrow
        if (keyCode == 264) {

            selectedSkill++;

            if (selectedSkill > 1) {
                selectedSkill = 0;
            }

            return true;
        }

        return super.keyPressed(
                keyCode,
                scanCode,
                modifiers
        );
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

        // =========================
// LEFT PANEL - SKILL LIST
// =========================

        graphics.drawString(
                font,
                (selectedSkill == 0 ? "> " : "  ")
                        + "Mining",
                40,
                80,
                0xFFD700
        );

        graphics.drawString(
                font,
                (selectedSkill == 1 ? "> " : "  ")
                        + "Woodcutting",
                40,
                100,
                0x55FF55
        );

// =========================
// RIGHT PANEL - DETAILS
// =========================

        if (selectedSkill == 0) {

            graphics.drawString(
                    font,
                    "Mining Level: " + miningLevel,
                    220,
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
                    220,
                    95,
                    0xAAAAAA
            );
        }

        if (selectedSkill == 1) {

            graphics.drawString(
                    font,
                    "Woodcutting Level: "
                            + woodcuttingLevel,
                    220,
                    80,
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
                    220,
                    95,
                    0xAAAAAA
            );
        }
    }
}