package com.trojan.proficiency.skill;

public final class OneHandedSkill {

    private OneHandedSkill() {
    }

    public static int getXpRequired(int level) {
        return level * 10;
    }
}
