package com.trojan.proficiency.skill;

public enum SkillType {
    MINING("Mining"),
    WOODCUTTING("Woodcutting"),
    FARMING("Farming");

    private final String displayName;

    SkillType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
