package com.trojan.proficiency.client.screen;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import com.trojan.proficiency.client.ClientSkillState;
import com.trojan.proficiency.skill.MiningSkill;
import com.trojan.proficiency.perk.MiningPerks;
import com.trojan.proficiency.perk.SkillPerk;
import com.trojan.proficiency.perk.WoodcuttingPerks;
import com.trojan.proficiency.perk.FarmingPerks;
import com.trojan.proficiency.perk.OneHandedPerks;
import com.trojan.proficiency.skill.SkillType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
public class SkillScreen extends Screen {
    private static final int DESIGN_WIDTH = 960;
    private static final int DESIGN_HEIGHT = 500;
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

    private static final int ONE_HANDED_TAB_X = 530;
    private static final int ONE_HANDED_TAB_Y = 22;
    private static final int ONE_HANDED_TAB_WIDTH = 180;
    private static final int ONE_HANDED_TAB_HEIGHT = 25;
    private static final int ONE_HANDED_TAB_LABEL_X = 620;

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
    private static final int WOODCUTTING_TOGGLE_ROW_STEP = 15;

    private static final int SETTINGS_PANEL_X = 35;
    private static final int SETTINGS_PANEL_FILL_Y = 410;
    private static final int SETTINGS_PANEL_FILL_HEIGHT = 50;
    private static final int SETTINGS_PANEL_OUTLINE_Y = 400;
    private static final int SETTINGS_PANEL_WIDTH = 185;
    private static final int SETTINGS_PANEL_OUTLINE_HEIGHT = 70;
    private static final int SETTINGS_TITLE_X = 50;
    private static final int SETTINGS_TITLE_Y = 415;
    private static final int BACKGROUND_TOGGLE_X = 40;
    private static final int BACKGROUND_TOGGLE_Y = 450;
    private static final int BACKGROUND_TOGGLE_WIDTH = 140;
    private static final int BACKGROUND_TOGGLE_HEIGHT = 15;
    private static final int BACKGROUND_LABEL_X = 50;
    private static final int BACKGROUND_LABEL_Y = 450;
    private static final int HEAVY_SWINGS_TOGGLE_X = 40;
    private static final int HEAVY_SWINGS_TOGGLE_Y = 432;
    private static final int HEAVY_SWINGS_TOGGLE_WIDTH = 150;
    private static final int HEAVY_SWINGS_TOGGLE_HEIGHT = 12;

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
    private static final int PRESTIGE_BUTTON_X = 810;
    private static final int PRESTIGE_BUTTON_Y = 375;
    private static final int PRESTIGE_BUTTON_WIDTH = 110;
    private static final int PRESTIGE_BUTTON_HEIGHT = 20;
    private static final int PRESTIGE_DIALOG_X = 300;
    private static final int PRESTIGE_DIALOG_Y = 115;
    private static final int PRESTIGE_DIALOG_WIDTH = 360;
    private static final int PRESTIGE_DIALOG_HEIGHT = 260;

    private boolean backgroundEnabled = true;;
    private boolean dragging = false;

    private double lastMouseX;

    private double lastMouseY;

    private int selectedSkill = 0;
    private float uiScale = 1.0f;
    private float uiOffsetX;
    private float uiOffsetY;
    private boolean confirmingPrestige;

    public SkillScreen() {
        super(Component.literal("Proficiency"));
    }

    @Override
    protected void init() {
        super.init();
        updateUiTransform();
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
                selectedSkill = 3;
            }

            return true;
        }

        // Down arrow
        if (keyCode == 264) {

            selectedSkill++;

            if (selectedSkill > 3) {
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

        updateUiTransform();
        mouseX = (mouseX - uiOffsetX) / uiScale;
        mouseY = (mouseY - uiOffsetY) / uiScale;

        if (
                selectedSkill == 0
                        && ClientSkillState.hasMiningPerk(
                        minecraft.player.getUUID(),
                        "heavy_swings"
                )
                        && isInside(
                        mouseX,
                        mouseY,
                        HEAVY_SWINGS_TOGGLE_X,
                        HEAVY_SWINGS_TOGGLE_Y,
                        HEAVY_SWINGS_TOGGLE_WIDTH,
                        HEAVY_SWINGS_TOGGLE_HEIGHT
                )
        ) {
            ClientSkillState.toggleMiningHeavySwings(
                    minecraft.player.getUUID()
            );
            minecraft.player.playSound(
                    net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK.value(),
                    1.0f,
                    1.0f
            );
            return true;
        }

        if (confirmingPrestige) {
            if (isInside(mouseX, mouseY, 390, 335, 80, 22)) {
                ClientSkillState.requestPrestige(getSelectedSkillType());
                confirmingPrestige = false;
                return true;
            }

            if (isInside(mouseX, mouseY, 490, 335, 80, 22)) {
                confirmingPrestige = false;
                return true;
            }

            return true;
        }

        if (
                getSelectedSkillLevel() >= 150
                        && isInside(
                        mouseX,
                        mouseY,
                        PRESTIGE_BUTTON_X,
                        PRESTIGE_BUTTON_Y,
                        PRESTIGE_BUTTON_WIDTH,
                        PRESTIGE_BUTTON_HEIGHT
                )
        ) {
            confirmingPrestige = true;
            return true;
        }

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

        if (
                mouseX >= ONE_HANDED_TAB_X
                        && mouseX <= ONE_HANDED_TAB_X + ONE_HANDED_TAB_WIDTH
                        && mouseY >= ONE_HANDED_TAB_Y
                        && mouseY <= ONE_HANDED_TAB_Y + ONE_HANDED_TAB_HEIGHT
        ) {
            selectedSkill = 3;
            return true;
        }

        if (selectedSkill == 1) {

            UUID playerId = minecraft.player.getUUID();
            List<FeatureToggle> toggles =
                    getVisibleWoodcuttingToggles(playerId);

            int toggleRow =
                    getWoodcuttingToggleRow(
                            mouseX,
                            mouseY,
                            toggles.size()
                    );

            if (toggleRow >= 0) {

                toggleWoodcuttingFeature(
                        playerId,
                        toggles.get(toggleRow).id()
                );

                minecraft.player.playSound(
                        net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK.value(),
                        1.0f,
                        1.0f
                );

                return true;
            }
        }

        if (selectedSkill == 2) {

            UUID playerId = minecraft.player.getUUID();
            List<FeatureToggle> toggles =
                    getVisibleFarmingToggles(playerId);

            int toggleRow =
                    getWoodcuttingToggleRow(
                            mouseX,
                            mouseY,
                            toggles.size()
                    );

            if (toggleRow >= 0) {

                toggleFarmingFeature(
                        playerId,
                        toggles.get(toggleRow).id()
                );

                minecraft.player.playSound(
                        net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK.value(),
                        1.0f,
                        1.0f
                );

                return true;
            }
        }

        if (selectedSkill == 3) {
            List<FeatureToggle> toggles =
                    getVisibleOneHandedToggles(minecraft.player.getUUID());
            int toggleRow = getWoodcuttingToggleRow(
                    mouseX,
                    mouseY,
                    toggles.size()
            );
            if (toggleRow >= 0) {
                ClientSkillState.toggleOneHanded(
                        toggles.get(toggleRow).id()
                );
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

                    ClientSkillState.requestPerkUnlock(
                            SkillType.MINING,
                            perk.getId()
                        );

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

                    ClientSkillState.requestPerkUnlock(
                            SkillType.WOODCUTTING,
                            perk.getId()
                        );

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

                    ClientSkillState.requestPerkUnlock(
                            SkillType.FARMING,
                            perk.getId()
                        );

                    return true;
                }
            }
        }
        if (selectedSkill == 3) {
            for (SkillPerk perk : OneHandedPerks.ALL_PERKS) {
                int perkX = perk.getX() + TREE_OFFSET_X;
                int perkY = perk.getY() + TREE_OFFSET_Y;
                if (
                        mouseX >= perkX
                                && mouseX <= perkX + PERK_NODE_SIZE
                                && mouseY >= perkY
                                && mouseY <= perkY + PERK_NODE_SIZE
                ) {
                    ClientSkillState.requestPerkUnlock(
                            SkillType.ONE_HANDED,
                            perk.getId()
                    );
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

                        ClientSkillState.hasMiningPerk(
                                minecraft.player.getUUID(),
                                "it_smells_2"
                        );

                case "gold",
                     "emerald",
                     "diamond" ->

                        ClientSkillState.hasMiningPerk(
                                minecraft.player.getUUID(),
                                "it_smells_3"
                        );

                case "ancient_debris" ->

                        ClientSkillState.hasMiningPerk(
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

                ClientSkillState.toggleOreSense(
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

        updateUiTransform();

        renderBackground(
                graphics,
                mouseX,
                mouseY,
                partialTick
        );

        int actualWidth = width;
        int actualHeight = height;
        int designMouseX = Math.round(
                (mouseX - uiOffsetX) / uiScale
        );
        int designMouseY = Math.round(
                (mouseY - uiOffsetY) / uiScale
        );

        graphics.pose().pushPose();
        graphics.pose().translate(uiOffsetX, uiOffsetY, 0.0f);
        graphics.pose().scale(uiScale, uiScale, 1.0f);
        width = DESIGN_WIDTH;
        height = DESIGN_HEIGHT;
        mouseX = designMouseX;
        mouseY = designMouseY;


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
                        : selectedSkill == 3
                        ? "COMBAT OPTIONS"
                        : "ORE SENSING",
                ORE_TITLE_X,
                ORE_TITLE_Y,
                TITLE_COLOR
        );

        if (selectedSkill == 1) {

            drawFeatureToggles(
                    graphics,
                    getVisibleWoodcuttingToggles(
                            minecraft.player.getUUID()
                    )
            );
        }

        if (selectedSkill == 2) {

            drawFeatureToggles(
                    graphics,
                    getVisibleFarmingToggles(
                            minecraft.player.getUUID()
                    )
            );
        }

        if (selectedSkill == 3) {
            drawFeatureToggles(
                    graphics,
                    getVisibleOneHandedToggles(
                            minecraft.player.getUUID()
                    )
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
                ClientSkillState.getMiningLevel(
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
                        "textures/gui/woodcutting_bg.png"
                );

        ResourceLocation farmingBackground =
                ResourceLocation.fromNamespaceAndPath(
                        "proficiency",
                        "textures/gui/farming_bg.png"
                );

        ResourceLocation oneHandedBackground =
                ResourceLocation.fromNamespaceAndPath(
                        "proficiency",
                        "textures/gui/one_handed_bg.png"
                );

        int miningXp =
                ClientSkillState.getMiningXp(
                        minecraft.player.getUUID()
                );

        int miningXpRequired =
                ClientSkillState.getMiningXpRequired(
                        minecraft.player.getUUID()
                );

        int miningPerkPoints =
                ClientSkillState.getMiningPerkPoints(
                        minecraft.player.getUUID()
                );

        int woodcuttingLevel =
                ClientSkillState.getWoodcuttingLevel(
                        minecraft.player.getUUID()
                );

        int woodcuttingXp =
                ClientSkillState.getWoodcuttingXp(
                        minecraft.player.getUUID()
                );

        int woodcuttingXpRequired =
                ClientSkillState.getWoodcuttingXpRequired(
                        minecraft.player.getUUID()
                );

        int woodcuttingPerkPoints =
                ClientSkillState.getWoodcuttingPerkPoints(
                        minecraft.player.getUUID()
                );

        int farmingLevel =
                ClientSkillState.getFarmingLevel(
                        minecraft.player.getUUID()
                );

        int farmingXp =
                ClientSkillState.getFarmingXp(
                        minecraft.player.getUUID()
                );

        int farmingXpRequired =
                ClientSkillState.getFarmingXpRequired(
                        minecraft.player.getUUID()
                );

        int farmingPerkPoints =
                ClientSkillState.getFarmingPerkPoints(
                        minecraft.player.getUUID()
                );
        int oneHandedLevel = ClientSkillState.getOneHandedLevel(
                minecraft.player.getUUID()
        );
        int oneHandedXp = ClientSkillState.getOneHandedXp(
                minecraft.player.getUUID()
        );
        int oneHandedXpRequired = ClientSkillState.getOneHandedXpRequired(
                minecraft.player.getUUID()
        );
        int oneHandedPerkPoints = ClientSkillState.getOneHandedPerkPoints(
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

        if (
                selectedSkill == 2
                        && backgroundEnabled
                        && minecraft.getResourceManager()
                                .getResource(farmingBackground)
                                .isPresent()
        ) {

            graphics.blit(
                    farmingBackground,
                    TREE_X,
                    TREE_Y,
                    0,
                    0,
                    TREE_BACKGROUND_WIDTH,
                    TREE_BACKGROUND_HEIGHT,
                    TREE_BACKGROUND_WIDTH,
                    TREE_BACKGROUND_HEIGHT
            );

            graphics.fill(
                    TREE_X,
                    TREE_Y,
                    TREE_X + TREE_BACKGROUND_WIDTH,
                    TREE_Y + TREE_BACKGROUND_HEIGHT,
                    0x33000000
            );
        }

        if (
                selectedSkill == 3
                        && backgroundEnabled
                        && minecraft.getResourceManager()
                                .getResource(oneHandedBackground)
                                .isPresent()
        ) {

            graphics.blit(
                    oneHandedBackground,
                    TREE_X,
                    TREE_Y,
                    0,
                    0,
                    TREE_BACKGROUND_WIDTH,
                    TREE_BACKGROUND_HEIGHT,
                    TREE_BACKGROUND_WIDTH,
                    TREE_BACKGROUND_HEIGHT
            );

            graphics.fill(
                    TREE_X,
                    TREE_Y,
                    TREE_X + TREE_BACKGROUND_WIDTH,
                    TREE_Y + TREE_BACKGROUND_HEIGHT,
                    0x33000000
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

                SkillPerk parent =
                        MiningPerks.getById(
                                perk.getParentId()
                        );

                if (parent == null) {
                    continue;
                }

                int color =
                        getConnectionColor(
                                parent,
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
                        resolveConnectionSegmentColor(
                                SkillType.MINING,
                                parent,
                                perk,
                                color
                        )
                );

                graphics.vLine(
                        x2,
                        Math.min(y1, y2),
                        Math.max(y1, y2),
                        resolveConnectionSegmentColor(
                                SkillType.MINING,
                                parent,
                                perk,
                                color
                        )
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
                                                "Cost: "
                                                        + perk.getPointCost()
                                                        + " Perk Points"
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
                    ClientSkillState.getSelectedOreSense(
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
                    ClientSkillState.hasMiningPerk(
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
                    ClientSkillState.hasMiningPerk(
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
                    ClientSkillState.hasMiningPerk(
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
        if (
                selectedSkill == 0
                        && ClientSkillState.hasMiningPerk(
                        minecraft.player.getUUID(),
                        "heavy_swings"
                )
        ) {
            boolean heavySwingsEnabled =
                    ClientSkillState.isMiningHeavySwingsEnabled(
                            minecraft.player.getUUID()
                    );
            graphics.drawString(
                    font,
                    "Heavy Swings: "
                            + (heavySwingsEnabled ? "ON" : "OFF"),
                    HEAVY_SWINGS_TOGGLE_X,
                    HEAVY_SWINGS_TOGGLE_Y,
                    heavySwingsEnabled ? 0x55FF55 : 0xFF5555
            );
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

        graphics.fill(
                ONE_HANDED_TAB_X,
                ONE_HANDED_TAB_Y,
                ONE_HANDED_TAB_X + ONE_HANDED_TAB_WIDTH,
                ONE_HANDED_TAB_Y + ONE_HANDED_TAB_HEIGHT,
                selectedSkill == 3 ? ACTIVE_TAB_FILL_COLOR : PANEL_FILL_COLOR
        );
        graphics.renderOutline(
                ONE_HANDED_TAB_X,
                ONE_HANDED_TAB_Y,
                ONE_HANDED_TAB_WIDTH,
                ONE_HANDED_TAB_HEIGHT,
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

        graphics.drawCenteredString(
                font,
                "ONE-HANDED",
                ONE_HANDED_TAB_LABEL_X,
                TAB_LABEL_Y,
                selectedSkill == 3 ? 0xFFFF5555 : 0xAAAAAA
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

                SkillPerk parent =
                        WoodcuttingPerks.getById(
                                perk.getParentId()
                        );

                if (parent == null) {
                    continue;
                }

                int color =
                        getWoodcuttingConnectionColor(
                                parent,
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
                        SkillType.WOODCUTTING,
                        parent,
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
                                            "Cost: "
                                                    + perk.getPointCost()
                                                    + " Perk Points"
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
                    "Growth Rate: +"
                            + getFarmingGrowthRatePercent(
                            minecraft.player.getUUID()
                    )
                            + "%",
                    MINING_STAT_X,
                    MINING_SPEED_STAT_Y,
                    0x55FF55
            );

            graphics.drawString(
                    font,
                    "Yield Bonus: +"
                            + getFarmingYieldBonusPercent(
                            minecraft.player.getUUID()
                    )
                            + "%",
                    MINING_STAT_X,
                    FORTUNE_STAT_Y,
                    0x55FFFF
            );

            graphics.drawString(
                    font,
                    "Animal Care: +"
                            + getFarmingAnimalCarePercent(
                            minecraft.player.getUUID()
                    )
                            + "%",
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
                        FarmingPerks.getById(
                                perk.getParentId()
                        );

                if (parent == null) {

                    continue;
                }

                int color =
                        getFarmingConnectionColor(
                                parent,
                                perk,
                                farmingLevel,
                                farmingPerkPoints
                        );

                drawWoodcuttingConnection(
                        graphics,
                        SkillType.FARMING,
                        parent,
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
                                            "Cost: "
                                                    + perk.getPointCost()
                                                    + " Perk Points"
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

        if (selectedSkill == 3) {
            UUID oneHandedPlayerId = minecraft.player.getUUID();
            int oneHandedSpeedBonus =
                    (ClientSkillState.hasOneHandedPerk(oneHandedPlayerId, "blade_training") ? 5 : 0)
                            + (ClientSkillState.hasOneHandedPerk(oneHandedPlayerId, "monster_hunter") ? 5 : 0)
                            + (ClientSkillState.hasOneHandedPerk(oneHandedPlayerId, "offhand_strike")
                            && ClientSkillState.isOneHandedToggleEnabled("dual_wield") ? 5 : 0);
            int oneHandedDurabilityBonus = ClientSkillState.hasOneHandedPerk(oneHandedPlayerId, "monster_hunter")
                    ? 25 : ClientSkillState.hasOneHandedPerk(oneHandedPlayerId, "precise_strikes") ? 15 : 0;
            int oneHandedLootingBonus = ClientSkillState.hasOneHandedPerk(oneHandedPlayerId, "trophy_collector")
                    ? 1 : 0;
            int oneHandedDefenseBonus = ClientSkillState.hasOneHandedPerk(oneHandedPlayerId, "shield_training")
                    && ClientSkillState.isOneHandedToggleEnabled("shield_effects") ? 2 : 0;

            graphics.drawCenteredString(
                    font,
                    "BERSERKER",
                    TREE_OFFSET_X + 130,
                    65,
                    0xFFFF7777
            );
            graphics.drawCenteredString(
                    font,
                    "DUELIST",
                    TREE_OFFSET_X + 225,
                    65,
                    0xFFFFFF77
            );
            graphics.drawCenteredString(
                    font,
                    "GUARDIAN",
                    TREE_OFFSET_X + 320,
                    65,
                    0xFF77AAFF
            );
            graphics.drawCenteredString(
                    font,
                    "WEAPON MASTERY",
                    TREE_OFFSET_X + 415,
                    65,
                    0xFFDD99FF
            );

            graphics.drawString(font, "One-Handed Level: " + oneHandedLevel,
                    BOTTOM_INFO_X, height - MINING_LEVEL_Y_OFFSET, 0xFFFF5555);
            graphics.drawString(font, "Perk Points: " + oneHandedPerkPoints,
                    BOTTOM_INFO_X, height - PERK_POINTS_Y_OFFSET, 0x55FFFF);
            drawXpBar(graphics, MINING_XP_BAR_X,
                    height - MINING_XP_BAR_Y_OFFSET,
                    MINING_XP_BAR_WIDTH, XP_BAR_HEIGHT,
                    oneHandedXp, oneHandedXpRequired, 0xFFFF5555);

            graphics.drawString(font, "Attack Speed: +" + oneHandedSpeedBonus + "%", MINING_STAT_X,
                    MINING_SPEED_STAT_Y, 0x55FF55);
            graphics.drawString(font, "Durability: +" + oneHandedDurabilityBonus + "%", MINING_STAT_X,
                    FORTUNE_STAT_Y, 0x55FFFF);
            graphics.drawString(font, "Looting: +" + oneHandedLootingBonus, MINING_STAT_X,
                    DURABILITY_STAT_Y, 0xAAAAAA);
            graphics.drawString(font, "Defense: +" + oneHandedDefenseBonus, MINING_STAT_X,
                    ORE_SENSE_STAT_Y, 0xFFAA00);

            for (SkillPerk perk : OneHandedPerks.ALL_PERKS) {
                if (perk.getParentId() == null) {
                    continue;
                }
                SkillPerk parent = OneHandedPerks.getById(perk.getParentId());
                if (parent == null) {
                    continue;
                }
                int color = getOneHandedConnectionColor(
                        parent,
                        perk,
                        oneHandedLevel,
                        oneHandedPerkPoints
                );
                drawWoodcuttingConnection(
                        graphics,
                        SkillType.ONE_HANDED,
                        parent,
                        perk,
                        parent.getX() + TREE_OFFSET_X + PERK_LINE_CENTER_OFFSET,
                        parent.getY() + TREE_OFFSET_Y + PERK_LINE_CENTER_OFFSET,
                        perk.getX() + TREE_OFFSET_X + PERK_LINE_CENTER_OFFSET,
                        perk.getY() + TREE_OFFSET_Y + PERK_LINE_CENTER_OFFSET,
                        color
                );
            }

            for (SkillPerk perk : OneHandedPerks.ALL_PERKS) {
                int perkX = perk.getX() + TREE_OFFSET_X;
                int perkY = perk.getY() + TREE_OFFSET_Y;
                graphics.fill(perkX, perkY,
                        perkX + PERK_NODE_SIZE, perkY + PERK_NODE_SIZE,
                        getOneHandedPerkFillColor(
                                perk,
                                oneHandedLevel,
                                oneHandedPerkPoints
                        ));
                graphics.renderOutline(perkX, perkY,
                        PERK_NODE_SIZE, PERK_NODE_SIZE,
                        getOneHandedPerkBorderColor(
                                perk,
                                oneHandedLevel,
                                oneHandedPerkPoints
                        ));

                if (
                        mouseX >= perkX
                                && mouseX <= perkX + PERK_NODE_SIZE
                                && mouseY >= perkY
                                && mouseY <= perkY + PERK_NODE_SIZE
                ) {
                    graphics.renderTooltip(
                            font,
                            List.of(
                                    Component.literal(perk.getName()).getVisualOrderText(),
                                    Component.literal("Requires One-Handed Level: " + perk.getRequiredLevel()).getVisualOrderText(),
                                    Component.literal("Cost: " + perk.getPointCost() + " Perk Points").getVisualOrderText(),
                                    Component.literal(perk.getDescription()).getVisualOrderText(),
                                    Component.literal(perk.getEffectText()).getVisualOrderText()
                            ),
                            mouseX,
                            mouseY
                    );
                }
            }
        }

        drawPrestigeControls(graphics);

        if (confirmingPrestige) {
            drawPrestigeConfirmation(graphics);
        }

        width = actualWidth;
        height = actualHeight;
        graphics.pose().popPose();
    }

    private void updateUiTransform() {

        uiScale = Math.min(
                1.0f,
                Math.min(
                        width / (float) DESIGN_WIDTH,
                        height / (float) DESIGN_HEIGHT
                )
        );
        uiOffsetX = (width - DESIGN_WIDTH * uiScale) / 2.0f;
        uiOffsetY = (height - DESIGN_HEIGHT * uiScale) / 2.0f;
    }

    private void drawPrestigeControls(GuiGraphics graphics) {

        int prestige = getSelectedPrestige();
        int rankColor = getPrestigeColor(prestige);

        graphics.drawString(
                font,
                "Prestige: " + getPrestigeRank(prestige),
                BOTTOM_INFO_X,
                height - 90,
                rankColor
        );
        graphics.drawString(
                font,
                "Perk Effectiveness: +" + prestige + "%",
                BOTTOM_INFO_X,
                height - 76,
                0xFFDDDDDD
        );

        if (getSelectedSkillLevel() < 150) {
            return;
        }

        graphics.fill(
                PRESTIGE_BUTTON_X,
                PRESTIGE_BUTTON_Y,
                PRESTIGE_BUTTON_X + PRESTIGE_BUTTON_WIDTH,
                PRESTIGE_BUTTON_Y + PRESTIGE_BUTTON_HEIGHT,
                0xFF6A4A10
        );
        graphics.renderOutline(
                PRESTIGE_BUTTON_X,
                PRESTIGE_BUTTON_Y,
                PRESTIGE_BUTTON_WIDTH,
                PRESTIGE_BUTTON_HEIGHT,
                0xFFFFD700
        );
        graphics.drawCenteredString(
                font,
                "Prestige Skill",
                PRESTIGE_BUTTON_X + PRESTIGE_BUTTON_WIDTH / 2,
                PRESTIGE_BUTTON_Y + 6,
                0xFFFFD700
        );
    }

    private void drawPrestigeConfirmation(GuiGraphics graphics) {

        graphics.fill(0, 0, width, height, 0x99000000);
        graphics.fill(
                PRESTIGE_DIALOG_X,
                PRESTIGE_DIALOG_Y,
                PRESTIGE_DIALOG_X + PRESTIGE_DIALOG_WIDTH,
                PRESTIGE_DIALOG_Y + PRESTIGE_DIALOG_HEIGHT,
                0xFF151515
        );
        graphics.renderOutline(
                PRESTIGE_DIALOG_X,
                PRESTIGE_DIALOG_Y,
                PRESTIGE_DIALOG_WIDTH,
                PRESTIGE_DIALOG_HEIGHT,
                0xFFFFD700
        );

        int x = PRESTIGE_DIALOG_X + 20;
        int y = PRESTIGE_DIALOG_Y + 18;
        graphics.drawString(font, "Prestige " + getSelectedSkillType().getDisplayName() + "?", x, y, 0xFFFFD700);
        graphics.drawString(font, "Prestiging will:", x, y + 24, 0xFFFFFFFF);
        graphics.drawString(font, "- Reset this skill to Level 1", x, y + 40, 0xFFDD7777);
        graphics.drawString(font, "- Reset XP to 0", x, y + 54, 0xFFDD7777);
        graphics.drawString(font, "- Reset unlocked perks", x, y + 68, 0xFFDD7777);
        graphics.drawString(font, "You will KEEP:", x, y + 92, 0xFFFFFFFF);
        graphics.drawString(font, "- All earned perk points", x, y + 108, 0xFF77DD77);
        graphics.drawString(font, "- Skill toggles/settings", x, y + 122, 0xFF77DD77);
        graphics.drawString(font, "- Your prestige rank", x, y + 136, 0xFF77DD77);
        graphics.drawString(font, "Each prestige adds +1% perk effectiveness.", x, y + 158, 0xFFDDDDDD);
        graphics.drawString(font, "Current Rank: " + getPrestigeRank(getSelectedPrestige()), x, y + 178, getPrestigeColor(getSelectedPrestige()));
        graphics.drawString(font, "Current Bonus: +" + getSelectedPrestige() + "%", x, y + 192, 0xFFFFFFFF);

        drawDialogButton(graphics, 390, 335, "Confirm", 0xFF6A4A10);
        drawDialogButton(graphics, 490, 335, "Cancel", 0xFF333333);
    }

    private void drawDialogButton(
            GuiGraphics graphics,
            int x,
            int y,
            String label,
            int fill
    ) {
        graphics.fill(x, y, x + 80, y + 22, fill);
        graphics.renderOutline(x, y, 80, 22, 0xFFAAAAAA);
        graphics.drawCenteredString(font, label, x + 40, y + 7, 0xFFFFFFFF);
    }

    private SkillType getSelectedSkillType() {
        return SkillType.values()[selectedSkill];
    }

    private int getSelectedSkillLevel() {
        UUID playerId = minecraft.player.getUUID();
        return switch (getSelectedSkillType()) {
            case MINING -> ClientSkillState.getMiningLevel(playerId);
            case WOODCUTTING -> ClientSkillState.getWoodcuttingLevel(playerId);
            case FARMING -> ClientSkillState.getFarmingLevel(playerId);
            case ONE_HANDED -> ClientSkillState.getOneHandedLevel(playerId);
        };
    }

    private int getSelectedPrestige() {
        UUID playerId = minecraft.player.getUUID();
        return switch (getSelectedSkillType()) {
            case MINING -> ClientSkillState.getMiningPrestige(playerId);
            case WOODCUTTING -> ClientSkillState.getWoodcuttingPrestige(playerId);
            case FARMING -> ClientSkillState.getFarmingPrestige(playerId);
            case ONE_HANDED -> ClientSkillState.getOneHandedPrestige(playerId);
        };
    }

    private String getPrestigeRank(int prestige) {
        if (prestige <= 0) {
            return "None";
        }

        String tier = prestige <= 3 ? "Bronze" : prestige <= 6 ? "Silver" : "Gold";
        int stars = prestige <= 9 ? ((prestige - 1) % 3) + 1 : 3;
        return tier + " " + "★".repeat(stars)
                + (prestige > 9 ? " +" + (prestige - 9) : "");
    }

    private int getPrestigeColor(int prestige) {
        if (prestige <= 0) {
            return 0xFFAAAAAA;
        }
        if (prestige <= 3) {
            return 0xFFCD7F32;
        }
        if (prestige <= 6) {
            return 0xFFC0C0C0;
        }
        return 0xFFFFD700;
    }

    private boolean isInside(
            double mouseX,
            double mouseY,
            int x,
            int y,
            int width,
            int height
    ) {
        return mouseX >= x && mouseX <= x + width
                && mouseY >= y && mouseY <= y + height;
    }

    private int getMiningSpeedBonusPercent(UUID playerId) {

        int hasteLevel = 0;

        if (
                ClientSkillState.hasMiningPerk(
                        playerId,
                        "stonecutter"
                )
        ) {

            hasteLevel = 1;
        }

        if (
                ClientSkillState.hasMiningPerk(
                        playerId,
                        "miners_momentum"
                )
        ) {

            int streak =
                    ClientSkillState.getMiningStreak(
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
                ClientSkillState.hasMiningPerk(
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
                ClientSkillState.hasMiningPerk(
                        playerId,
                        "nearly_indestructible"
                )
        ) {

            return 75;
        }

        if (
                ClientSkillState.hasMiningPerk(
                        playerId,
                        "tempered_tools"
                )
        ) {

            return 40;
        }

        if (
                ClientSkillState.hasMiningPerk(
                        playerId,
                        "reinforced_grip"
                )
        ) {

            return 25;
        }

        if (
                ClientSkillState.hasMiningPerk(
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
                ClientSkillState.hasMiningPerk(
                        playerId,
                        "it_smells_4"
                )
        ) {

            return 4;
        }

        if (
                ClientSkillState.hasMiningPerk(
                        playerId,
                        "it_smells_3"
                )
        ) {

            return 3;
        }

        if (
                ClientSkillState.hasMiningPerk(
                        playerId,
                        "it_smells_2"
                )
        ) {

            return 2;
        }

        if (
                ClientSkillState.hasMiningPerk(
                        playerId,
                        "they_have_a_scent"
                )
        ) {

            return 1;
        }

        return 0;
    }

    private boolean hasCaveVision(UUID playerId) {

        return ClientSkillState.hasMiningPerk(
                playerId,
                "deep_delver"
        );
    }

    private int getWoodcuttingChopSpeedBonusPercent(UUID playerId) {

        if (
                ClientSkillState.hasWoodcuttingPerk(
                        playerId,
                        "rhythm_of_the_forest"
                )
        ) {

            return 50;
        }

        if (
                ClientSkillState.hasWoodcuttingPerk(
                        playerId,
                        "felling_momentum"
                )
        ) {

            return 35;
        }

        if (
                ClientSkillState.hasWoodcuttingPerk(
                        playerId,
                        "clean_swing"
                )
        ) {

            return 25;
        }

        if (
                ClientSkillState.hasWoodcuttingPerk(
                        playerId,
                        "lumberjacks_stance"
                )
        ) {

            return 15;
        }

        if (
                ClientSkillState.hasWoodcuttingPerk(
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
                ClientSkillState.hasWoodcuttingPerk(
                        playerId,
                        "twigs_everywhere"
                )
        ) {

            bonus++;
        }

        if (
                ClientSkillState.hasWoodcuttingPerk(
                        playerId,
                        "green_thumb"
                )
        ) {

            bonus++;
        }

        if (
                ClientSkillState.hasWoodcuttingPerk(
                        playerId,
                        "apple_picker"
                )
        ) {

            bonus++;
        }

        if (
                ClientSkillState.hasWoodcuttingPerk(
                        playerId,
                        "natures_gift"
                )
        ) {

            bonus++;
        }

        if (
                ClientSkillState.hasWoodcuttingPerk(
                        playerId,
                        "fast_decay"
                )
        ) {

            bonus++;
        }

        if (
                ClientSkillState.hasWoodcuttingPerk(
                        playerId,
                        "autumn_winds"
                )
        ) {

            bonus++;
        }

        if (
                ClientSkillState.hasWoodcuttingPerk(
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
                ClientSkillState.hasWoodcuttingPerk(
                        playerId,
                        "veteran_woodsman"
                )
        ) {

            return 75;
        }

        if (
                ClientSkillState.hasWoodcuttingPerk(
                        playerId,
                        "seasoned_haft"
                )
        ) {

            return 50;
        }

        if (
                ClientSkillState.hasWoodcuttingPerk(
                        playerId,
                        "callused_hands"
                )
        ) {

            return 35;
        }

        if (
                ClientSkillState.hasWoodcuttingPerk(
                        playerId,
                        "reinforced_haft"
                )
        ) {

            return 20;
        }

        if (
                ClientSkillState.hasWoodcuttingPerk(
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
                ClientSkillState.hasWoodcuttingPerk(
                        playerId,
                        "natures_gift"
                )
        ) {

            return 4;
        }

        if (
                ClientSkillState.hasWoodcuttingPerk(
                        playerId,
                        "apple_picker"
                )
        ) {

            return 3;
        }

        if (
                ClientSkillState.hasWoodcuttingPerk(
                        playerId,
                        "green_thumb"
                )
        ) {

            return 2;
        }

        if (
                ClientSkillState.hasWoodcuttingPerk(
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
                    ClientSkillState.hasWoodcuttingPerk(
                            playerId,
                            perkId
                    )
            ) {

                bonus++;
            }
        }

        return bonus;
    }

    private int getFarmingGrowthRatePercent(
            UUID playerId
    ) {

        return ClientSkillState.getFarmingGrowthBonusPercent(
                playerId
        );
    }

    private int getFarmingYieldBonusPercent(
            UUID playerId
    ) {

        return ClientSkillState.hasFarmingPerk(
                playerId,
                "better_yields"
        )
                ? 10
                : 0;
    }

    private int getFarmingAnimalCarePercent(
            UUID playerId
    ) {

        return ClientSkillState
                .getFarmingAnimalGrowthBonusPercent(
                        playerId
                );
    }

    private int getConnectionColor(
            SkillPerk parent,
            SkillPerk perk,
            int miningLevel,
            int miningPerkPoints
    ) {

        if (isConnectionUnlocked(SkillType.MINING, parent, perk)) {

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
                && miningPerkPoints >= perk.getPointCost()
                && isParentUnlocked(perk);
    }

    private boolean isPerkUnlocked(SkillPerk perk) {

        return ClientSkillState.hasMiningPerk(
                minecraft.player.getUUID(),
                perk.getId()
        );
    }

    private boolean isParentUnlocked(SkillPerk perk) {

        if (perk.getParentId() == null) {

            return true;
        }

        return ClientSkillState.hasMiningPerk(
                minecraft.player.getUUID(),
                perk.getParentId()
        );
    }

    private int getWoodcuttingConnectionColor(
            SkillPerk parent,
            SkillPerk perk,
            int woodcuttingLevel,
            int woodcuttingPerkPoints
    ) {

        if (isConnectionUnlocked(SkillType.WOODCUTTING, parent, perk)) {

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
            SkillType skillType,
            SkillPerk parent,
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
                    skillType,
                    parent,
                    perk,
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
                skillType,
                parent,
                perk,
                x1,
                y1,
                x1,
                middleY,
                color
        );

        drawWoodcuttingConnectionSegment(
                graphics,
                skillType,
                parent,
                perk,
                x1,
                middleY,
                x2,
                middleY,
                color
        );

        drawWoodcuttingConnectionSegment(
                graphics,
                skillType,
                parent,
                perk,
                x2,
                middleY,
                x2,
                y2,
                color
        );
    }

    private void drawWoodcuttingConnectionSegment(
            GuiGraphics graphics,
            SkillType skillType,
            SkillPerk parent,
            SkillPerk child,
            int x1,
            int y1,
            int x2,
            int y2,
            int color
    ) {

        int segmentColor = resolveConnectionSegmentColor(
                skillType,
                parent,
                child,
                color
        );

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
                    segmentColor
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
                && woodcuttingPerkPoints >= perk.getPointCost()
                && isWoodcuttingParentUnlocked(perk);
    }

    private boolean isWoodcuttingPerkUnlocked(SkillPerk perk) {

        return ClientSkillState.hasWoodcuttingPerk(
                minecraft.player.getUUID(),
                perk.getId()
        );
    }

    private boolean isWoodcuttingParentUnlocked(SkillPerk perk) {

        if (perk.getParentId() == null) {

            return true;
        }

        return ClientSkillState.hasWoodcuttingPerk(
                minecraft.player.getUUID(),
                perk.getParentId()
        );
    }

    private int getFarmingConnectionColor(
            SkillPerk parent,
            SkillPerk perk,
            int farmingLevel,
            int farmingPerkPoints
    ) {

        if (isConnectionUnlocked(SkillType.FARMING, parent, perk)) {

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

    private int resolveConnectionSegmentColor(
            SkillType skillType,
            SkillPerk parent,
            SkillPerk child,
            int fallbackColor
    ) {
        return isConnectionUnlocked(skillType, parent, child)
                ? PERK_UNLOCKED_COLOR
                : fallbackColor;
    }

    private boolean isConnectionUnlocked(
            SkillType skillType,
            SkillPerk parent,
            SkillPerk child
    ) {
        UUID playerId = minecraft.player.getUUID();

        return switch (skillType) {
            case MINING -> ClientSkillState.hasMiningPerk(
                    playerId,
                    parent.getId()
            ) && ClientSkillState.hasMiningPerk(playerId, child.getId());
            case WOODCUTTING -> ClientSkillState.hasWoodcuttingPerk(
                    playerId,
                    parent.getId()
            ) && ClientSkillState.hasWoodcuttingPerk(playerId, child.getId());
            case FARMING -> ClientSkillState.hasFarmingPerk(
                    playerId,
                    parent.getId()
            ) && ClientSkillState.hasFarmingPerk(playerId, child.getId());
            case ONE_HANDED -> ClientSkillState.hasOneHandedPerk(
                    playerId,
                    parent.getId()
            ) && ClientSkillState.hasOneHandedPerk(playerId, child.getId());
        };
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
                && farmingPerkPoints >= perk.getPointCost()
                && isFarmingParentUnlocked(perk);
    }

    private boolean isFarmingPerkUnlocked(
            SkillPerk perk
    ) {

        return ClientSkillState.hasFarmingPerk(
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

        return ClientSkillState.hasFarmingPerk(
                minecraft.player.getUUID(),
                perk.getParentId()
        );
    }

    private int getOneHandedConnectionColor(
            SkillPerk parent,
            SkillPerk perk,
            int level,
            int perkPoints
    ) {
        if (isConnectionUnlocked(SkillType.ONE_HANDED, parent, perk)) {
            return PERK_UNLOCKED_COLOR;
        }
        if (isOneHandedPerkAvailable(perk, level, perkPoints)) {
            return PERK_AVAILABLE_BORDER_COLOR;
        }
        return PERK_LOCKED_BORDER_COLOR;
    }

    private int getOneHandedPerkFillColor(
            SkillPerk perk,
            int level,
            int perkPoints
    ) {
        if (isOneHandedPerkUnlocked(perk)) {
            return PERK_UNLOCKED_COLOR;
        }
        return isOneHandedPerkAvailable(perk, level, perkPoints)
                ? PERK_AVAILABLE_FILL_COLOR
                : PERK_LOCKED_FILL_COLOR;
    }

    private int getOneHandedPerkBorderColor(
            SkillPerk perk,
            int level,
            int perkPoints
    ) {
        if (isOneHandedPerkUnlocked(perk)) {
            return PERK_UNLOCKED_COLOR;
        }
        return isOneHandedPerkAvailable(perk, level, perkPoints)
                ? PERK_AVAILABLE_BORDER_COLOR
                : PERK_LOCKED_BORDER_COLOR;
    }

    private boolean isOneHandedPerkAvailable(
            SkillPerk perk,
            int level,
            int perkPoints
    ) {
        return !isOneHandedPerkUnlocked(perk)
                && level >= perk.getRequiredLevel()
                && perkPoints >= perk.getPointCost()
                && isOneHandedParentUnlocked(perk);
    }

    private boolean isOneHandedPerkUnlocked(SkillPerk perk) {
        return ClientSkillState.hasOneHandedPerk(
                minecraft.player.getUUID(),
                perk.getId()
        );
    }

    private boolean isOneHandedParentUnlocked(SkillPerk perk) {
        return perk.getParentId() == null
                || ClientSkillState.hasOneHandedPerk(
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
                ClientSkillState.getSelectedOreSense(
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
            double mouseY,
            int toggleRows
    ) {

        if (
                mouseX < WOODCUTTING_TOGGLE_X
                        || mouseX > WOODCUTTING_TOGGLE_X
                        + WOODCUTTING_TOGGLE_WIDTH
        ) {

            return -1;
        }

        for (int row = 0; row < toggleRows; row++) {

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

    private List<FeatureToggle> getVisibleWoodcuttingToggles(
            UUID playerId
    ) {

        List<FeatureToggle> toggles = new ArrayList<>();

        if (hasAnyWoodcuttingPerk(playerId, "fast_decay", "autumn_winds")) {
            toggles.add(new FeatureToggle(
                    "leaf_decay",
                    "Leaf Decay",
                    ClientSkillState.isWoodcuttingLeafDecayEnabled(playerId)
            ));
        }

        if (hasAnyWoodcuttingPerk(playerId, "master_arborist")) {
            toggles.add(new FeatureToggle(
                    "whole_tree",
                    "Whole Tree",
                    ClientSkillState.isWoodcuttingWholeTreeEnabled(playerId)
            ));
        }

        if (hasAnyWoodcuttingPerk(
                playerId,
                "twigs_everywhere",
                "green_thumb",
                "apple_picker",
                "natures_gift",
                "friction_fire"
        )) {
            toggles.add(new FeatureToggle(
                    "bonus_drops",
                    "Bonus Drops",
                    ClientSkillState.isWoodcuttingBonusDropsEnabled(playerId)
            ));
        }

        if (hasAnyWoodcuttingPerk(playerId, "clean_forest_floor")) {
            toggles.add(new FeatureToggle(
                    "clean_floor",
                    "Clean Floor",
                    ClientSkillState.isWoodcuttingCleanFloorEnabled(playerId)
            ));
        }

        if (hasAnyWoodcuttingPerk(playerId, "decapitation_chance")) {
            toggles.add(new FeatureToggle(
                    "decapitation",
                    "Decapitation",
                    ClientSkillState.isWoodcuttingDecapitationEnabled(playerId)
            ));
        }

        return toggles;
    }

    private List<FeatureToggle> getVisibleFarmingToggles(
            UUID playerId
    ) {

        List<FeatureToggle> toggles = new ArrayList<>();

        if (hasAnyFarmingPerk(playerId, "better_yields", "bountiful_harvest")) {
            toggles.add(new FeatureToggle(
                    "bonus_harvests",
                    "Bonus Harvests",
                    ClientSkillState.isFarmingBonusHarvestsEnabled(playerId)
            ));
        }

        if (hasAnyFarmingPerk(playerId, "auto_replant")) {
            toggles.add(new FeatureToggle(
                    "auto_replant",
                    "Auto Replant",
                    ClientSkillState.isFarmingAutoReplantEnabled(playerId)
            ));
        }

        if (hasAnyFarmingPerk(playerId, "herd_instinct")) {
            toggles.add(new FeatureToggle(
                    "animal_follow",
                    "Animal Follow",
                    ClientSkillState.isFarmingAnimalFollowEnabled(playerId)
            ));
        }

        if (hasAnyFarmingPerk(
                playerId,
                "healthy_stock",
                "prime_cuts",
                "efficient_rancher",
                "bountiful_herds"
        )) {
            toggles.add(new FeatureToggle(
                    "animal_drops",
                    "Animal Drops",
                    ClientSkillState.isFarmingAnimalDropsEnabled(playerId)
            ));
        }

        if (hasAnyFarmingPerk(
                playerId,
                "mushroom_expert",
                "berry_harvester",
                "bountiful_harvest"
        )) {
            toggles.add(new FeatureToggle(
                    "gathering_bonus_drops",
                    "Gathering Drops",
                    ClientSkillState
                            .isFarmingGatheringBonusDropsEnabled(playerId)
            ));
        }

        if (hasAnyFarmingPerk(
                playerId,
                "busy_bees",
                "pollination_expert",
                "honey_gatherer",
                "honey_mastery",
                "master_beekeeper"
        )) {
            toggles.add(new FeatureToggle(
                    "beekeeping",
                    "Beekeeping",
                    ClientSkillState.isFarmingBeekeepingEnabled(playerId)
            ));
        }

        if (hasAnyFarmingPerk(
                playerId,
                "animal_faster_growth",
                "experienced_breeder",
                "extra_wool",
                "healthy_flocks",
                "herd_instinct",
                "shepherds_touch",
                "shepherds_call"
        )) {
            toggles.add(new FeatureToggle(
                    "animal_overlay",
                    "Animal Overlay",
                    ClientSkillState.isFarmingAnimalOverlayEnabled(playerId)
            ));
        }

        return toggles;
    }

    private boolean hasAnyWoodcuttingPerk(
            UUID playerId,
            String... perkIds
    ) {

        for (String perkId : perkIds) {
            if (ClientSkillState.hasWoodcuttingPerk(playerId, perkId)) {
                return true;
            }
        }

        return false;
    }

    private boolean hasAnyFarmingPerk(
            UUID playerId,
            String... perkIds
    ) {

        for (String perkId : perkIds) {
            if (ClientSkillState.hasFarmingPerk(playerId, perkId)) {
                return true;
            }
        }

        return false;
    }

    private List<FeatureToggle> getVisibleOneHandedToggles(
            UUID playerId
    ) {
        List<FeatureToggle> toggles = new ArrayList<>();

        if (ClientSkillState.hasOneHandedPerk(playerId, "offhand_strike")) {
            toggles.add(new FeatureToggle(
                    "dual_wield",
                    "Dual Wield",
                    ClientSkillState.isOneHandedToggleEnabled("dual_wield")
            ));
        }
        if (ClientSkillState.hasOneHandedPerk(playerId, "parry")) {
            toggles.add(new FeatureToggle(
                    "parry",
                    "Parry",
                    ClientSkillState.isOneHandedToggleEnabled("parry")
            ));
        }
        if (ClientSkillState.hasOneHandedPerk(playerId, "shield_training")) {
            toggles.add(new FeatureToggle(
                    "shield_effects",
                    "Shield Effects",
                    ClientSkillState.isOneHandedToggleEnabled("shield_effects")
            ));
        }
        if (ClientSkillState.hasOneHandedPerk(playerId, "monster_hunter")) {
            toggles.add(new FeatureToggle(
                    "bonus_loot",
                    "Bonus Loot",
                    ClientSkillState.isOneHandedToggleEnabled("bonus_loot")
            ));
        }

        return toggles;
    }

    private void toggleWoodcuttingFeature(
            UUID playerId,
            String toggleId
    ) {

        switch (toggleId) {
            case "leaf_decay" ->
                    ClientSkillState.toggleWoodcuttingLeafDecay(playerId);
            case "whole_tree" ->
                    ClientSkillState.toggleWoodcuttingWholeTree(playerId);
            case "bonus_drops" ->
                    ClientSkillState.toggleWoodcuttingBonusDrops(playerId);
            case "clean_floor" ->
                    ClientSkillState.toggleWoodcuttingCleanFloor(playerId);
            case "decapitation" ->
                    ClientSkillState.toggleWoodcuttingDecapitation(playerId);
        }
    }

    private void toggleFarmingFeature(
            UUID playerId,
            String toggleId
    ) {

        switch (toggleId) {
            case "bonus_harvests" ->
                    ClientSkillState.toggleFarmingBonusHarvests(playerId);
            case "auto_replant" ->
                    ClientSkillState.toggleFarmingAutoReplant(playerId);
            case "animal_follow" ->
                    ClientSkillState.toggleFarmingAnimalFollow(playerId);
            case "animal_drops" ->
                    ClientSkillState.toggleFarmingAnimalDrops(playerId);
            case "gathering_bonus_drops" ->
                    ClientSkillState.toggleFarmingGatheringBonusDrops(playerId);
            case "beekeeping" ->
                    ClientSkillState.toggleFarmingBeekeeping(playerId);
            case "animal_overlay" ->
                    ClientSkillState.toggleFarmingAnimalOverlay(playerId);
        }
    }

    private void drawFeatureToggles(
            GuiGraphics graphics,
            List<FeatureToggle> toggles
    ) {

        for (int row = 0; row < toggles.size(); row++) {

            FeatureToggle toggle = toggles.get(row);

            drawWoodcuttingFeatureToggle(
                    graphics,
                    toggle.label(),
                    toggle.enabled(),
                    WOODCUTTING_TOGGLE_START_Y
                            + row * WOODCUTTING_TOGGLE_ROW_STEP
            );
        }
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

    private record FeatureToggle(
            String id,
            String label,
            boolean enabled
    ) {
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
