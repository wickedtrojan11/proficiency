package com.trojan.proficiency;

import java.util.HashMap;
import java.util.UUID;

public class SkillManager {

    private static final HashMap<UUID, Integer> miningXp = new HashMap<>();
    private static final HashMap<UUID, Integer> miningLevel = new HashMap<>();

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
}