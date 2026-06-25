package com.trojan.proficiency.client.screen;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import com.trojan.proficiency.SkillManager;
import com.trojan.proficiency.skill.MiningSkill;
import com.trojan.proficiency.perk.MiningPerks;
import com.trojan.proficiency.perk.SkillPerk;
import com.trojan.proficiency.perk.WoodcuttingPerks;
import com.trojan.proficiency.perk.FarmingPerks;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
public class SkillScreen extends Screen {
    private static final int SCREEN_MARGIN_X = 10;
    private static final int SCREEN_TOP = 20;
    private static final int SCREEN_BOTTOM_MARGIN = 20;
    private static final int SCREEN_OUTLINE_HEIGHT_OFFSET = 40;

    private static final int MINING_TAB_X = 40;
    private static final int MINING_TAB_Y = 22;
    private static final int MINING_TAB_WIDTH = 140;
    private static final int MINING_TAB_HEIGHT = 25;
    private static final int MINING_TAB_LABEL_X = 110;
    private static final int TAB_LABEL_Y = 30;

    private static final int WOODCUTTING_TAB_X = 190;
    private static final int WOODCUTTING_TAB_Y = 22;
    private static final int WOODCUTTING_TAB_WIDTH = 180;
    private static final int WOODCUTTING_TAB_HEIGHT = 25;
    private static final int WOODCUTTING_TAB_LABEL_X = 280;

    private static final int FARMING_TAB_X = 380;
    private static final int FARMING_TAB_Y = 22;
    private static final int FARMING_TAB_WIDTH = 140;
    private static final int FARMING_TAB_HEIGHT = 25;
    private static final int FARMING_TAB_LABEL_X = 450;

    private static final int STATS_PANEL_X = 35;
    private static final int STATS_PANEL_Y = 90;
    private static final int STATS_PANEL_WIDTH = 185;
    private static final int STATS_PANEL_HEIGHT = 130;
    private static final int STATS_TITLE_X = 50;
    private static final int STATS_TITLE_Y = 105;
    private static final int MINING_STAT_X = 50;
    private static final int MINING_SPEED_STAT_Y = 130;
    private static final int FORTUNE_STAT_Y = 150;
    private static final int DURABILITY_STAT_Y = 170;
    private static final int ORE_SENSE_STAT_Y = 190;

    private static final int ORE_PANEL_X = 35;
    private static final int ORE_PANEL_Y = 235;
    private static final int ORE_PANEL_WIDTH = 185;
    private static final int ORE_PANEL_HEIGHT = 150;
    private static final int ORE_TITLE_X = 50;
    private static final int ORE_TITLE_Y = 255;
    private static final int ORE_TOGGLE_X = 40;
    private static final int ORE_TOGGLE_WIDTH = 120;
    private static final int ORE_START_Y = 280;
    private static final int ORE_ROW_HEIGHT = 10;
    private static final int ORE_ROW_STEP = 10;
    private static final int ORE_TIER_GAP = 2;
    private static final int WOODCUTTING_TOGGLE_X = 50;
    private static final int WOODCUTTING_TOGGLE_START_Y = 280;
    private static final int WOODCUTTING_TOGGLE_WIDTH = 150;
    private static final int WOODCUTTING_TOGGLE_HEIGHT = 12;
    private static final int WOODCUTTING_TOGGLE_ROW_STEP = 22;

    private static final int SETTINGS_PANEL_X = 35;
    private static final int SETTINGS_PANEL_FILL_Y = 410;
    private static final int SETTINGS_PANEL_FILL_HEIGHT = 50;
    private static final int SETTINGS_PANEL_OUTLINE_Y = 400;
    private static final int SETTINGS_PANEL_WIDTH = 185;
    private static final int SETTINGS_PANEL_OUTLINE_HEIGHT = 70;
    private static final int SETTINGS_TITLE_X = 50;
    private static final int SETTINGS_TITLE_Y = 415;
    private static final int BACKGROUND_TOGGLE_X = 40;
    private static final int BACKGROUND_TOGGLE_Y = 440;
    private static final int BACKGROUND_TOGGLE_WIDTH = 140;
    private static final int BACKGROUND_TOGGLE_HEIGHT = 15;
    private static final int BACKGROUND_LABEL_X = 50;
    private static final int BACKGROUND_LABEL_Y = 440;

    private static final int TREE_PANEL_X = 250;
    private static final int TREE_PANEL_Y = 50;
    private static final int TREE_PANEL_RIGHT = 700;
    private static final int TREE_PANEL_BOTTOM = 420;
    private static final int TREE_PANEL_WIDTH = 700;
    private static final int TREE_PANEL_HEIGHT = 420;
    private static final int TREE_X = 420;
    private static final int TREE_Y = 60;
    private static final int TREE_BACKGROUND_WIDTH = 400;
    private static final int TREE_BACKGROUND_HEIGHT = 300;
    private static final int TREE_OFFSET_X = 355;
    private static final int TREE_OFFSET_Y = -15;
    private static final int PERK_NODE_SIZE = 10;
    private static final int PERK_LINE_CENTER_OFFSET = 6;

    private static final int BOTTOM_PANEL_X = 250;
    private static final int BOTTOM_PANEL_WIDTH = 700;
    private static final int BOTTOM_PANEL_HEIGHT = 110;
    private static final int BOTTOM_PANEL_TOP_OFFSET = 140;
    private static final int BOTTOM_PANEL_BOTTOM_OFFSET = 30;
    private static final int BOTTOM_INFO_X = 260;
    private static final int MINING_LEVEL_Y_OFFSET = 125;
    private static final int PERK_POINTS_Y_OFFSET = 108;
    private static final int MINING_XP_BAR_X = 520;
    private static final int MINING_XP_BAR_Y_OFFSET = 120;
    private static final int MINING_XP_BAR_WIDTH = 240;
    private static final int XP_BAR_HEIGHT = 12;
    private static final int PERK_PROMPT_Y_OFFSET = 100;

    private static final int WOODCUTTING_INFO_X = 220;
    private static final int WOODCUTTING_LEVEL_Y = 80;
    private static final int WOODCUTTING_XP_BAR_Y = 95;
    private static final int WOODCUTTING_XP_BAR_WIDTH = 140;

    private static final int PANEL_FILL_COLOR = 0x88000000;
    private static final int SCREEN_FILL_COLOR = 0x66000000;
    private static final int TREE_PANEL_FILL_COLOR = 0x44000000;
    private static final int DEFAULT_OUTLINE_COLOR = 0xFF555555;
    private static final int TREE_PANEL_OUTLINE_COLOR = 0xFF444444;
    private static final int TAB_OUTLINE_COLOR = 0xFF777777;
    private static final int ACTIVE_TAB_FILL_COLOR = 0xFF222222;
    private static final int TITLE_COLOR = 0xFFFFAA00;
    private static final int PERK_LOCKED_FILL_COLOR = 0xFF222222;
    private static final int PERK_LOCKED_BORDER_COLOR = 0xFF555555;
    private static final int PERK_AVAILABLE_FILL_COLOR = 0xFF665000;
    private static final int PERK_AVAILABLE_BORDER_COLOR = 0xFFFFAA00;
    private static final int PERK_UNLOCKED_COLOR = 0xFFFFD700;

    private boolean backgroundEnabled = true;;
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
                selectedSkill = 2;
            }

            return true;
        }

        // Down arrow
        if (keyCode == 264) {

            selectedSkill++;

            if (selectedSkill > 2) {
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
                mouseX >= BACKGROUND_TOGGLE_X
                        && mouseX <= BACKGROUND_TOGGLE_X + BACKGROUND_TOGGLE_WIDTH
                        && mouseY >= BACKGROUND_TOGGLE_Y
                        && mouseY <= BACKGROUND_TOGGLE_Y + BACKGROUND_TOGGLE_HEIGHT
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
                mouseX >= MINING_TAB_X
                        && mouseX <= MINING_TAB_X + MINING_TAB_WIDTH
                        && mouseY >= MINING_TAB_Y
                        && mouseY <= MINING_TAB_Y + MINING_TAB_HEIGHT
        ) {

            selectedSkill = 0;

            return true;
        }

// Woodcutting tab
        if (
                mouseX >= WOODCUTTING_TAB_X
                        && mouseX <= WOODCUTTING_TAB_X + WOODCUTTING_TAB_WIDTH
                        && mouseY >= WOODCUTTING_TAB_Y
                        && mouseY <= WOODCUTTING_TAB_Y + WOODCUTTING_TAB_HEIGHT
        ) {

            selectedSkill = 1;
            return true;
        }

// Farming tab
        if (
                mouseX >= FARMING_TAB_X
                        && mouseX <= FARMING_TAB_X + FARMING_TAB_WIDTH
                        && mouseY >= FARMING_TAB_Y
                        && mouseY <= FARMING_TAB_Y + FARMING_TAB_HEIGHT
        ) {

            selectedSkill = 2;
            return true;
        }

        if (selectedSkill == 1) {

            int toggleRow =
                    getWoodcuttingToggleRow(
                            mouseX,
                            mouseY
                    );

            if (toggleRow >= 0) {

                UUID playerId =
                        minecraft.player.getUUID();

                switch (toggleRow) {

                    case 0 ->
                            SkillManager.toggleWoodcuttingLeafDecay(
                                    playerId
                            );

                    case 1 ->
                            SkillManager.toggleWoodcuttingWholeTree(
                                    playerId
                            );

                    case 2 ->
                            SkillManager.toggleWoodcuttingBonusDrops(
                                    playerId
                            );

                    case 3 ->
                            SkillManager.toggleWoodcuttingCleanFloor(
                                    playerId
                            );

                    default -> {
                        return false;
                    }
                }

                minecraft.player.playSound(
                        net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK.value(),
                        1.0f,
                        1.0f
                );

                return true;
            }
        }

        if (selectedSkill == 2) {

            int toggleRow =
                    getWoodcuttingToggleRow(
                            mouseX,
                            mouseY
                    );

            if (toggleRow >= 0) {

                UUID playerId =
                        minecraft.player.getUUID();

                switch (toggleRow) {

                    case 0 ->
                            SkillManager.toggleFarmingBonusHarvests(
                                    playerId
                            );

                    case 1 ->
                            SkillManager.toggleFarmingAnimalFollow(
                                    playerId
                            );

                    case 2 ->
                            SkillManager.toggleFarmingAutoReplant(
                                    playerId
                            );

                    case 3 ->
                            SkillManager.toggleFarmingBeeGrowth(
                                    playerId
                            );

                    default -> {
                        return false;
                    }
                }

                minecraft.player.playSound(
                        net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK.value(),
                        1.0f,
                        1.0f
                );

                return true;
            }
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
                                && mouseX <= perkX + PERK_NODE_SIZE
                                && mouseY >= perkY
                                && mouseY <= perkY + PERK_NODE_SIZE;

                if (hovering) {

                    boolean success =
                            SkillManager.unlockMiningPerk(
                                    minecraft.player.getUUID(),
                                    perk.getId(),
                                    perk.getRequiredLevel()
                            );

                    if (success) {

                        showPerkUnlockToast(perk);

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
        if (selectedSkill == 1) {

            for (SkillPerk perk
                    : WoodcuttingPerks.ALL_PERKS) {

                int perkX =
                        perk.getX()
                                + TREE_OFFSET_X;

                int perkY =
                        perk.getY() + TREE_OFFSET_Y;

                boolean hovering =
                        mouseX >= perkX
                                && mouseX <= perkX + PERK_NODE_SIZE
                                && mouseY >= perkY
                                && mouseY <= perkY + PERK_NODE_SIZE;

                if (hovering) {

                    int woodcuttingLevel =
                            SkillManager.getWoodcuttingLevel(
                                    minecraft.player.getUUID()
                            );

                    int woodcuttingPerkPoints =
                            SkillManager.getWoodcuttingPerkPoints(
                                    minecraft.player.getUUID()
                            );

                    if (
                            !isWoodcuttingPerkAvailable(
                                    perk,
                                    woodcuttingLevel,
                                    woodcuttingPerkPoints
                            )
                    ) {

                        minecraft.player.sendSystemMessage(
                                Component.literal(
                                        "§cCannot unlock "
                                                + perk.getName()
                                )
                        );

                        return true;
                    }

                    boolean success =
                            SkillManager.unlockWoodcuttingPerk(
                                    minecraft.player.getUUID(),
                                    perk.getId(),
                                    perk.getRequiredLevel()
                            );

                    if (success) {

                        showPerkUnlockToast(perk);

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

        if (selectedSkill == 2) {

            for (SkillPerk perk
                    : FarmingPerks.ALL_PERKS) {

                int perkX =
                        perk.getX()
                                + TREE_OFFSET_X;

                int perkY =
                        perk.getY()
                                + TREE_OFFSET_Y;

                boolean hovering =
                        mouseX >= perkX
                                && mouseX <= perkX + PERK_NODE_SIZE
                                && mouseY >= perkY
                                && mouseY <= perkY + PERK_NODE_SIZE;

                if (hovering) {

                    int farmingLevel =
                            SkillManager.getFarmingLevel(
                                    minecraft.player.getUUID()
                            );

                    int farmingPerkPoints =
                            SkillManager.getFarmingPerkPoints(
                                    minecraft.player.getUUID()
                            );

                    if (
                            !isFarmingPerkAvailable(
                                    perk,
                                    farmingLevel,
                                    farmingPerkPoints
                            )
                    ) {

                        minecraft.player.sendSystemMessage(
                                Component.literal(
                                        "\u00A7cCannot unlock "
                                                + perk.getName()
                                )
                        );

                        return true;
                    }

                    boolean success =
                            SkillManager.unlockFarmingPerk(
                                    minecraft.player.getUUID(),
                                    perk.getId(),
                                    perk.getRequiredLevel()
                            );

                    if (success) {

                        showPerkUnlockToast(perk);

                        minecraft.player.sendSystemMessage(
                                Component.literal(
                                        "\u00A76Unlocked "
                                                + perk.getName()
                                                + "!"
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

        int oreY = ORE_START_Y;

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
                    mouseX >= ORE_TOGGLE_X
                            && mouseX <= ORE_TOGGLE_X + ORE_TOGGLE_WIDTH
                            && mouseY >= oreY
                            && mouseY <= oreY + ORE_ROW_HEIGHT
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

            oreY += ORE_ROW_STEP;

            if (
                    ore.equals("redstone")
                            || ore.equals("lapis")
                            || ore.equals("diamond")
            ) {

                oreY += ORE_TIER_GAP;
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
                SCREEN_MARGIN_X,
                SCREEN_TOP,
                width - SCREEN_MARGIN_X,
                height - SCREEN_BOTTOM_MARGIN,
                SCREEN_FILL_COLOR
        );

        graphics.renderOutline(
                SCREEN_MARGIN_X,
                SCREEN_TOP,
                width - SCREEN_MARGIN_X * 2,
                height - SCREEN_OUTLINE_HEIGHT_OFFSET,
                DEFAULT_OUTLINE_COLOR
        );
        // =========================
// STATS PANEL
// =========================

        graphics.fill(
                STATS_PANEL_X,
                STATS_PANEL_Y,
                STATS_PANEL_X + STATS_PANEL_WIDTH,
                STATS_PANEL_Y + STATS_PANEL_HEIGHT,
                PANEL_FILL_COLOR
        );

        graphics.renderOutline(
                STATS_PANEL_X,
                STATS_PANEL_Y,
                STATS_PANEL_WIDTH,
                STATS_PANEL_HEIGHT,
                DEFAULT_OUTLINE_COLOR
        );

        graphics.drawString(
                font,
                "STATS",
                STATS_TITLE_X,
                STATS_TITLE_Y,
                TITLE_COLOR
        );
        // =========================
// ORE SENSING PANEL
// =========================

        graphics.fill(
                ORE_PANEL_X,
                ORE_PANEL_Y,
                ORE_PANEL_X + ORE_PANEL_WIDTH,
                ORE_PANEL_Y + ORE_PANEL_HEIGHT,
                PANEL_FILL_COLOR
        );

        graphics.renderOutline(
                ORE_PANEL_X,
                ORE_PANEL_Y,
                ORE_PANEL_WIDTH,
                ORE_PANEL_HEIGHT,
                DEFAULT_OUTLINE_COLOR
        );

        graphics.drawString(
                font,
                selectedSkill == 1
                        ? "FORAGING"
                        : selectedSkill == 2
                        ? "FARM OPTIONS"
                        : "ORE SENSING",
                ORE_TITLE_X,
                ORE_TITLE_Y,
                TITLE_COLOR
        );

        if (selectedSkill == 1) {

            UUID playerId =
                    minecraft.player.getUUID();

            drawWoodcuttingFeatureToggle(
                    graphics,
                    "Leaf Decay",
                    SkillManager.isWoodcuttingLeafDecayEnabled(
                            playerId
                    ),
                    WOODCUTTING_TOGGLE_START_Y
            );

            drawWoodcuttingFeatureToggle(
                    graphics,
                    "Whole Tree",
                    SkillManager.isWoodcuttingWholeTreeEnabled(
                            playerId
                    ),
                    WOODCUTTING_TOGGLE_START_Y
                            + WOODCUTTING_TOGGLE_ROW_STEP
            );

            drawWoodcuttingFeatureToggle(
                    graphics,
                    "Bonus Drops",
                    SkillManager.isWoodcuttingBonusDropsEnabled(
                            playerId
                    ),
                    WOODCUTTING_TOGGLE_START_Y
                            + WOODCUTTING_TOGGLE_ROW_STEP * 2
            );

            drawWoodcuttingFeatureToggle(
                    graphics,
                    "Clean Floor",
                    SkillManager.isWoodcuttingCleanFloorEnabled(
                            playerId
                    ),
                    WOODCUTTING_TOGGLE_START_Y
                            + WOODCUTTING_TOGGLE_ROW_STEP * 3
            );
        }

        if (selectedSkill == 2) {

            UUID playerId =
                    minecraft.player.getUUID();

            drawWoodcuttingFeatureToggle(
                    graphics,
                    "Bonus Harvests",
                    SkillManager.isFarmingBonusHarvestsEnabled(
                            playerId
                    ),
                    WOODCUTTING_TOGGLE_START_Y
            );

            drawWoodcuttingFeatureToggle(
                    graphics,
                    "Animal Follow",
                    SkillManager.isFarmingAnimalFollowEnabled(
                            playerId
                    ),
                    WOODCUTTING_TOGGLE_START_Y
                            + WOODCUTTING_TOGGLE_ROW_STEP
            );

            drawWoodcuttingFeatureToggle(
                    graphics,
                    "Auto Replant",
                    SkillManager.isFarmingAutoReplantEnabled(
                            playerId
                    ),
                    WOODCUTTING_TOGGLE_START_Y
                            + WOODCUTTING_TOGGLE_ROW_STEP * 2
            );

            drawWoodcuttingFeatureToggle(
                    graphics,
                    "Bee Growth",
                    SkillManager.isFarmingBeeGrowthEnabled(
                            playerId
                    ),
                    WOODCUTTING_TOGGLE_START_Y
                            + WOODCUTTING_TOGGLE_ROW_STEP * 3
            );
        }

        // =========================
// SETTINGS PANEL
// =========================

        graphics.fill(
                SETTINGS_PANEL_X,
                SETTINGS_PANEL_FILL_Y,
                SETTINGS_PANEL_X + SETTINGS_PANEL_WIDTH,
                SETTINGS_PANEL_FILL_Y + SETTINGS_PANEL_FILL_HEIGHT,
                PANEL_FILL_COLOR
        );

        graphics.renderOutline(
                SETTINGS_PANEL_X,
                SETTINGS_PANEL_OUTLINE_Y,
                SETTINGS_PANEL_WIDTH,
                SETTINGS_PANEL_OUTLINE_HEIGHT,
                DEFAULT_OUTLINE_COLOR
        );

        graphics.drawString(
                font,
                "SETTINGS",
                SETTINGS_TITLE_X,
                SETTINGS_TITLE_Y,
                TITLE_COLOR
        );
// =========================
// TREE PANEL
// =========================

        graphics.fill(
                TREE_PANEL_X,
                TREE_PANEL_Y,
                TREE_PANEL_RIGHT,
                TREE_PANEL_BOTTOM,
                TREE_PANEL_FILL_COLOR
        );

        graphics.renderOutline(
                TREE_PANEL_X,
                TREE_PANEL_Y,
                TREE_PANEL_WIDTH,
                TREE_PANEL_HEIGHT,
                TREE_PANEL_OUTLINE_COLOR
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
        ResourceLocation woodcuttingBackground =
                ResourceLocation.fromNamespaceAndPath(
                        "proficiency",
                        "textures/gui/woodcutting_axe_stump_bg.png"
                );

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

        int woodcuttingPerkPoints =
                SkillManager.getWoodcuttingPerkPoints(
                        minecraft.player.getUUID()
                );

        int farmingLevel =
                SkillManager.getFarmingLevel(
                        minecraft.player.getUUID()
                );

        int farmingXp =
                SkillManager.getFarmingXp(
                        minecraft.player.getUUID()
                );

        int farmingXpRequired =
                SkillManager.getFarmingXpRequired(
                        minecraft.player.getUUID()
                );

        int farmingPerkPoints =
                SkillManager.getFarmingPerkPoints(
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
                    TREE_BACKGROUND_WIDTH,
                    TREE_BACKGROUND_HEIGHT,
                    TREE_BACKGROUND_WIDTH,
                    TREE_BACKGROUND_HEIGHT
            );
        }

        if (
                selectedSkill == 1
                        && backgroundEnabled
                        && minecraft.getResourceManager()
                                .getResource(woodcuttingBackground)
                                .isPresent()
        ) {

            graphics.blit(
                    woodcuttingBackground,
                    TREE_X,
                    TREE_Y,
                    0,
                    0,
                    TREE_BACKGROUND_WIDTH,
                    TREE_BACKGROUND_HEIGHT,
                    TREE_BACKGROUND_WIDTH,
                    TREE_BACKGROUND_HEIGHT
            );
        }

        // =========================
// BOTTOM INFO PANEL
// =========================

        graphics.fill(
                BOTTOM_PANEL_X,
                height - BOTTOM_PANEL_TOP_OFFSET,
                BOTTOM_PANEL_X + BOTTOM_PANEL_WIDTH,
                height - BOTTOM_PANEL_BOTTOM_OFFSET,
                PANEL_FILL_COLOR
        );

        graphics.renderOutline(
                BOTTOM_PANEL_X,
                height - BOTTOM_PANEL_TOP_OFFSET,
                BOTTOM_PANEL_WIDTH,
                BOTTOM_PANEL_HEIGHT,
                TREE_PANEL_OUTLINE_COLOR
        );
        if (selectedSkill == 0) {

                graphics.drawString(
                        font,
                        "Mining Level: " + miningLevel,
                        BOTTOM_INFO_X,
                        height - MINING_LEVEL_Y_OFFSET,
                        0xFFD700
                );

                graphics.drawString(
                        font,
                        "Perk Points: " + miningPerkPoints,
                        BOTTOM_INFO_X,
                        height - PERK_POINTS_Y_OFFSET,
                        0x55FFFF
                );
                drawXpBar(
                        graphics,
                        MINING_XP_BAR_X,
                        height - MINING_XP_BAR_Y_OFFSET,
                        MINING_XP_BAR_WIDTH,
                        XP_BAR_HEIGHT,
                        miningXp,
                        miningXpRequired,
                        0xFFFFAA00
                );
                if (miningPerkPoints > 0) {

                    graphics.drawString(
                            font,
                            "Spend your perk points!",
                            MINING_XP_BAR_X,
                            height - PERK_PROMPT_Y_OFFSET,
                            0x55FF55
                    );
                }
                graphics.drawString(
                        font,
                        "Mining Speed: +"
                                + getMiningSpeedBonusPercent(
                                        minecraft.player.getUUID()
                                )
                                + "%",
                        MINING_STAT_X,
                        MINING_SPEED_STAT_Y,
                        0x55FF55
                );

                graphics.drawString(
                        font,
                        "Fortune: +"
                                + getMiningFortuneBonus(
                                        minecraft.player.getUUID()
                                ),
                        MINING_STAT_X,
                        FORTUNE_STAT_Y,
                        0x55FFFF
                );

                graphics.drawString(
                        font,
                        "Durability: +"
                                + getMiningDurabilityBonusPercent(
                                        minecraft.player.getUUID()
                                )
                                + "%",
                        MINING_STAT_X,
                        DURABILITY_STAT_Y,
                        0xAAAAAA
                );

                graphics.drawString(
                        font,
                        "Ore Sense: Tier "
                                + getOreSenseTier(
                                        minecraft.player.getUUID()
                                ),
                        MINING_STAT_X,
                        ORE_SENSE_STAT_Y,
                        0xFFAA00
                );

                if (
                        hasCaveVision(
                                minecraft.player.getUUID()
                        )
                ) {

                    graphics.drawString(
                            font,
                            "Cave Vision: Enabled",
                            MINING_STAT_X,
                            ORE_SENSE_STAT_Y + 20,
                            0xAA88FF
                    );
                }
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

                int color =
                        getConnectionColor(
                                perk,
                                miningLevel,
                                miningPerkPoints
                        );

                int x1 =
                        parent.getX()
                                + TREE_OFFSET_X
                                + PERK_LINE_CENTER_OFFSET;

                int y1 =
                        parent.getY()
                                + TREE_OFFSET_Y
                                + PERK_LINE_CENTER_OFFSET;

                int x2 =
                        perk.getX()
                                + TREE_OFFSET_X
                                + PERK_LINE_CENTER_OFFSET;

                int y2 =
                        perk.getY()
                                + TREE_OFFSET_Y
                                + PERK_LINE_CENTER_OFFSET;

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

                    int fillColor =
                            getPerkFillColor(
                                    perk,
                                    miningLevel,
                                    miningPerkPoints
                            );

                    int borderColor =
                            getPerkBorderColor(
                                    perk,
                                    miningLevel,
                                    miningPerkPoints
                            );

                    graphics.fill(
                            perkX,
                            perkY,
                            perkX + PERK_NODE_SIZE,
                            perkY + PERK_NODE_SIZE,
                            fillColor
                    );

                    graphics.renderOutline(
                            perkX,
                            perkY,
                            PERK_NODE_SIZE,
                            PERK_NODE_SIZE,
                            borderColor
                    );

                    // Hover tooltip
                    if (
                            mouseX >= perkX
                                    && mouseX <= perkX + PERK_NODE_SIZE
                                    && mouseY >= perkY
                                    && mouseY <= perkY + PERK_NODE_SIZE
                    ) {

                        graphics.renderTooltip(
                                font,
                                List.of(
                                        Component.literal(
                                                perk.getName()
                                        ).getVisualOrderText(),
                                        Component.literal(
                                                "Requires Mining Level: "
                                                        + perk.getRequiredLevel()
                                        ).getVisualOrderText(),
                                        Component.literal(
                                                perk.getDescription()
                                        ).getVisualOrderText(),
                                        Component.literal(
                                                perk.getEffectText()
                                        ).getVisualOrderText()
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
            int oreY = ORE_START_Y;

// =========================
// TIER 1
// =========================

            drawOreToggle(
                    graphics,
                    "coal",
                    oreY
            );

            oreY += ORE_ROW_STEP;

            drawOreToggle(
                    graphics,
                    "redstone",
                    oreY
            );

            oreY += ORE_ROW_STEP + ORE_TIER_GAP;

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

                oreY += ORE_ROW_STEP;

                drawOreToggle(
                        graphics,
                        "copper",
                        oreY
                );

                oreY += ORE_ROW_STEP;

                drawOreToggle(
                        graphics,
                        "lapis",
                        oreY
                );

                oreY += ORE_ROW_STEP + ORE_TIER_GAP;
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

                oreY += ORE_ROW_STEP;

                drawOreToggle(
                        graphics,
                        "emerald",
                        oreY
                );

                oreY += ORE_ROW_STEP;

                drawOreToggle(
                        graphics,
                        "diamond",
                        oreY
                );

                oreY += ORE_ROW_STEP + ORE_TIER_GAP;
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
                BACKGROUND_LABEL_X,
                BACKGROUND_LABEL_Y,
                backgroundEnabled
                        ? 0x55FF55
                        : 0xFF5555
        );
        // =========================
// TOP SKILL TABS
// =========================

// Mining tab background
        graphics.fill(
                MINING_TAB_X,
                MINING_TAB_Y,
                MINING_TAB_X + MINING_TAB_WIDTH,
                MINING_TAB_Y + MINING_TAB_HEIGHT,
                selectedSkill == 0
                        ? ACTIVE_TAB_FILL_COLOR
                        : PANEL_FILL_COLOR
        );

        graphics.renderOutline(
                MINING_TAB_X,
                MINING_TAB_Y,
                MINING_TAB_WIDTH,
                MINING_TAB_HEIGHT,
                TAB_OUTLINE_COLOR
        );

// Woodcutting tab background
        graphics.fill(
                WOODCUTTING_TAB_X,
                WOODCUTTING_TAB_Y,
                WOODCUTTING_TAB_X + WOODCUTTING_TAB_WIDTH,
                WOODCUTTING_TAB_Y + WOODCUTTING_TAB_HEIGHT,
                selectedSkill == 1
                        ? ACTIVE_TAB_FILL_COLOR
                        : PANEL_FILL_COLOR
        );

        graphics.renderOutline(
                WOODCUTTING_TAB_X,
                WOODCUTTING_TAB_Y,
                WOODCUTTING_TAB_WIDTH,
                WOODCUTTING_TAB_HEIGHT,
                TAB_OUTLINE_COLOR
        );

// Farming tab background
        graphics.fill(
                FARMING_TAB_X,
                FARMING_TAB_Y,
                FARMING_TAB_X + FARMING_TAB_WIDTH,
                FARMING_TAB_Y + FARMING_TAB_HEIGHT,
                selectedSkill == 2
                        ? ACTIVE_TAB_FILL_COLOR
                        : PANEL_FILL_COLOR
        );

        graphics.renderOutline(
                FARMING_TAB_X,
                FARMING_TAB_Y,
                FARMING_TAB_WIDTH,
                FARMING_TAB_HEIGHT,
                TAB_OUTLINE_COLOR
        );

// Tab text
        graphics.drawCenteredString(
                font,
                "MINING",
                MINING_TAB_LABEL_X,
                TAB_LABEL_Y,
                selectedSkill == 0
                        ? 0xFFFF55
                        : 0xAAAAAA
        );

        graphics.drawCenteredString(
                font,
                "WOODCUTTING",
                WOODCUTTING_TAB_LABEL_X,
                TAB_LABEL_Y,
                selectedSkill == 1
                        ? 0x55FF55
                        : 0xAAAAAA
        );

        graphics.drawCenteredString(
                font,
                "FARMING",
                FARMING_TAB_LABEL_X,
                TAB_LABEL_Y,
                selectedSkill == 2
                        ? 0xFFFF55
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
                    BOTTOM_INFO_X,
                    height - MINING_LEVEL_Y_OFFSET,
                    0x55FF55
            );

            graphics.drawString(
                    font,
                    "Perk Points: " + woodcuttingPerkPoints,
                    BOTTOM_INFO_X,
                    height - PERK_POINTS_Y_OFFSET,
                    0x55FFFF
            );

            drawXpBar(
                    graphics,
                    MINING_XP_BAR_X,
                    height - MINING_XP_BAR_Y_OFFSET,
                    MINING_XP_BAR_WIDTH,
                    XP_BAR_HEIGHT,
                    woodcuttingXp,
                    woodcuttingXpRequired,
                    0xFF55AA55
            );

            graphics.drawString(
                    font,
                    "Chop Speed: +"
                            + getWoodcuttingChopSpeedBonusPercent(
                                    minecraft.player.getUUID()
                            )
                            + "%",
                    MINING_STAT_X,
                    MINING_SPEED_STAT_Y,
                    0x55FF55
            );

            graphics.drawString(
                    font,
                    "Foraging: +"
                            + getWoodcuttingForagingBonus(
                                    minecraft.player.getUUID()
                            ),
                    MINING_STAT_X,
                    FORTUNE_STAT_Y,
                    0x55FFFF
            );

            graphics.drawString(
                    font,
                    "Axe Durability: +"
                            + getWoodcuttingDurabilityBonusPercent(
                                    minecraft.player.getUUID()
                            )
                            + "%",
                    MINING_STAT_X,
                    DURABILITY_STAT_Y,
                    0xAAAAAA
            );

            graphics.drawString(
                    font,
                    "Drops: T"
                            + getWoodcuttingBonusDropsTier(
                                    minecraft.player.getUUID()
                            )
                            + " | Combat: +"
                            + getWoodcuttingCombatBonus(
                                    minecraft.player.getUUID()
                            ),
                    MINING_STAT_X,
                    ORE_SENSE_STAT_Y,
                    0xFFAA00
            );

            for (SkillPerk perk
                    : WoodcuttingPerks.ALL_PERKS) {

                if (perk.getParentId() == null) {
                    continue;
                }

                SkillPerk parent = null;

                for (SkillPerk possibleParent
                        : WoodcuttingPerks.ALL_PERKS) {

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

                int color =
                        getWoodcuttingConnectionColor(
                                perk,
                                woodcuttingLevel,
                                woodcuttingPerkPoints
                        );

                int x1 =
                        parent.getX()
                                + TREE_OFFSET_X
                                + PERK_LINE_CENTER_OFFSET;

                int y1 =
                        parent.getY()
                                + TREE_OFFSET_Y
                                + PERK_LINE_CENTER_OFFSET;

                int x2 =
                        perk.getX()
                                + TREE_OFFSET_X
                                + PERK_LINE_CENTER_OFFSET;

                int y2 =
                        perk.getY()
                                + TREE_OFFSET_Y
                                + PERK_LINE_CENTER_OFFSET;

                drawWoodcuttingConnection(
                        graphics,
                        perk,
                        x1,
                        y1,
                        x2,
                        y2,
                        color
                );
            }

            for (SkillPerk perk : WoodcuttingPerks.ALL_PERKS) {

                int perkX =
                        perk.getX()
                                + TREE_OFFSET_X;

                int perkY =
                        perk.getY()
                                + TREE_OFFSET_Y;

                int fillColor =
                        getWoodcuttingPerkFillColor(
                                perk,
                                woodcuttingLevel,
                                woodcuttingPerkPoints
                        );

                int borderColor =
                        getWoodcuttingPerkBorderColor(
                                perk,
                                woodcuttingLevel,
                                woodcuttingPerkPoints
                        );

                graphics.fill(
                        perkX,
                        perkY,
                        perkX + PERK_NODE_SIZE,
                        perkY + PERK_NODE_SIZE,
                        fillColor
                );

                graphics.renderOutline(
                        perkX,
                        perkY,
                        PERK_NODE_SIZE,
                        PERK_NODE_SIZE,
                        borderColor
                );

                if (
                        mouseX >= perkX
                                && mouseX <= perkX + PERK_NODE_SIZE
                                && mouseY >= perkY
                                && mouseY <= perkY + PERK_NODE_SIZE
                ) {

                    graphics.renderTooltip(
                            font,
                            List.of(
                                    Component.literal(
                                            perk.getName()
                                    ).getVisualOrderText(),
                                    Component.literal(
                                            "Requires Woodcutting Level: "
                                                    + perk.getRequiredLevel()
                                    ).getVisualOrderText(),
                                    Component.literal(
                                            perk.getDescription()
                                    ).getVisualOrderText(),
                                    Component.literal(
                                            perk.getEffectText()
                                    ).getVisualOrderText()
                            ),
                            mouseX,
                            mouseY
                    );
                }
            }
        }

        if (selectedSkill == 2) {

            graphics.drawString(
                    font,
                    "Farming Level: "
                            + farmingLevel,
                    BOTTOM_INFO_X,
                    height - MINING_LEVEL_Y_OFFSET,
                    0xFFFFCC55
            );

            graphics.drawString(
                    font,
                    "Perk Points: "
                            + farmingPerkPoints,
                    BOTTOM_INFO_X,
                    height - PERK_POINTS_Y_OFFSET,
                    0x55FFFF
            );

            drawXpBar(
                    graphics,
                    MINING_XP_BAR_X,
                    height - MINING_XP_BAR_Y_OFFSET,
                    MINING_XP_BAR_WIDTH,
                    XP_BAR_HEIGHT,
                    farmingXp,
                    farmingXpRequired,
                    0xFFFFCC55
            );

            graphics.drawString(
                    font,
                    "Growth Rate: +0%",
                    MINING_STAT_X,
                    MINING_SPEED_STAT_Y,
                    0x55FF55
            );

            graphics.drawString(
                    font,
                    "Yield Bonus: +0",
                    MINING_STAT_X,
                    FORTUNE_STAT_Y,
                    0x55FFFF
            );

            graphics.drawString(
                    font,
                    "Animal Care: +0",
                    MINING_STAT_X,
                    DURABILITY_STAT_Y,
                    0xAAAAAA
            );

            graphics.drawString(
                    font,
                    "Harvesting: +0",
                    MINING_STAT_X,
                    ORE_SENSE_STAT_Y,
                    0xFFAA00
            );

            for (SkillPerk perk
                    : FarmingPerks.ALL_PERKS) {

                if (perk.getParentId() == null) {

                    continue;
                }

                SkillPerk parent =
                        findFarmingPerk(
                                perk.getParentId()
                        );

                if (parent == null) {

                    continue;
                }

                int color =
                        getFarmingConnectionColor(
                                perk,
                                farmingLevel,
                                farmingPerkPoints
                        );

                drawWoodcuttingConnection(
                        graphics,
                        perk,
                        parent.getX()
                                + TREE_OFFSET_X
                                + PERK_LINE_CENTER_OFFSET,
                        parent.getY()
                                + TREE_OFFSET_Y
                                + PERK_LINE_CENTER_OFFSET,
                        perk.getX()
                                + TREE_OFFSET_X
                                + PERK_LINE_CENTER_OFFSET,
                        perk.getY()
                                + TREE_OFFSET_Y
                                + PERK_LINE_CENTER_OFFSET,
                        color
                );
            }

            for (SkillPerk perk
                    : FarmingPerks.ALL_PERKS) {

                int perkX =
                        perk.getX()
                                + TREE_OFFSET_X;

                int perkY =
                        perk.getY()
                                + TREE_OFFSET_Y;

                graphics.fill(
                        perkX,
                        perkY,
                        perkX + PERK_NODE_SIZE,
                        perkY + PERK_NODE_SIZE,
                        getFarmingPerkFillColor(
                                perk,
                                farmingLevel,
                                farmingPerkPoints
                        )
                );

                graphics.renderOutline(
                        perkX,
                        perkY,
                        PERK_NODE_SIZE,
                        PERK_NODE_SIZE,
                        getFarmingPerkBorderColor(
                                perk,
                                farmingLevel,
                                farmingPerkPoints
                        )
                );

                if (
                        mouseX >= perkX
                                && mouseX <= perkX + PERK_NODE_SIZE
                                && mouseY >= perkY
                                && mouseY <= perkY + PERK_NODE_SIZE
                ) {

                    graphics.renderTooltip(
                            font,
                            List.of(
                                    Component.literal(
                                            perk.getName()
                                    ).getVisualOrderText(),
                                    Component.literal(
                                            "Requires Farming Level: "
                                                    + perk.getRequiredLevel()
                                    ).getVisualOrderText(),
                                    Component.literal(
                                            perk.getDescription()
                                    ).getVisualOrderText(),
                                    Component.literal(
                                            perk.getEffectText()
                                    ).getVisualOrderText()
                            ),
                            mouseX,
                            mouseY
                    );
                }
            }
        }
    }
    private int getMiningSpeedBonusPercent(UUID playerId) {

        int hasteLevel = 0;

        if (
                SkillManager.hasMiningPerk(
                        playerId,
                        "stonecutter"
                )
        ) {

            hasteLevel = 1;
        }

        if (
                SkillManager.hasMiningPerk(
                        playerId,
                        "miners_momentum"
                )
        ) {

            int streak =
                    SkillManager.getMiningStreak(
                            playerId
                    );

            if (streak >= 50) {

                hasteLevel =
                        Math.max(
                                hasteLevel,
                                3
                        );

            } else if (streak >= 30) {

                hasteLevel =
                        Math.max(
                                hasteLevel,
                                2
                        );

            } else if (streak >= 10) {

                hasteLevel =
                        Math.max(
                                hasteLevel,
                                1
                        );
            }
        }

        return hasteLevel * 20;
    }

    private int getMiningFortuneBonus(UUID playerId) {

        if (
                SkillManager.hasMiningPerk(
                        playerId,
                        "no_ore_escapes"
                )
        ) {

            return 1;
        }

        return 0;
    }

    private int getMiningDurabilityBonusPercent(UUID playerId) {

        if (
                SkillManager.hasMiningPerk(
                        playerId,
                        "nearly_indestructible"
                )
        ) {

            return 75;
        }

        if (
                SkillManager.hasMiningPerk(
                        playerId,
                        "tempered_tools"
                )
        ) {

            return 40;
        }

        if (
                SkillManager.hasMiningPerk(
                        playerId,
                        "reinforced_grip"
                )
        ) {

            return 25;
        }

        if (
                SkillManager.hasMiningPerk(
                        playerId,
                        "better_handling"
                )
        ) {

            return 10;
        }

        return 0;
    }

    private int getOreSenseTier(UUID playerId) {

        if (
                SkillManager.hasMiningPerk(
                        playerId,
                        "it_smells_4"
                )
        ) {

            return 4;
        }

        if (
                SkillManager.hasMiningPerk(
                        playerId,
                        "it_smells_3"
                )
        ) {

            return 3;
        }

        if (
                SkillManager.hasMiningPerk(
                        playerId,
                        "it_smells_2"
                )
        ) {

            return 2;
        }

        if (
                SkillManager.hasMiningPerk(
                        playerId,
                        "they_have_a_scent"
                )
        ) {

            return 1;
        }

        return 0;
    }

    private boolean hasCaveVision(UUID playerId) {

        return SkillManager.hasMiningPerk(
                playerId,
                "deep_delver"
        );
    }

    private int getWoodcuttingChopSpeedBonusPercent(UUID playerId) {

        if (
                SkillManager.hasWoodcuttingPerk(
                        playerId,
                        "rhythm_of_the_forest"
                )
        ) {

            return 50;
        }

        if (
                SkillManager.hasWoodcuttingPerk(
                        playerId,
                        "felling_momentum"
                )
        ) {

            return 35;
        }

        if (
                SkillManager.hasWoodcuttingPerk(
                        playerId,
                        "clean_swing"
                )
        ) {

            return 25;
        }

        if (
                SkillManager.hasWoodcuttingPerk(
                        playerId,
                        "lumberjacks_stance"
                )
        ) {

            return 15;
        }

        if (
                SkillManager.hasWoodcuttingPerk(
                        playerId,
                        "timber_training"
                )
        ) {

            return 10;
        }

        return 0;
    }

    private int getWoodcuttingForagingBonus(UUID playerId) {

        int bonus = 0;

        if (
                SkillManager.hasWoodcuttingPerk(
                        playerId,
                        "twigs_everywhere"
                )
        ) {

            bonus++;
        }

        if (
                SkillManager.hasWoodcuttingPerk(
                        playerId,
                        "green_thumb"
                )
        ) {

            bonus++;
        }

        if (
                SkillManager.hasWoodcuttingPerk(
                        playerId,
                        "apple_picker"
                )
        ) {

            bonus++;
        }

        if (
                SkillManager.hasWoodcuttingPerk(
                        playerId,
                        "natures_gift"
                )
        ) {

            bonus++;
        }

        if (
                SkillManager.hasWoodcuttingPerk(
                        playerId,
                        "fast_decay"
                )
        ) {

            bonus++;
        }

        if (
                SkillManager.hasWoodcuttingPerk(
                        playerId,
                        "autumn_winds"
                )
        ) {

            bonus++;
        }

        if (
                SkillManager.hasWoodcuttingPerk(
                        playerId,
                        "clean_forest_floor"
                )
        ) {

            bonus++;
        }

        return bonus;
    }

    private int getWoodcuttingDurabilityBonusPercent(UUID playerId) {

        if (
                SkillManager.hasWoodcuttingPerk(
                        playerId,
                        "veteran_woodsman"
                )
        ) {

            return 75;
        }

        if (
                SkillManager.hasWoodcuttingPerk(
                        playerId,
                        "seasoned_haft"
                )
        ) {

            return 50;
        }

        if (
                SkillManager.hasWoodcuttingPerk(
                        playerId,
                        "callused_hands"
                )
        ) {

            return 35;
        }

        if (
                SkillManager.hasWoodcuttingPerk(
                        playerId,
                        "reinforced_haft"
                )
        ) {

            return 20;
        }

        if (
                SkillManager.hasWoodcuttingPerk(
                        playerId,
                        "proper_grip"
                )
        ) {

            return 10;
        }

        return 0;
    }

    private int getWoodcuttingBonusDropsTier(UUID playerId) {

        if (
                SkillManager.hasWoodcuttingPerk(
                        playerId,
                        "natures_gift"
                )
        ) {

            return 4;
        }

        if (
                SkillManager.hasWoodcuttingPerk(
                        playerId,
                        "apple_picker"
                )
        ) {

            return 3;
        }

        if (
                SkillManager.hasWoodcuttingPerk(
                        playerId,
                        "green_thumb"
                )
        ) {

            return 2;
        }

        if (
                SkillManager.hasWoodcuttingPerk(
                        playerId,
                        "twigs_everywhere"
                )
        ) {

            return 1;
        }

        return 0;
    }

    private int getWoodcuttingCombatBonus(UUID playerId) {

        int bonus = 0;

        String[] combatPerks = {
                "splinter_fighter",
                "axe_training",
                "heavy_chop",
                "cleaving_swing",
                "decapitation_chance",
                "splinter_fighter_2",
                "quick_hatchet",
                "battle_axe_mastery"
        };

        for (String perkId : combatPerks) {

            if (
                    SkillManager.hasWoodcuttingPerk(
                            playerId,
                            perkId
                    )
            ) {

                bonus++;
            }
        }

        return bonus;
    }

    private void showPerkUnlockToast(SkillPerk perk) {

        SystemToast.add(
                minecraft.getToasts(),
                SystemToast.SystemToastId.PERIODIC_NOTIFICATION,
                Component.literal("NEW PERK UNLOCKED!"),
                Component.literal(
                        perk.getName()
                                + ": "
                                + perk.getDescription()
                )
        );
    }

    private int getConnectionColor(
            SkillPerk perk,
            int miningLevel,
            int miningPerkPoints
    ) {

        if (isPerkUnlocked(perk)) {

            return PERK_UNLOCKED_COLOR;
        }

        if (
                isPerkAvailable(
                        perk,
                        miningLevel,
                        miningPerkPoints
                )
        ) {

            return PERK_AVAILABLE_BORDER_COLOR;
        }

        return PERK_LOCKED_BORDER_COLOR;
    }

    private int getPerkFillColor(
            SkillPerk perk,
            int miningLevel,
            int miningPerkPoints
    ) {

        if (isPerkUnlocked(perk)) {

            return PERK_UNLOCKED_COLOR;
        }

        if (
                isPerkAvailable(
                        perk,
                        miningLevel,
                        miningPerkPoints
                )
        ) {

            return PERK_AVAILABLE_FILL_COLOR;
        }

        return PERK_LOCKED_FILL_COLOR;
    }

    private int getPerkBorderColor(
            SkillPerk perk,
            int miningLevel,
            int miningPerkPoints
    ) {

        if (isPerkUnlocked(perk)) {

            return PERK_UNLOCKED_COLOR;
        }

        if (
                isPerkAvailable(
                        perk,
                        miningLevel,
                        miningPerkPoints
                )
        ) {

            return PERK_AVAILABLE_BORDER_COLOR;
        }

        return PERK_LOCKED_BORDER_COLOR;
    }

    private boolean isPerkAvailable(
            SkillPerk perk,
            int miningLevel,
            int miningPerkPoints
    ) {

        return !isPerkUnlocked(perk)
                && miningLevel >= perk.getRequiredLevel()
                && miningPerkPoints > 0
                && isParentUnlocked(perk);
    }

    private boolean isPerkUnlocked(SkillPerk perk) {

        return SkillManager.hasMiningPerk(
                minecraft.player.getUUID(),
                perk.getId()
        );
    }

    private boolean isParentUnlocked(SkillPerk perk) {

        if (perk.getParentId() == null) {

            return true;
        }

        return SkillManager.hasMiningPerk(
                minecraft.player.getUUID(),
                perk.getParentId()
        );
    }

    private int getWoodcuttingConnectionColor(
            SkillPerk perk,
            int woodcuttingLevel,
            int woodcuttingPerkPoints
    ) {

        if (isWoodcuttingPerkUnlocked(perk)) {

            return PERK_UNLOCKED_COLOR;
        }

        if (
                isWoodcuttingPerkAvailable(
                        perk,
                        woodcuttingLevel,
                        woodcuttingPerkPoints
                )
        ) {

            return PERK_AVAILABLE_BORDER_COLOR;
        }

        return PERK_LOCKED_BORDER_COLOR;
    }

    private void drawWoodcuttingConnection(
            GuiGraphics graphics,
            SkillPerk perk,
            int x1,
            int y1,
            int x2,
            int y2,
            int color
    ) {

        if (x1 == x2 || y1 == y2) {

            drawWoodcuttingConnectionSegment(
                    graphics,
                    x1,
                    y1,
                    x2,
                    y2,
                    color
            );

            return;
        }

        int middleY =
                y1 + (y2 - y1) / 2;

        drawWoodcuttingConnectionSegment(
                graphics,
                x1,
                y1,
                x1,
                middleY,
                color
        );

        drawWoodcuttingConnectionSegment(
                graphics,
                x1,
                middleY,
                x2,
                middleY,
                color
        );

        drawWoodcuttingConnectionSegment(
                graphics,
                x2,
                middleY,
                x2,
                y2,
                color
        );
    }

    private void drawWoodcuttingConnectionSegment(
            GuiGraphics graphics,
            int x1,
            int y1,
            int x2,
            int y2,
            int color
    ) {

        int steps =
                Math.max(
                        Math.abs(x2 - x1),
                        Math.abs(y2 - y1)
                );

        for (int i = 0; i <= steps; i += 3) {

            float progress =
                    steps == 0
                            ? 0.0f
                            : i / (float) steps;

            int x =
                    x1 + Math.round(
                            (x2 - x1) * progress
                    );

            int y =
                    y1 + Math.round(
                            (y2 - y1) * progress
                    );

            graphics.fill(
                    x,
                    y,
                    x + 2,
                    y + 2,
                    color
            );
        }
    }

    private int getWoodcuttingPerkFillColor(
            SkillPerk perk,
            int woodcuttingLevel,
            int woodcuttingPerkPoints
    ) {

        if (isWoodcuttingPerkUnlocked(perk)) {

            return PERK_UNLOCKED_COLOR;
        }

        if (
                isWoodcuttingPerkAvailable(
                        perk,
                        woodcuttingLevel,
                        woodcuttingPerkPoints
                )
        ) {

            return PERK_AVAILABLE_FILL_COLOR;
        }

        return PERK_LOCKED_FILL_COLOR;
    }

    private int getWoodcuttingPerkBorderColor(
            SkillPerk perk,
            int woodcuttingLevel,
            int woodcuttingPerkPoints
    ) {

        if (isWoodcuttingPerkUnlocked(perk)) {

            return PERK_UNLOCKED_COLOR;
        }

        if (
                isWoodcuttingPerkAvailable(
                        perk,
                        woodcuttingLevel,
                        woodcuttingPerkPoints
                )
        ) {

            return PERK_AVAILABLE_BORDER_COLOR;
        }

        return PERK_LOCKED_BORDER_COLOR;
    }

    private boolean isWoodcuttingPerkAvailable(
            SkillPerk perk,
            int woodcuttingLevel,
            int woodcuttingPerkPoints
    ) {

        return !isWoodcuttingPerkUnlocked(perk)
                && woodcuttingLevel >= perk.getRequiredLevel()
                && woodcuttingPerkPoints > 0
                && isWoodcuttingParentUnlocked(perk);
    }

    private boolean isWoodcuttingPerkUnlocked(SkillPerk perk) {

        return SkillManager.hasWoodcuttingPerk(
                minecraft.player.getUUID(),
                perk.getId()
        );
    }

    private boolean isWoodcuttingParentUnlocked(SkillPerk perk) {

        if (perk.getParentId() == null) {

            return true;
        }

        return SkillManager.hasWoodcuttingPerk(
                minecraft.player.getUUID(),
                perk.getParentId()
        );
    }

    private SkillPerk findFarmingPerk(
            String perkId
    ) {

        for (SkillPerk perk
                : FarmingPerks.ALL_PERKS) {

            if (perk.getId().equals(perkId)) {

                return perk;
            }
        }

        return null;
    }

    private int getFarmingConnectionColor(
            SkillPerk perk,
            int farmingLevel,
            int farmingPerkPoints
    ) {

        if (isFarmingPerkUnlocked(perk)) {

            return PERK_UNLOCKED_COLOR;
        }

        if (
                isFarmingPerkAvailable(
                        perk,
                        farmingLevel,
                        farmingPerkPoints
                )
        ) {

            return PERK_AVAILABLE_BORDER_COLOR;
        }

        return PERK_LOCKED_BORDER_COLOR;
    }

    private int getFarmingPerkFillColor(
            SkillPerk perk,
            int farmingLevel,
            int farmingPerkPoints
    ) {

        if (isFarmingPerkUnlocked(perk)) {

            return PERK_UNLOCKED_COLOR;
        }

        if (
                isFarmingPerkAvailable(
                        perk,
                        farmingLevel,
                        farmingPerkPoints
                )
        ) {

            return PERK_AVAILABLE_FILL_COLOR;
        }

        return PERK_LOCKED_FILL_COLOR;
    }

    private int getFarmingPerkBorderColor(
            SkillPerk perk,
            int farmingLevel,
            int farmingPerkPoints
    ) {

        if (isFarmingPerkUnlocked(perk)) {

            return PERK_UNLOCKED_COLOR;
        }

        if (
                isFarmingPerkAvailable(
                        perk,
                        farmingLevel,
                        farmingPerkPoints
                )
        ) {

            return PERK_AVAILABLE_BORDER_COLOR;
        }

        return PERK_LOCKED_BORDER_COLOR;
    }

    private boolean isFarmingPerkAvailable(
            SkillPerk perk,
            int farmingLevel,
            int farmingPerkPoints
    ) {

        return !isFarmingPerkUnlocked(perk)
                && farmingLevel >= perk.getRequiredLevel()
                && farmingPerkPoints > 0
                && isFarmingParentUnlocked(perk);
    }

    private boolean isFarmingPerkUnlocked(
            SkillPerk perk
    ) {

        return SkillManager.hasFarmingPerk(
                minecraft.player.getUUID(),
                perk.getId()
        );
    }

    private boolean isFarmingParentUnlocked(
            SkillPerk perk
    ) {

        if (perk.getParentId() == null) {

            return true;
        }

        return SkillManager.hasFarmingPerk(
                minecraft.player.getUUID(),
                perk.getParentId()
        );
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
                ORE_TOGGLE_X,
                y,
                selected
                        ? 0x55FF55
                        : 0xAAAAAA
        );
    }

    private int getWoodcuttingToggleRow(
            double mouseX,
            double mouseY
    ) {

        if (
                mouseX < WOODCUTTING_TOGGLE_X
                        || mouseX > WOODCUTTING_TOGGLE_X
                        + WOODCUTTING_TOGGLE_WIDTH
        ) {

            return -1;
        }

        for (int row = 0; row < 4; row++) {

            int rowY =
                    WOODCUTTING_TOGGLE_START_Y
                            + row
                            * WOODCUTTING_TOGGLE_ROW_STEP;

            if (
                    mouseY >= rowY
                            && mouseY <= rowY
                            + WOODCUTTING_TOGGLE_HEIGHT
            ) {

                return row;
            }
        }

        return -1;
    }

    private void drawWoodcuttingFeatureToggle(
            GuiGraphics graphics,
            String label,
            boolean enabled,
            int y
    ) {

        graphics.drawString(
                font,
                label
                        + ": "
                        + (
                        enabled
                                ? "ON"
                                : "OFF"
                ),
                WOODCUTTING_TOGGLE_X,
                y,
                enabled
                        ? 0x55FF55
                        : 0xAA5555
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
