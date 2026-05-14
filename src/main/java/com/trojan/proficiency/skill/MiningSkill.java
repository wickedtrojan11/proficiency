package com.trojan.proficiency.skill;

public class MiningSkill {

    public static final String NAME = "Mining";

    public static final int COLOR = 0xFFD700;

    // =========================
    // PERKS
    // =========================

    public static final String STONECUTTER_NAME =
            "Stonecutter";

    public static final String STONECUTTER_DESCRIPTION =
            "Mine stone slightly faster.";

    public static final int STONECUTTER_LEVEL = 5;

    public static int getXpRequired(int level) {

        return level * 10;
    }
}