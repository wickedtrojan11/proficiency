package com.trojan.proficiency;

import java.util.HashMap;
import java.util.UUID;

public class SkillManager {

    // Mining
    private static final HashMap<UUID, Integer> miningXp = new HashMap<>();
    private static final HashMap<UUID, Integer> miningLevel = new HashMap<>();

    // Woodcutting
    private static final HashMap<UUID, Integer> woodcuttingXp = new HashMap<>();
    private static final HashMap<UUID, Integer> woodcuttingLevel = new HashMap<>();

    // =========================
    // MINING
    // =========================

    public static boolean addMiningXp(UUID playerId, int amount) {

        int currentXp = miningXp.getOrDefault(playerId, 0);
        int currentLevel = miningLevel.getOrDefault(playerId, 1);

        currentXp += amount;

        int xpRequired = currentLevel * 10;

        boolean leveledUp = false;

        if (currentXp >= xpRequired) {

            currentXp = 0;
            currentLevel++;

            miningLevel.put(playerId, currentLevel);

            leveledUp = true;
        }

        miningXp.put(playerId, currentXp);

        return leveledUp;
    }

    public static int getMiningXp(UUID playerId) {

        return miningXp.getOrDefault(playerId, 0);
    }

    public static int getMiningLevel(UUID playerId) {

        return miningLevel.getOrDefault(playerId, 1);
    }

    // =========================
    // WOODCUTTING
    // =========================

    public static boolean addWoodcuttingXp(UUID playerId, int amount) {

        int currentXp = woodcuttingXp.getOrDefault(playerId, 0);
        int currentLevel = woodcuttingLevel.getOrDefault(playerId, 1);

        currentXp += amount;

        int xpRequired = currentLevel * 10;

        boolean leveledUp = false;

        if (currentXp >= xpRequired) {

            currentXp = 0;
            currentLevel++;

            woodcuttingLevel.put(playerId, currentLevel);

            leveledUp = true;
        }

        woodcuttingXp.put(playerId, currentXp);

        return leveledUp;
    }

    public static int getWoodcuttingXp(UUID playerId) {

        return woodcuttingXp.getOrDefault(playerId, 0);
    }

    public static int getWoodcuttingLevel(UUID playerId) {

        return woodcuttingLevel.getOrDefault(playerId, 1);
    }
}