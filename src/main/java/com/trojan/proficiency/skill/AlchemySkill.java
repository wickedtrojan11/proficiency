package com.trojan.proficiency.skill;

public final class AlchemySkill {

    private AlchemySkill() {
    }

    public static int getXpRequired(int level) {
        return 10 * level;
    }
}
