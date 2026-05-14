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