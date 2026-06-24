package com.trojan.proficiency;
import com.trojan.proficiency.perk.MiningPerks;
import com.trojan.proficiency.perk.SkillPerk;
import java.util.Set;
import net.minecraft.network.chat.Component;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayer;

import net.minecraft.network.chat.Component;

import com.trojan.proficiency.perk.MiningPerks;
import com.trojan.proficiency.perk.SkillPerk;
import com.trojan.proficiency.player.PlayerData;
import com.trojan.proficiency.save.PlayerDataStorage;
import com.trojan.proficiency.skill.MiningSkill;
import com.trojan.proficiency.skill.WoodcuttingSkill;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.HashMap;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
public class SkillManager {
    private static final HashMap<UUID, PlayerData>
            playerDataMap = new HashMap<>();
    private static final HashMap<UUID, Integer>
            miningStreaks = new HashMap<>();
    private static final HashMap<UUID, Integer>
            wellRestedRemainingTicks = new HashMap<>();

    private static final int WELL_RESTED_DURATION_TICKS =
            10 * 60 * 20;
    private static final int WELL_RESTED_XP_MULTIPLIER = 2;

    public static void clearPlayerDataCache() {

        playerDataMap.clear();
        miningStreaks.clear();
        wellRestedRemainingTicks.clear();
    }

    public static void loadPlayerData(
            UUID playerId
    ) {

        PlayerData loadedData =
                PlayerDataStorage.loadPlayer(
                        playerId
                );

        playerDataMap.put(
                playerId,
                loadedData
        );
    }

    public static void saveAllPlayerData() {

        for (
                UUID playerId
                        : new HashSet<>(
                                playerDataMap.keySet()
                        )
        ) {

            PlayerDataStorage.savePlayer(
                    playerId,
                    playerDataMap.get(playerId),
                    "server stopping"
            );
        }
    }

    public static void saveLoadedPlayerData(
            UUID playerId,
            String reason
    ) {

        if (!playerDataMap.containsKey(playerId)) {

            ProficiencyMod.LOGGER.warn(
                    "Skipping proficiency save for {} because no data is loaded ({})",
                    playerId,
                    reason
            );
            return;
        }

        PlayerDataStorage.savePlayer(
                playerId,
                playerDataMap.get(playerId),
                reason
        );
    }

    public static void unloadPlayerData(
            UUID playerId
    ) {

        playerDataMap.remove(playerId);
        miningStreaks.remove(playerId);
        wellRestedRemainingTicks.remove(playerId);
    }

    private static PlayerData getPlayerData(
            UUID playerId
    )
    {

        if (!playerDataMap.containsKey(playerId)) {

            PlayerData loadedData =
                    PlayerDataStorage.loadPlayer(
                            playerId
                    );

            playerDataMap.put(
                    playerId,
                    loadedData
            );
        }

        return playerDataMap.get(playerId);
    }

    // =========================
    // SAVE / LOAD
    // =========================

    public static void savePlayerData(
            UUID playerId
    ) {

        savePlayerData(
                playerId,
                "regular save"
        );
    }

    public static void savePlayerData(
            UUID playerId,
            String reason
    ) {

        PlayerData data =
                getPlayerData(playerId);

        PlayerDataStorage.savePlayer(
                playerId,
                data,
                reason
        );
    }

    // =========================
    // MINING
    // =========================
    public static int getMiningStreak(
            UUID playerId
    ) {

        return miningStreaks.getOrDefault(
                playerId,
                0
        );
    }

    public static void increaseMiningStreak(
            UUID playerId
    ) {

        int streak =
                getMiningStreak(playerId);

        miningStreaks.put(
                playerId,
                streak + 1
        );
    }

    public static void resetMiningStreak(
            UUID playerId
    ) {

        miningStreaks.put(
                playerId,
                0
        );
    }
    public static boolean addMiningXp(ServerPlayer player, int amount) {

        UUID playerId = player.getUUID();
        amount =
                applySkillXpMultiplier(
                        playerId,
                        amount
                );

        PlayerData data =
                getPlayerData(playerId);

        int currentXp =
                data.getMiningXp();

        int currentLevel =
                data.getMiningLevel();

        currentXp += amount;

        int xpRequired =
                MiningSkill.getXpRequired(
                        currentLevel
                );

        boolean leveledUp = false;

        if (currentXp >= xpRequired) {

            currentXp = 0;

            currentLevel++;

            data.setMiningLevel(currentLevel);
            checkForNewPerks(
                    player,
                    data.getMiningLevel()
            );
            int currentPerkPoints =
                    data.getMiningPerkPoints();

            data.setMiningPerkPoints(
                    currentPerkPoints + 1
            );

            leveledUp = true;
        }

        data.setMiningXp(currentXp);

        savePlayerData(playerId);

        return leveledUp;
    }
    private static void checkForNewPerks(
            ServerPlayer player,
            int level
    ) {

        for (SkillPerk perk : MiningPerks.ALL_PERKS) {

            if (level == perk.getRequiredLevel()) {

                player.sendSystemMessage(
                        Component.literal(
                                "§aNEW PERK AVAILABLE: "
                                        + perk.getName()
                        )

                );
                player.level().playSound(
                        null,
                        player.blockPosition(),
                        SoundEvents.ENCHANTMENT_TABLE_USE,
                        SoundSource.PLAYERS,
                        0.5f,
                        1.2f
                );
            }
        }
    }
    public static int getMiningXp(
            UUID playerId
    ) {

        return getPlayerData(playerId)
                .getMiningXp();
    }

    public static int getMiningLevel(
            UUID playerId
    ) {

        return getPlayerData(playerId)
                .getMiningLevel();
    }

    public static int getMiningXpRequired(
            UUID playerId
    ) {

        int level =
                getMiningLevel(playerId);

        return MiningSkill.getXpRequired(
                level
        );
    }

    public static int getMiningPerkPoints(
            UUID playerId
    ) {

        return getPlayerData(playerId)
                .getMiningPerkPoints();
    }

    // =========================
    // GENERIC MINING PERKS
    // =========================

    public static boolean unlockMiningPerk(
            UUID playerId,
            String perkId,
            int requiredLevel
    ) {

        PlayerData data =
                getPlayerData(playerId);

        // Already unlocked
        if (data.hasMiningPerk(perkId)) {
            return false;
        }

        // Not high enough level
        if (data.getMiningLevel() < requiredLevel) {
            return false;
        }

        // No perk points
        if (data.getMiningPerkPoints() <= 0) {
            return false;
        }

        // Unlock perk
        data.unlockMiningPerk(perkId);

        // Spend perk point
        data.setMiningPerkPoints(
                data.getMiningPerkPoints() - 1
        );

        savePlayerData(playerId);

        return true;
    }

    public static boolean hasMiningPerk(
            UUID playerId,
            String perkId
    ) {

        return getPlayerData(playerId)
                .hasMiningPerk(
                        perkId
                );
    }

    // =========================
    // WOODCUTTING
    // =========================

    public static boolean addWoodcuttingXp(
            UUID playerId,
            int amount
    ) {

        amount =
                applySkillXpMultiplier(
                        playerId,
                        amount
                );

        PlayerData data =
                getPlayerData(playerId);

        int currentXp =
                data.getWoodcuttingXp();

        int currentLevel =
                data.getWoodcuttingLevel();

        currentXp += amount;

        int xpRequired =
                WoodcuttingSkill.getXpRequired(
                        currentLevel
                );

        boolean leveledUp = false;

        if (currentXp >= xpRequired) {

            currentXp = 0;

            currentLevel++;

            data.setWoodcuttingLevel(
                    currentLevel
            );

            data.setWoodcuttingPerkPoints(
                    data.getWoodcuttingPerkPoints() + 1
            );

            leveledUp = true;
        }

        data.setWoodcuttingXp(currentXp);

        savePlayerData(playerId);

        return leveledUp;
    }

    public static void grantWellRested(
            ServerPlayer player
    ) {

        wellRestedRemainingTicks.put(
                player.getUUID(),
                WELL_RESTED_DURATION_TICKS
        );

        player.sendSystemMessage(
                Component.literal(
                        "You feel well rested. Skill XP doubled for 10 minutes."
                )
        );
    }

    public static int applySkillXpMultiplier(
            UUID playerId,
            int amount
    ) {

        Integer remainingTicks =
                wellRestedRemainingTicks.get(playerId);

        if (
                remainingTicks == null
                        || remainingTicks <= 0
        ) {

            return amount;
        }

        return amount
                * WELL_RESTED_XP_MULTIPLIER;
    }

    public static void tickWellRestedTimers() {

        wellRestedRemainingTicks.replaceAll(
                (playerId, remainingTicks) ->
                        remainingTicks - 1
        );

        wellRestedRemainingTicks.entrySet()
                .removeIf(
                        entry ->
                                entry.getValue() <= 0
                );
    }

    public static int getWoodcuttingXp(
            UUID playerId
    ) {

        return getPlayerData(playerId)
                .getWoodcuttingXp();
    }

    public static int getWoodcuttingLevel(
            UUID playerId
    ) {

        return getPlayerData(playerId)
                .getWoodcuttingLevel();
    }

    public static int getWoodcuttingXpRequired(
            UUID playerId
    ) {

        int level =
                getWoodcuttingLevel(playerId);

        return WoodcuttingSkill.getXpRequired(
                level
        );
    }

    public static int getWoodcuttingPerkPoints(
            UUID playerId
    ) {

        return getPlayerData(playerId)
                .getWoodcuttingPerkPoints();
    }

    public static boolean unlockWoodcuttingPerk(
            UUID playerId,
            String perkId,
            int requiredLevel
    ) {

        PlayerData data =
                getPlayerData(playerId);

        if (data.hasWoodcuttingPerk(perkId)) {
            return false;
        }

        if (data.getWoodcuttingLevel() < requiredLevel) {
            return false;
        }

        if (data.getWoodcuttingPerkPoints() <= 0) {
            return false;
        }

        data.unlockWoodcuttingPerk(perkId);

        data.setWoodcuttingPerkPoints(
                data.getWoodcuttingPerkPoints() - 1
        );

        savePlayerData(playerId);

        return true;
    }

    public static boolean hasWoodcuttingPerk(
            UUID playerId,
            String perkId
    ) {

        return getPlayerData(playerId)
                .hasWoodcuttingPerk(
                        perkId
                );
    }

    public static Set<String> getSelectedOreSense(
            UUID playerId
    ) {

        return getPlayerData(playerId)
                .getSelectedOreSense();
    }

    public static boolean isOreSelected(
            UUID playerId,
            String ore
    ) {

        return getPlayerData(playerId)
                .getSelectedOreSense()
                .contains(ore);
    }

    public static void toggleOreSense(
            UUID playerId,
            String ore
    ) {

        PlayerData data =
                getPlayerData(playerId);

        Set<String> selected =
                data.getSelectedOreSense();

        if (selected.contains(ore)) {

            selected.remove(ore);

        } else {

            selected.add(ore);
        }

        savePlayerData(playerId);
    }
}
