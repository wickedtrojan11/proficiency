package com.trojan.proficiency.skill;

public enum SkillType {
    MINING("mining", "Mining"),
    WOODCUTTING("woodcutting", "Woodcutting"),
    FARMING("farming", "Farming"),
    ONE_HANDED("one_handed", "One-Handed"),
    ALCHEMY("alchemy", "Alchemy");

    private final String id;
    private final String displayName;

    SkillType(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static SkillType fromId(String id) {

        for (SkillType skillType : values()) {

            if (skillType.id.equals(id)) {
                return skillType;
            }
        }

        return null;
    }
}
