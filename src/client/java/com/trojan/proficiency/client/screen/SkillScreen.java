package com.trojan.proficiency.client.screen;
import java.util.Set;
import net.minecraft.client.Minecraft;
import com.trojan.proficiency.SkillManager;
import com.trojan.proficiency.skill.MiningSkill;
import com.trojan.proficiency.perk.MiningPerks;
import com.trojan.proficiency.perk.SkillPerk;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
public class SkillScreen extends Screen {
    private static final int TREE_X = 420;
    private static final int TREE_Y = 60;
    private boolean backgroundEnabled = true;;
    private static final int TREE_OFFSET_X = 355;
    private static final int TREE_OFFSET_Y = -15;
    private boolean dragging = false;

    private double lastMouseX;

    private double lastMouseY;

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
    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button
    ) {

        // =========================
// BACKGROUND TOGGLE
// =========================

        if (
                mouseX >= 40
                        && mouseX <= 180
                        && mouseY >=  570
                        && mouseY <=  585
        ) {

            backgroundEnabled =
                    !backgroundEnabled;

            return true;
        }
// =========================
// SKILL TAB CLICKING
// =========================

// Mining tab
        if (
                mouseX >= 40
                        && mouseX <= 180
                        && mouseY >= 30
                        && mouseY <= 55
        ) {

            selectedSkill = 0;

            return true;
        }

// Woodcutting tab
        if (
                mouseX >= 190
                        && mouseX <= 370
                        && mouseY >= 30
                        && mouseY <= 55
        ) {

            selectedSkill = 1;
            return true;
        }
        if (selectedSkill == 0) {

            for (SkillPerk perk
                    : MiningPerks.ALL_PERKS) {

                int perkX =
                        perk.getX()
                                + TREE_OFFSET_X;

                int perkY =
                        perk.getY() + TREE_OFFSET_Y;

                boolean hovering =
                        mouseX >= perkX
                                && mouseX <= perkX + 10
                                && mouseY >= perkY
                                && mouseY <= perkY + 10;

                if (hovering) {

                    boolean success =
                            SkillManager.unlockMiningPerk(
                                    minecraft.player.getUUID(),
                                    perk.getId(),
                                    perk.getRequiredLevel()
                            );

                    if (success) {

                        minecraft.player.sendSystemMessage(
                                Component.literal(
                                        "§6Unlocked "
                                                + perk.getName()
                                                + "!"
                                )
                        );

                    } else {

                        minecraft.player.sendSystemMessage(
                                Component.literal(
                                        "§cCannot unlock "
                                                + perk.getName()
                                )
                        );
                    }

                    return true;
                }
            }

        }
        String[] ores = {
                "coal",
                "redstone",
                "iron",
                "copper",
                "lapis",
                "gold",
                "emerald",
                "diamond",
                "ancient_debris"
        };

        int oreY = 370;

        for (String ore : ores) {

            boolean unlocked = switch (ore) {

                case "iron",
                     "copper",
                     "lapis" ->

                        SkillManager.hasMiningPerk(
                                minecraft.player.getUUID(),
                                "it_smells_2"
                        );

                case "gold",
                     "emerald",
                     "diamond" ->

                        SkillManager.hasMiningPerk(
                                minecraft.player.getUUID(),
                                "it_smells_3"
                        );

                case "ancient_debris" ->

                        SkillManager.hasMiningPerk(
                                minecraft.player.getUUID(),
                                "it_smells_4"
                        );

                default -> true;
            };

            if (!unlocked) {
                continue;
            }

            if (
                    mouseX >= 40
                            && mouseX <= 160
                            && mouseY >= oreY
                            && mouseY <= oreY + 10
            ) {

                SkillManager.toggleOreSense(
                        minecraft.player.getUUID(),
                        ore
                );

                minecraft.player.playSound(
                        net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK.value(),
                        1.0f,
                        1.0f
                );

                return true;
            }

            oreY += 12;

            if (
                    ore.equals("redstone")
                            || ore.equals("lapis")
                            || ore.equals("diamond")
            ) {

                oreY += 6;
            }
        }
        return super.mouseClicked(
                mouseX,
                mouseY,
                button
        );

    }

    @Override
    public void render(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {

        renderBackground(
                graphics,
                mouseX,
                mouseY,
                partialTick
        );


        // =========================
// MAIN UI FRAME
// =========================

        graphics.fill(
                10,
                20,
                width - 10,
                height - 20,
                0x66000000
        );

        graphics.renderOutline(
                10,
                20,
                width - 20,
                height - 40,
                0xFF555555
        );
        // =========================
// STATS PANEL
// =========================

        graphics.fill(
                35,
                90,
                220,
                290,
                0x88000000
        );

        graphics.renderOutline(
                35,
                90,
                185,
                200,
                0xFF555555
        );

        graphics.drawString(
                font,
                "STATS",
                50,
                105,
                0xFFFFAA00
        );
        // =========================
// ORE SENSING PANEL
// =========================

        graphics.fill(
                35,
                320,
                220,
                530,
                0x88000000
        );

        graphics.renderOutline(
                35,
                320,
                185,
                210,
                0xFF555555
        );

        graphics.drawString(
                font,
                "ORE SENSING",
                50,
                340,
                0xFFFFAA00
        );
        // =========================
// SETTINGS PANEL
// =========================

        graphics.fill(
                35,
                550,
                220,
                560,
                0x88000000
        );

        graphics.renderOutline(
                35,
                540,
                185,
                60,
                0xFF555555
        );

        graphics.drawString(
                font,
                "SETTINGS",
                50,
                565,
                0xFFFFAA00
        );
// =========================
// TREE PANEL
// =========================

        graphics.fill(
                250,
                50,
                700,
                420,
                0x44000000
        );

        graphics.renderOutline(
                250,
                50,
                700,
                420,
                0xFF444444
        );

        int miningLevel =
                SkillManager.getMiningLevel(
                        minecraft.player.getUUID()
                );
        ResourceLocation background;

        if (miningLevel >= 40) {

            background =
                    ResourceLocation.fromNamespaceAndPath(
                            "proficiency",
                            "textures/gui/netherite_pick_bg.png"
                    );

        } else if (miningLevel >= 30) {

            background =
                    ResourceLocation.fromNamespaceAndPath(
                            "proficiency",
                            "textures/gui/diamond_pick_bg.png"
                    );

        } else if (miningLevel >= 20) {

            background =
                    ResourceLocation.fromNamespaceAndPath(
                            "proficiency",
                            "textures/gui/iron_pick_bg.png"
                    );

        } else if (miningLevel >= 10) {

            background =
                    ResourceLocation.fromNamespaceAndPath(
                            "proficiency",
                            "textures/gui/stone_pick_bg.png"
                    );

        } else {

            background =
                    ResourceLocation.fromNamespaceAndPath(
                            "proficiency",
                            "textures/gui/wood_pick_bg.png"
                    );
        }
        int miningXp =
                SkillManager.getMiningXp(
                        minecraft.player.getUUID()
                );

        int miningXpRequired =
                SkillManager.getMiningXpRequired(
                        minecraft.player.getUUID()
                );

        int miningPerkPoints =
                SkillManager.getMiningPerkPoints(
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
        if (
                selectedSkill == 0
                        && backgroundEnabled
        ) {

            graphics.blit(
                    background,
                    TREE_X,
                    TREE_Y,
                    0,
                    0,
                    400,
                    300,
                    400,
                    300
            );
        }

        // =========================
// BOTTOM INFO PANEL
// =========================

        graphics.fill(
                250,
                height - 140,
                950,
                height - 30,
                0x88000000
        );

        graphics.renderOutline(
                250,
                height - 140,
                700,
                110,
                0xFF444444
        );
        if (selectedSkill == 0) {

                graphics.drawString(
                        font,
                        "Mining Level: " + miningLevel,
                        260,
                        height - 125,
                        0xFFD700
                );

                graphics.drawString(
                        font,
                        "Perk Points: " + miningPerkPoints,
                        260,
                        height - 108,
                        0x55FFFF
                );
                drawXpBar(
                        graphics,
                        520,
                        height - 120,
                        240,
                        12,
                        miningXp,
                        miningXpRequired,
                        0xFFFFAA00
                );
                if (miningPerkPoints > 0) {

                    graphics.drawString(
                            font,
                            "Spend your perk points!",
                            520,
                            height - 100,
                            0x55FF55
                    );
                }
                graphics.drawString(
                        font,
                        "Mining Speed: +15%",
                        50,
                         190,
                        0x55FF55
                );

                graphics.drawString(
                        font,
                        "Fortune: +3",
                        50,
                         210,
                        0x55FFFF
                );

                graphics.drawString(
                        font,
                        "Durability: +40%",
                        50,
                         230,
                        0xAAAAAA
                );

                graphics.drawString(
                        font,
                        "Ore Sense: Tier 2",
                        50,
                        250,
                        0xFFAA00
                );
            // =========================
            // CONNECTION LINES
            // =========================

            for (SkillPerk perk
                    : MiningPerks.ALL_PERKS) {

                if (perk.getParentId() == null) {
                    continue;
                }

                SkillPerk parent = null;

                for (SkillPerk possibleParent
                        : MiningPerks.ALL_PERKS) {

                    if (
                            possibleParent.getId()
                                    .equals(
                                            perk.getParentId()
                                    )
                    ) {

                        parent = possibleParent;
                        break;
                    }
                }

                if (parent == null) {
                    continue;
                }

                boolean unlocked =
                        SkillManager.hasMiningPerk(
                                minecraft.player.getUUID(),
                                perk.getId()
                        );

                int color =
                        unlocked
                                ? 0xFFFFAA00
                                : 0xFF555555;

                int x1 =
                        parent.getX()
                                + TREE_OFFSET_X
                                + 6;

                int y1 =
                        parent.getY()
                                + TREE_OFFSET_Y
                                + 6;

                int x2 =
                        perk.getX()
                                + TREE_OFFSET_X
                                + 6;

                int y2 =
                        perk.getY()
                                + TREE_OFFSET_Y
                                + 6;

                graphics.hLine(
                        Math.min(x1, x2),
                        Math.max(x1, x2),
                        y1,
                        color
                );

                graphics.vLine(
                        x2,
                        Math.min(y1, y2),
                        Math.max(y1, y2),
                        color
                );
            }
                // =========================
// PERK NODES
// =========================

                for (SkillPerk perk : MiningPerks.ALL_PERKS) {

                    int perkX =
                            perk.getX()
                                    + TREE_OFFSET_X;

                    int perkY =
                            perk.getY()
                                    + TREE_OFFSET_Y;

                    boolean unlocked =
                            SkillManager.hasMiningPerk(
                                    minecraft.player.getUUID(),
                                    perk.getId()
                            );

                    int color =
                            unlocked
                                    ? 0xFFFFAA00
                                    : 0xFFCCCCCC;

                    graphics.fill(
                            perkX,
                            perkY,
                            perkX + 10,
                            perkY + 10,
                            color
                    );

                    graphics.renderOutline(
                            perkX,
                            perkY,
                            10,
                            10,
                            0xFFFFFFFF
                    );

                    // Hover tooltip
                    if (
                            mouseX >= perkX
                                    && mouseX <= perkX + 10
                                    && mouseY >= perkY
                                    && mouseY <= perkY + 10
                    ) {

                        graphics.renderTooltip(
                                font,
                                Component.literal(
                                        perk.getName()
                                                + "\n"
                                                + perk.getDescription()
                                ),
                                mouseX,
                                mouseY
                        );
                    }
                }
            Set<String> selectedOres =
                    SkillManager.getSelectedOreSense(
                            minecraft.player.getUUID()
                    );

            String[] ores = {
                    "coal",
                    "redstone",
                    "iron",
                    "copper",
                    "lapis",
                    "gold",
                    "emerald",
                    "diamond",
                    "ancient_debris"
            };
            int oreY = 370;

// =========================
// TIER 1
// =========================

            drawOreToggle(
                    graphics,
                    "coal",
                    oreY
            );

            oreY += 12;

            drawOreToggle(
                    graphics,
                    "redstone",
                    oreY
            );

            oreY += 18;

// =========================
// TIER 2
// =========================

            if (
                    SkillManager.hasMiningPerk(
                            minecraft.player.getUUID(),
                            "it_smells_2"
                    )
            ) {

                drawOreToggle(
                        graphics,
                        "iron",
                        oreY
                );

                oreY += 12;

                drawOreToggle(
                        graphics,
                        "copper",
                        oreY
                );

                oreY += 12;

                drawOreToggle(
                        graphics,
                        "lapis",
                        oreY
                );

                oreY += 18;
            }

// =========================
// TIER 3
// =========================

            if (
                    SkillManager.hasMiningPerk(
                            minecraft.player.getUUID(),
                            "it_smells_3"
                    )
            ) {

                drawOreToggle(
                        graphics,
                        "gold",
                        oreY
                );

                oreY += 12;

                drawOreToggle(
                        graphics,
                        "emerald",
                        oreY
                );

                oreY += 12;

                drawOreToggle(
                        graphics,
                        "diamond",
                        oreY
                );

                oreY += 18;
            }

// =========================
// TIER 4
// =========================

            if (
                    SkillManager.hasMiningPerk(
                            minecraft.player.getUUID(),
                            "it_smells_4"
                    )
            ) {

                drawOreToggle(
                        graphics,
                        "ancient_debris",
                        oreY
                );
            }
        }
        graphics.drawString(
                font,
                "Background: "
                        + (
                        backgroundEnabled
                                ? "ON"
                                : "OFF"
                ),
                50,
                525,
                backgroundEnabled
                        ? 0x55FF55
                        : 0xFF5555
        );
        // =========================
// TOP SKILL TABS
// =========================

// Mining tab background
        graphics.fill(
                40,
                30,
                180,
                55,
                selectedSkill == 0
                        ? 0xFF222222
                        : 0x88000000
        );

        graphics.renderOutline(
                40,
                30,
                140,
                25,
                0xFF777777
        );

// Woodcutting tab background
        graphics.fill(
                190,
                30,
                370,
                55,
                selectedSkill == 1
                        ? 0xFF222222
                        : 0x88000000
        );

        graphics.renderOutline(
                190,
                30,
                180,
                25,
                0xFF777777
        );

// Tab text
        graphics.drawCenteredString(
                font,
                "MINING",
                110,
                38,
                selectedSkill == 0
                        ? 0xFFFF55
                        : 0xAAAAAA
        );

        graphics.drawCenteredString(
                font,
                "WOODCUTTING",
                280,
                38,
                selectedSkill == 1
                        ? 0x55FF55
                        : 0xAAAAAA
        );




        // =========================
        // WOODCUTTING PANEL
        // =========================

    if (selectedSkill == 1) {

            graphics.drawString(
                    font,
                    "Woodcutting Level: "
                            + woodcuttingLevel,
                    220,
                    80,
                    0x55FF55
            );
            drawXpBar(
                    graphics,
                    220,
                    95,
                    140,
                    12,
                    woodcuttingXp,
                    woodcuttingXpRequired,
                    0xFF55AA55
            );
        }
    }
    private void drawOreToggle(
            GuiGraphics graphics,
            String ore,
            int y
    ) {

        Set<String> selectedOres =
                SkillManager.getSelectedOreSense(
                        minecraft.player.getUUID()
                );

        boolean selected =
                selectedOres.contains(ore);

        graphics.drawString(
                font,
                (selected ? "[x] " : "[ ] ")
                        + ore,
                40,
                y,
                selected
                        ? 0x55FF55
                        : 0xAAAAAA
        );
    }
    private void drawXpBar(
            GuiGraphics graphics,
            int x,
            int y,
            int width,
            int height,
            int currentXp,
            int requiredXp,
            int fillColor
    ) {

        // Background
        graphics.fill(
                x,
                y,
                x + width,
                y + height,
                0xFF222222
        );

        // Border
        graphics.renderOutline(
                x,
                y,
                width,
                height,
                0xFF555555
        );

        // Fill amount
        int fillWidth =
                (int)(
                        (
                                currentXp
                                        / (float) requiredXp
                        ) * width
                );

        // Filled section
        graphics.fill(
                x + 1,
                y + 1,
                x + fillWidth - 1,
                y + height - 1,
                fillColor
        );

        // XP text
        graphics.drawCenteredString(
                font,
                currentXp + " / " + requiredXp,
                x + width / 2,
                y + 2,
                0xFFFFFFFF
        );
    }
    @Override
    public boolean mouseDragged(
            double mouseX,
            double mouseY,
            int button,
            double dragX,
            double dragY
    ) {

        return false;
    }
}
