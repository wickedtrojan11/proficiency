package com.trojan.proficiency.skill;

public class WoodcuttingSkill {

    public static final String NAME = "Woodcutting";

    public static final int COLOR = 0x55FF55;

    public static int getXpRequired(int level) {

        return level * 10;
    }
}