package com.trojan.proficiency;
import com.trojan.proficiency.perk.MiningPerks;
import com.trojan.proficiency.perk.FarmingPerks;
import com.trojan.proficiency.perk.PerkUnlockResult;
import com.trojan.proficiency.perk.SkillPerk;
import com.trojan.proficiency.perk.WoodcuttingPerks;
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
import com.trojan.proficiency.skill.FarmingSkill;
import com.trojan.proficiency.skill.WoodcuttingSkill;
import com.trojan.proficiency.skill.SkillType;
import com.trojan.proficiency.network.XpGainPayload;
import com.trojan.proficiency.network.WellRestedPayload;
import com.trojan.proficiency.network.SkillStatePayload;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
            5 * 60 * 20;
    private static final int WELL_RESTED_XP_MULTIPLIER = 2;
    private static final Set<String> ORE_TOGGLE_IDS =
            Set.of(
                    "coal",
                    "redstone",
                    "iron",
                    "copper",
                    "lapis",
                    "gold",
                    "emerald",
                    "diamond",
                    "ancient_debris"
            );

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

    public static void sendSkillState(
            ServerPlayer player
    ) {

        PlayerData data = getPlayerData(player.getUUID());
        Map<String, Boolean> miningToggles = new HashMap<>();

        for (String oreId : ORE_TOGGLE_IDS) {

            miningToggles.put(
                    oreId,
                    data.getSelectedOreSense().contains(oreId)
            );
        }

        SkillStatePayload.send(
                player,
                new SkillStatePayload(
                        player.getUUID(),
                        new SkillStatePayload.SkillState(
                                data.getMiningLevel(),
                                data.getMiningXp(),
                                MiningSkill.getXpRequired(
                                        data.getMiningLevel()
                                ),
                                data.getMiningPerkPoints(),
                                data.getUnlockedMiningPerks(),
                                miningToggles
                        ),
                        new SkillStatePayload.SkillState(
                                data.getWoodcuttingLevel(),
                                data.getWoodcuttingXp(),
                                WoodcuttingSkill.getXpRequired(
                                        data.getWoodcuttingLevel()
                                ),
                                data.getWoodcuttingPerkPoints(),
                                data.getUnlockedWoodcuttingPerks(),
                                Map.of(
                                        "leaf_decay",
                                        data.isWoodcuttingLeafDecayEnabled(),
                                        "whole_tree",
                                        data.isWoodcuttingWholeTreeEnabled(),
                                        "bonus_drops",
                                        data.isWoodcuttingBonusDropsEnabled(),
                                        "clean_floor",
                                        data.isWoodcuttingCleanFloorEnabled()
                                )
                        ),
                        new SkillStatePayload.SkillState(
                                data.getFarmingLevel(),
                                data.getFarmingXp(),
                                FarmingSkill.getXpRequired(
                                        data.getFarmingLevel()
                                ),
                                data.getFarmingPerkPoints(),
                                data.getUnlockedFarmingPerks(),
                                Map.of(
                                        "bonus_harvests",
                                        data.isFarmingBonusHarvestsEnabled(),
                                        "animal_follow",
                                        data.isFarmingAnimalFollowEnabled(),
                                        "animal_drops",
                                        data.isFarmingAnimalDropsEnabled(),
                                        "auto_replant",
                                        data.isFarmingAutoReplantEnabled(),
                                        "bee_growth",
                                        data.isFarmingBeeGrowthEnabled(),
                                        "gathering_bonus_drops",
                                        data.isFarmingBeeGrowthEnabled(),
                                        "beekeeping",
                                        data.isFarmingBeekeepingEnabled(),
                                        "animal_overlay",
                                        data.isFarmingAnimalOverlayEnabled()
                                )
                        ),
                        getMiningStreak(player.getUUID())
                )
        );
    }

    public static boolean setToggle(
            UUID playerId,
            SkillType skillType,
            String toggleId,
            boolean desiredState
    ) {

        PlayerData data = getPlayerData(playerId);
        boolean valid = switch (skillType) {
            case MINING -> setMiningToggle(
                    data,
                    toggleId,
                    desiredState
            );
            case WOODCUTTING -> setWoodcuttingToggle(
                    data,
                    toggleId,
                    desiredState
            );
            case FARMING -> setFarmingToggle(
                    data,
                    toggleId,
                    desiredState
            );
        };

        if (valid) {
            savePlayerData(playerId);
        }

        return valid;
    }

    private static boolean setMiningToggle(
            PlayerData data,
            String toggleId,
            boolean desiredState
    ) {

        if (!ORE_TOGGLE_IDS.contains(toggleId)) {
            return false;
        }

        if (desiredState) {
            data.getSelectedOreSense().add(toggleId);
        } else {
            data.getSelectedOreSense().remove(toggleId);
        }

        return true;
    }

    private static boolean setWoodcuttingToggle(
            PlayerData data,
            String toggleId,
            boolean desiredState
    ) {

        switch (toggleId) {
            case "leaf_decay" ->
                    data.setWoodcuttingLeafDecayEnabled(desiredState);
            case "whole_tree" ->
                    data.setWoodcuttingWholeTreeEnabled(desiredState);
            case "bonus_drops" ->
                    data.setWoodcuttingBonusDropsEnabled(desiredState);
            case "clean_floor" ->
                    data.setWoodcuttingCleanFloorEnabled(desiredState);
            default -> {
                return false;
            }
        }

        return true;
    }

    private static boolean setFarmingToggle(
            PlayerData data,
            String toggleId,
            boolean desiredState
    ) {

        switch (toggleId) {
            case "bonus_harvests" ->
                    data.setFarmingBonusHarvestsEnabled(desiredState);
            case "animal_follow" ->
                    data.setFarmingAnimalFollowEnabled(desiredState);
            case "animal_drops" ->
                    data.setFarmingAnimalDropsEnabled(desiredState);
            case "auto_replant" ->
                    data.setFarmingAutoReplantEnabled(desiredState);
            case "bee_growth" ->
                    data.setFarmingBeeGrowthEnabled(desiredState);
            case "gathering_bonus_drops" ->
                    data.setFarmingBeeGrowthEnabled(desiredState);
            case "beekeeping" ->
                    data.setFarmingBeekeepingEnabled(desiredState);
            case "animal_overlay" ->
                    data.setFarmingAnimalOverlayEnabled(desiredState);
            default -> {
                return false;
            }
        }

        return true;
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

        XpGainPayload.send(
                player,
                SkillType.MINING,
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
                    currentPerkPoints
                            + getPerkPointsAwardForLevel(
                                    currentLevel
                            )
            );

            leveledUp = true;
        }

        data.setMiningXp(currentXp);

        savePlayerData(playerId);
        sendSkillState(player);

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

        return unlockPerk(
                playerId,
                SkillType.MINING,
                perkId
        ).success();
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
            ServerPlayer player,
            int amount
    ) {

        UUID playerId = player.getUUID();

        amount =
                applySkillXpMultiplier(
                        playerId,
                        amount
                );

        XpGainPayload.send(
                player,
                SkillType.WOODCUTTING,
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
                    data.getWoodcuttingPerkPoints()
                            + getPerkPointsAwardForLevel(
                                    currentLevel
                            )
            );

            leveledUp = true;
        }

        data.setWoodcuttingXp(currentXp);

        savePlayerData(playerId);
        sendSkillState(player);

        return leveledUp;
    }

    public static void grantWellRested(
            ServerPlayer player
    ) {

        wellRestedRemainingTicks.put(
                player.getUUID(),
                WELL_RESTED_DURATION_TICKS
        );

        WellRestedPayload.send(
                player,
                WELL_RESTED_DURATION_TICKS
        );

        player.sendSystemMessage(
                Component.literal(
                        "You feel well rested. Skill XP doubled for 5 minutes."
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

        return unlockPerk(
                playerId,
                SkillType.WOODCUTTING,
                perkId
        ).success();
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

    public static boolean isWoodcuttingLeafDecayEnabled(
            UUID playerId
    ) {

        return getPlayerData(playerId)
                .isWoodcuttingLeafDecayEnabled();
    }

    public static void toggleWoodcuttingLeafDecay(
            UUID playerId
    ) {

        PlayerData data =
                getPlayerData(playerId);

        data.setWoodcuttingLeafDecayEnabled(
                !data.isWoodcuttingLeafDecayEnabled()
        );

        savePlayerData(playerId);
    }

    public static boolean isWoodcuttingWholeTreeEnabled(
            UUID playerId
    ) {

        return getPlayerData(playerId)
                .isWoodcuttingWholeTreeEnabled();
    }

    public static void toggleWoodcuttingWholeTree(
            UUID playerId
    ) {

        PlayerData data =
                getPlayerData(playerId);

        data.setWoodcuttingWholeTreeEnabled(
                !data.isWoodcuttingWholeTreeEnabled()
        );

        savePlayerData(playerId);
    }

    public static boolean isWoodcuttingBonusDropsEnabled(
            UUID playerId
    ) {

        return getPlayerData(playerId)
                .isWoodcuttingBonusDropsEnabled();
    }

    public static void toggleWoodcuttingBonusDrops(
            UUID playerId
    ) {

        PlayerData data =
                getPlayerData(playerId);

        data.setWoodcuttingBonusDropsEnabled(
                !data.isWoodcuttingBonusDropsEnabled()
        );

        savePlayerData(playerId);
    }

    public static boolean isWoodcuttingCleanFloorEnabled(
            UUID playerId
    ) {

        return getPlayerData(playerId)
                .isWoodcuttingCleanFloorEnabled();
    }

    public static void toggleWoodcuttingCleanFloor(
            UUID playerId
    ) {

        PlayerData data =
                getPlayerData(playerId);

        data.setWoodcuttingCleanFloorEnabled(
                !data.isWoodcuttingCleanFloorEnabled()
        );

        savePlayerData(playerId);
    }

    // =========================
    // FARMING
    // =========================

    public static boolean addFarmingXp(
            ServerPlayer player,
            int amount
    ) {

        UUID playerId =
                player.getUUID();

        amount =
                applySkillXpMultiplier(
                        playerId,
                        amount
                );

        XpGainPayload.send(
                player,
                SkillType.FARMING,
                amount
        );

        PlayerData data =
                getPlayerData(playerId);

        int currentXp =
                data.getFarmingXp()
                        + amount;

        int currentLevel =
                data.getFarmingLevel();

        int xpRequired =
                FarmingSkill.getXpRequired(
                        currentLevel
                );

        boolean leveledUp = false;

        if (currentXp >= xpRequired) {

            currentXp = 0;
            currentLevel++;

            data.setFarmingLevel(
                    currentLevel
            );

            data.setFarmingPerkPoints(
                    data.getFarmingPerkPoints()
                            + getPerkPointsAwardForLevel(
                                    currentLevel
                            )
            );

            announceFarmingLevelUp(
                    player,
                    currentLevel,
                    data.getFarmingPerkPoints()
            );

            announceAvailableFarmingPerks(
                    player,
                    currentLevel
            );

            leveledUp = true;
        }

        data.setFarmingXp(currentXp);
        savePlayerData(playerId);
        sendSkillState(player);

        return leveledUp;
    }

    private static void announceFarmingLevelUp(
            ServerPlayer player,
            int level,
            int perkPoints
    ) {

        player.sendSystemMessage(
                Component.literal(
                        "\u00A72Farming Level Up! \u2192 Level "
                                + level
                )
        );

        player.sendSystemMessage(
                Component.literal(
                        "\u00A7bPerk points earned: "
                                + getPerkPointsAwardForLevel(level)
                                + ". Total: "
                                + perkPoints
                )
        );

        player.level().playSound(
                null,
                player.blockPosition(),
                SoundEvents.PLAYER_LEVELUP,
                SoundSource.PLAYERS,
                0.7f,
                1.0f
        );
    }

    private static void announceAvailableFarmingPerks(
            ServerPlayer player,
            int level
    ) {

        for (SkillPerk perk
                : FarmingPerks.ALL_PERKS) {

            if (level == perk.getRequiredLevel()) {

                player.sendSystemMessage(
                        Component.literal(
                                "\u00A7aNEW PERK AVAILABLE: "
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

    public static int getFarmingXp(
            UUID playerId
    ) {

        return getPlayerData(playerId)
                .getFarmingXp();
    }

    public static int getFarmingLevel(
            UUID playerId
    ) {

        return getPlayerData(playerId)
                .getFarmingLevel();
    }

    public static int getFarmingXpRequired(
            UUID playerId
    ) {

        return FarmingSkill.getXpRequired(
                getFarmingLevel(playerId)
        );
    }

    public static int getFarmingPerkPoints(
            UUID playerId
    ) {

        return getPlayerData(playerId)
                .getFarmingPerkPoints();
    }

    public static boolean unlockFarmingPerk(
            UUID playerId,
            String perkId,
            int requiredLevel
    ) {

        return unlockPerk(
                playerId,
                SkillType.FARMING,
                perkId
        ).success();
    }

    public static PerkUnlockResult unlockPerk(
            UUID playerId,
            SkillType skillType,
            String perkId
    ) {

        PlayerData data = getPlayerData(playerId);
        SkillPerk perk = getPerk(skillType, perkId);

        if (perk == null) {

            return new PerkUnlockResult(
                    PerkUnlockResult.Status.PERK_NOT_FOUND,
                    "Unknown "
                            + skillType.getDisplayName()
                            + " perk: "
                            + perkId
            );
        }

        if (hasPerk(data, skillType, perkId)) {

            return new PerkUnlockResult(
                    PerkUnlockResult.Status.ALREADY_UNLOCKED,
                    perk.getName()
                            + " is already unlocked."
            );
        }

        int playerLevel = getLevel(data, skillType);

        if (playerLevel < perk.getRequiredLevel()) {

            return new PerkUnlockResult(
                    PerkUnlockResult.Status.LEVEL_REQUIRED,
                    perk.getName()
                            + " requires "
                            + skillType.getDisplayName()
                            + " Level "
                            + perk.getRequiredLevel()
                            + "."
            );
        }

        if (
                perk.getParentId() != null
                        && !hasPerk(
                        data,
                        skillType,
                        perk.getParentId()
                )
        ) {

            SkillPerk parent =
                    getPerk(
                            skillType,
                            perk.getParentId()
                    );

            return new PerkUnlockResult(
                    PerkUnlockResult.Status.PARENT_REQUIRED,
                    "Unlock "
                            + (parent == null
                            ? perk.getParentId()
                            : parent.getName())
                            + " first."
            );
        }

        int availablePoints =
                getPerkPoints(data, skillType);
        int pointCost = perk.getPointCost();

        if (availablePoints < pointCost) {

            return new PerkUnlockResult(
                    PerkUnlockResult.Status.INSUFFICIENT_POINTS,
                    perk.getName()
                            + " needs "
                            + pointCost
                            + " perk points (you have "
                            + availablePoints
                            + ")."
            );
        }

        unlockPerk(data, skillType, perkId);
        setPerkPoints(
                data,
                skillType,
                availablePoints - pointCost
        );
        savePlayerData(playerId);

        return new PerkUnlockResult(
                PerkUnlockResult.Status.SUCCESS,
                "Unlocked "
                        + perk.getName()
                        + "!"
        );
    }

    private static SkillPerk getPerk(
            SkillType skillType,
            String perkId
    ) {

        return switch (skillType) {
            case MINING -> MiningPerks.getById(perkId);
            case WOODCUTTING -> WoodcuttingPerks.getById(perkId);
            case FARMING -> FarmingPerks.getById(perkId);
        };
    }

    private static int getLevel(
            PlayerData data,
            SkillType skillType
    ) {

        return switch (skillType) {
            case MINING -> data.getMiningLevel();
            case WOODCUTTING -> data.getWoodcuttingLevel();
            case FARMING -> data.getFarmingLevel();
        };
    }

    private static int getPerkPoints(
            PlayerData data,
            SkillType skillType
    ) {

        return switch (skillType) {
            case MINING -> data.getMiningPerkPoints();
            case WOODCUTTING -> data.getWoodcuttingPerkPoints();
            case FARMING -> data.getFarmingPerkPoints();
        };
    }

    private static boolean hasPerk(
            PlayerData data,
            SkillType skillType,
            String perkId
    ) {

        return switch (skillType) {
            case MINING -> data.hasMiningPerk(perkId);
            case WOODCUTTING -> data.hasWoodcuttingPerk(perkId);
            case FARMING -> data.hasFarmingPerk(perkId);
        };
    }

    private static void unlockPerk(
            PlayerData data,
            SkillType skillType,
            String perkId
    ) {

        switch (skillType) {
            case MINING -> data.unlockMiningPerk(perkId);
            case WOODCUTTING -> data.unlockWoodcuttingPerk(perkId);
            case FARMING -> data.unlockFarmingPerk(perkId);
        }
    }

    private static void setPerkPoints(
            PlayerData data,
            SkillType skillType,
            int perkPoints
    ) {

        switch (skillType) {
            case MINING -> data.setMiningPerkPoints(perkPoints);
            case WOODCUTTING ->
                    data.setWoodcuttingPerkPoints(perkPoints);
            case FARMING -> data.setFarmingPerkPoints(perkPoints);
        }
    }

    public static int getPerkPointsAwardForLevel(
            int level
    ) {

        if (level <= 10) {
            return 1;
        }

        if (level <= 25) {
            return 2;
        }

        return 3;
    }

    public static boolean hasFarmingPerk(
            UUID playerId,
            String perkId
    ) {

        return getPlayerData(playerId)
                .hasFarmingPerk(perkId);
    }

    public static int getFarmingGrowthBonusPercent(
            UUID playerId
    ) {

        if (
                hasFarmingPerk(
                        playerId,
                        "greenhouse_genius"
                )
        ) {

            return 50;
        }

        if (
                hasFarmingPerk(
                        playerId,
                        "rapid_growth"
                )
        ) {

            return 40;
        }

        if (
                hasFarmingPerk(
                        playerId,
                        "improved_growth"
                )
        ) {

            return 25;
        }

        if (
                hasFarmingPerk(
                        playerId,
                        "cultivation_faster_growth"
                )
        ) {

            return 10;
        }

        return 0;
    }

    public static int getFarmingAnimalGrowthBonusPercent(
            UUID playerId
    ) {

        if (
                hasFarmingPerk(
                        playerId,
                        "shepherds_call"
                )
        ) {

            return 75;
        }

        if (
                hasFarmingPerk(
                        playerId,
                        "shepherds_touch"
                )
        ) {

            return 50;
        }

        if (
                hasFarmingPerk(
                        playerId,
                        "animal_faster_growth"
                )
        ) {

            return 25;
        }

        return 0;
    }

    public static boolean isFarmingBonusHarvestsEnabled(
            UUID playerId
    ) {

        return getPlayerData(playerId)
                .isFarmingBonusHarvestsEnabled();
    }

    public static void toggleFarmingBonusHarvests(
            UUID playerId
    ) {

        PlayerData data =
                getPlayerData(playerId);

        data.setFarmingBonusHarvestsEnabled(
                !data.isFarmingBonusHarvestsEnabled()
        );

        savePlayerData(playerId);
    }

    public static boolean isFarmingAnimalFollowEnabled(
            UUID playerId
    ) {

        return getPlayerData(playerId)
                .isFarmingAnimalFollowEnabled();
    }

    public static void toggleFarmingAnimalFollow(
            UUID playerId
    ) {

        PlayerData data =
                getPlayerData(playerId);

        data.setFarmingAnimalFollowEnabled(
                !data.isFarmingAnimalFollowEnabled()
        );

        savePlayerData(playerId);
    }

    public static boolean isFarmingAnimalDropsEnabled(
            UUID playerId
    ) {

        return getPlayerData(playerId)
                .isFarmingAnimalDropsEnabled();
    }

    public static void toggleFarmingAnimalDrops(
            UUID playerId
    ) {

        PlayerData data =
                getPlayerData(playerId);

        data.setFarmingAnimalDropsEnabled(
                !data.isFarmingAnimalDropsEnabled()
        );

        savePlayerData(playerId);
    }

    public static boolean isFarmingAutoReplantEnabled(
            UUID playerId
    ) {

        return getPlayerData(playerId)
                .isFarmingAutoReplantEnabled();
    }

    public static void toggleFarmingAutoReplant(
            UUID playerId
    ) {

        PlayerData data =
                getPlayerData(playerId);

        data.setFarmingAutoReplantEnabled(
                !data.isFarmingAutoReplantEnabled()
        );

        savePlayerData(playerId);
    }

    public static boolean isFarmingBeeGrowthEnabled(
            UUID playerId
    ) {

        return getPlayerData(playerId)
                .isFarmingBeeGrowthEnabled();
    }

    public static void toggleFarmingBeeGrowth(
            UUID playerId
    ) {

        PlayerData data =
                getPlayerData(playerId);

        data.setFarmingBeeGrowthEnabled(
                !data.isFarmingBeeGrowthEnabled()
        );

        savePlayerData(playerId);
    }

    public static boolean isFarmingGatheringBonusDropsEnabled(
            UUID playerId
    ) {

        return isFarmingBeeGrowthEnabled(playerId);
    }

    public static void toggleFarmingGatheringBonusDrops(
            UUID playerId
    ) {

        toggleFarmingBeeGrowth(playerId);
    }

    public static boolean isFarmingBeekeepingEnabled(
            UUID playerId
    ) {

        return getPlayerData(playerId)
                .isFarmingBeekeepingEnabled();
    }

    public static void toggleFarmingBeekeeping(
            UUID playerId
    ) {

        PlayerData data =
                getPlayerData(playerId);

        data.setFarmingBeekeepingEnabled(
                !data.isFarmingBeekeepingEnabled()
        );

        savePlayerData(playerId);
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
