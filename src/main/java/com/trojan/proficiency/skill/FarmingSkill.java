package com.trojan.proficiency.skill;

public class FarmingSkill {

    public static final String NAME = "Farming";

    public static final int COLOR = 0xFFFFCC55;

    public static int getXpRequired(int level) {

        return level * 10;
    }
}
