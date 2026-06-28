package com.trojan.proficiency.perk;

public class SkillPerk {
    private final String parentId;
    private final String id;
    private final String effectText;
    private final String name;

    private final String description;

    private final int requiredLevel;

    private final int x;

    private final int y;

    public SkillPerk(
            String id,
            String name,
            String description,
            String effectText,
            int requiredLevel,
            int x,
            int y,
            String parentId
    ) {
        this.effectText = effectText;
        this.id = id;

        this.name = name;

        this.description = description;

        this.requiredLevel = requiredLevel;

        this.x = x;

        this.y = y;

        this.parentId = parentId;
    }
    public String getEffectText() {

        return effectText;
    }
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public int getPointCost() {

        return getPointCostForLevel(requiredLevel);
    }

    public static int getPointCostForLevel(
            int requiredLevel
    ) {

        if (requiredLevel <= 5) {
            return 1;
        }

        if (requiredLevel <= 10) {
            return 2;
        }

        if (requiredLevel <= 15) {
            return 4;
        }

        if (requiredLevel <= 20) {
            return 8;
        }

        if (requiredLevel <= 25) {
            return 16;
        }

        if (requiredLevel <= 30) {
            return 32;
        }

        if (requiredLevel <= 35) {
            return 64;
        }

        return 128;
    }

    public int getX() {
        return x;
    }
    public String getParentId() {
        return parentId;
    }
    public int getY() {
        return y;
    }
}
