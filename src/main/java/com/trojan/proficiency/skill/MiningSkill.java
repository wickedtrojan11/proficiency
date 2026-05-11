package com.trojan.proficiency.skill;

public class MiningSkill {

    public static final String NAME = "Mining";

    public static final int COLOR = 0xFFD700;

    public static int getXpRequired(int level) {

        return level * 10;
    }
}