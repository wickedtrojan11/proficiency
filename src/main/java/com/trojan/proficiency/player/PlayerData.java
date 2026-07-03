package com.trojan.proficiency.player;

import java.util.HashSet;
import java.util.Set;

public class PlayerData {
    public PlayerData() {

        selectedOreSense.add("coal");
    }
    // =========================
    // MINING
    // =========================

    private int miningXp = 0;

    private int miningLevel = 1;

    private int miningPerkPoints = 0;

    private int miningPrestige = 0;

    private Boolean miningHeavySwingsEnabled = true;

    private Set<String> unlockedMiningPerks =
            new HashSet<>();

    // =========================
    // WOODCUTTING
    // =========================

    private int woodcuttingXp = 0;

    private int woodcuttingLevel = 1;

    private int woodcuttingPerkPoints = 0;

    private int woodcuttingPrestige = 0;

    private Set<String> unlockedWoodcuttingPerks =
            new HashSet<>();

    private Boolean woodcuttingLeafDecayEnabled = true;

    private Boolean woodcuttingWholeTreeEnabled = true;

    private Boolean woodcuttingBonusDropsEnabled = true;

    private Boolean woodcuttingCleanFloorEnabled = true;

    private Boolean woodcuttingDecapitationEnabled = true;

    // =========================
    // FARMING
    // =========================

    private int farmingXp = 0;

    private int farmingLevel = 1;

    private int farmingPerkPoints = 0;

    private int farmingPrestige = 0;

    private Set<String> unlockedFarmingPerks =
            new HashSet<>();

    private Boolean farmingBonusHarvestsEnabled = true;

    private Boolean farmingAnimalFollowEnabled = true;

    private Boolean farmingAnimalDropsEnabled = true;

    private Boolean farmingAutoReplantEnabled = true;

    private Boolean farmingBeeGrowthEnabled = true;

    private Boolean farmingBeekeepingEnabled = true;

    private Boolean farmingAnimalOverlayEnabled = true;

    private int oneHandedXp = 0;
    private int oneHandedLevel = 1;
    private int oneHandedPerkPoints = 0;
    private int oneHandedPrestige = 0;
    private Set<String> unlockedOneHandedPerks = new HashSet<>();
    private Boolean oneHandedDualWieldEnabled = true;
    private Boolean oneHandedParryEnabled = true;
    private Boolean oneHandedShieldEffectsEnabled = true;
    private Boolean oneHandedBonusLootEnabled = true;

    private Set<String> selectedOreSense =
            new HashSet<>();
    public Set<String> getSelectedOreSense() {

        if (selectedOreSense == null) {

            selectedOreSense =
                    new HashSet<>();

            selectedOreSense.add("coal");
        }

        return selectedOreSense;
    }
    public void setSelectedOreSense(
            Set<String> selectedOreSense
    ) {

        this.selectedOreSense =
                selectedOreSense;
    }
    // =========================
    // MINING GETTERS/SETTERS
    // =========================

    public int getMiningXp() {
        return miningXp;
    }

    public void setMiningXp(int miningXp) {
        this.miningXp = miningXp;
    }

    public int getMiningLevel() {
        return miningLevel;
    }

    public void setMiningLevel(int miningLevel) {
        this.miningLevel = miningLevel;
    }

    public int getMiningPerkPoints() {
        return miningPerkPoints;
    }

    public void setMiningPerkPoints(
            int miningPerkPoints
    ) {
        this.miningPerkPoints =
                miningPerkPoints;
    }

    // =========================
    // PERKS
    // =========================

    public Set<String> getUnlockedMiningPerks() {
        if (unlockedMiningPerks == null) {
            unlockedMiningPerks = new HashSet<>();
        }
        return unlockedMiningPerks;
    }

    public boolean hasMiningPerk(
            String perkId
    ) {

        return getUnlockedMiningPerks().contains(
                perkId
        );
    }

    public void unlockMiningPerk(
            String perkId
    ) {

        getUnlockedMiningPerks().add(
                perkId
        );
    }

    // =========================
    // WOODCUTTING
    // =========================

    public int getWoodcuttingXp() {
        return woodcuttingXp;
    }

    public void setWoodcuttingXp(
            int woodcuttingXp
    ) {
        this.woodcuttingXp =
                woodcuttingXp;
    }

    public int getWoodcuttingLevel() {
        return woodcuttingLevel;
    }

    public void setWoodcuttingLevel(
            int woodcuttingLevel
    ) {
        this.woodcuttingLevel =
                woodcuttingLevel;
    }

    public int getWoodcuttingPerkPoints() {
        return woodcuttingPerkPoints;
    }

    public void setWoodcuttingPerkPoints(
            int woodcuttingPerkPoints
    ) {
        this.woodcuttingPerkPoints =
                woodcuttingPerkPoints;
    }

    public Set<String> getUnlockedWoodcuttingPerks() {
        if (unlockedWoodcuttingPerks == null) {
            unlockedWoodcuttingPerks = new HashSet<>();
        }
        return unlockedWoodcuttingPerks;
    }

    public boolean hasWoodcuttingPerk(
            String perkId
    ) {

        return getUnlockedWoodcuttingPerks().contains(
                perkId
        );
    }

    public void unlockWoodcuttingPerk(
            String perkId
    ) {

        getUnlockedWoodcuttingPerks().add(
                perkId
        );
    }

    public boolean isWoodcuttingLeafDecayEnabled() {

        return woodcuttingLeafDecayEnabled == null
                || woodcuttingLeafDecayEnabled;
    }

    public void setWoodcuttingLeafDecayEnabled(
            boolean enabled
    ) {

        woodcuttingLeafDecayEnabled = enabled;
    }

    public boolean isWoodcuttingWholeTreeEnabled() {

        return woodcuttingWholeTreeEnabled == null
                || woodcuttingWholeTreeEnabled;
    }

    public void setWoodcuttingWholeTreeEnabled(
            boolean enabled
    ) {

        woodcuttingWholeTreeEnabled = enabled;
    }

    public boolean isWoodcuttingBonusDropsEnabled() {

        return woodcuttingBonusDropsEnabled == null
                || woodcuttingBonusDropsEnabled;
    }

    public void setWoodcuttingBonusDropsEnabled(
            boolean enabled
    ) {

        woodcuttingBonusDropsEnabled = enabled;
    }

    public boolean isWoodcuttingCleanFloorEnabled() {

        return woodcuttingCleanFloorEnabled == null
                || woodcuttingCleanFloorEnabled;
    }

    public void setWoodcuttingCleanFloorEnabled(
            boolean enabled
    ) {

        woodcuttingCleanFloorEnabled = enabled;
    }

    public boolean isWoodcuttingDecapitationEnabled() {
        return woodcuttingDecapitationEnabled == null
                || woodcuttingDecapitationEnabled;
    }

    public void setWoodcuttingDecapitationEnabled(boolean enabled) {
        woodcuttingDecapitationEnabled = enabled;
    }

    public int getFarmingXp() {

        return farmingXp;
    }

    public void setFarmingXp(int farmingXp) {

        this.farmingXp = farmingXp;
    }

    public int getFarmingLevel() {

        return farmingLevel;
    }

    public void setFarmingLevel(int farmingLevel) {

        this.farmingLevel = farmingLevel;
    }

    public int getFarmingPerkPoints() {

        return farmingPerkPoints;
    }

    public void setFarmingPerkPoints(
            int farmingPerkPoints
    ) {

        this.farmingPerkPoints =
                farmingPerkPoints;
    }

    public Set<String> getUnlockedFarmingPerks() {

        if (unlockedFarmingPerks == null) {

            unlockedFarmingPerks =
                    new HashSet<>();
        }

        return unlockedFarmingPerks;
    }

    public boolean hasFarmingPerk(
            String perkId
    ) {

        return getUnlockedFarmingPerks()
                .contains(perkId);
    }

    public void unlockFarmingPerk(
            String perkId
    ) {

        getUnlockedFarmingPerks()
                .add(perkId);
    }

    public boolean isFarmingBonusHarvestsEnabled() {

        return farmingBonusHarvestsEnabled == null
                || farmingBonusHarvestsEnabled;
    }

    public void setFarmingBonusHarvestsEnabled(
            boolean enabled
    ) {

        farmingBonusHarvestsEnabled = enabled;
    }

    public boolean isFarmingAnimalFollowEnabled() {

        return farmingAnimalFollowEnabled == null
                || farmingAnimalFollowEnabled;
    }

    public void setFarmingAnimalFollowEnabled(
            boolean enabled
    ) {

        farmingAnimalFollowEnabled = enabled;
    }

    public boolean isFarmingAnimalDropsEnabled() {

        return farmingAnimalDropsEnabled == null
                || farmingAnimalDropsEnabled;
    }

    public void setFarmingAnimalDropsEnabled(
            boolean enabled
    ) {

        farmingAnimalDropsEnabled = enabled;
    }

    public boolean isFarmingAutoReplantEnabled() {

        return farmingAutoReplantEnabled == null
                || farmingAutoReplantEnabled;
    }

    public void setFarmingAutoReplantEnabled(
            boolean enabled
    ) {

        farmingAutoReplantEnabled = enabled;
    }

    public boolean isFarmingBeeGrowthEnabled() {

        return farmingBeeGrowthEnabled == null
                || farmingBeeGrowthEnabled;
    }

    public void setFarmingBeeGrowthEnabled(
            boolean enabled
    ) {

        farmingBeeGrowthEnabled = enabled;
    }

    public boolean isFarmingBeekeepingEnabled() {

        return farmingBeekeepingEnabled == null
                || farmingBeekeepingEnabled;
    }

    public void setFarmingBeekeepingEnabled(
            boolean enabled
    ) {

        farmingBeekeepingEnabled = enabled;
    }

    public boolean isFarmingAnimalOverlayEnabled() {

        return farmingAnimalOverlayEnabled == null
                || farmingAnimalOverlayEnabled;
    }

    public void setFarmingAnimalOverlayEnabled(
            boolean enabled
    ) {

        farmingAnimalOverlayEnabled = enabled;
    }

    public int getMiningPrestige() {
        return miningPrestige;
    }

    public void setMiningPrestige(int prestige) {
        miningPrestige = Math.max(0, prestige);
    }

    public int getWoodcuttingPrestige() {
        return woodcuttingPrestige;
    }

    public void setWoodcuttingPrestige(int prestige) {
        woodcuttingPrestige = Math.max(0, prestige);
    }

    public int getFarmingPrestige() {
        return farmingPrestige;
    }

    public void setFarmingPrestige(int prestige) {
        farmingPrestige = Math.max(0, prestige);
    }

    public void clearMiningPerks() {
        getUnlockedMiningPerks().clear();
    }

    public boolean isMiningHeavySwingsEnabled() {
        return miningHeavySwingsEnabled == null
                || miningHeavySwingsEnabled;
    }

    public void setMiningHeavySwingsEnabled(boolean enabled) {
        miningHeavySwingsEnabled = enabled;
    }

    public void clearWoodcuttingPerks() {
        getUnlockedWoodcuttingPerks().clear();
    }

    public void clearFarmingPerks() {
        getUnlockedFarmingPerks().clear();
    }

    public int getOneHandedXp() {
        return oneHandedXp;
    }

    public void setOneHandedXp(int xp) {
        oneHandedXp = xp;
    }

    public int getOneHandedLevel() {
        return oneHandedLevel;
    }

    public void setOneHandedLevel(int level) {
        oneHandedLevel = level;
    }

    public int getOneHandedPerkPoints() {
        return oneHandedPerkPoints;
    }

    public void setOneHandedPerkPoints(int perkPoints) {
        oneHandedPerkPoints = perkPoints;
    }

    public int getOneHandedPrestige() {
        return oneHandedPrestige;
    }

    public void setOneHandedPrestige(int prestige) {
        oneHandedPrestige = Math.max(0, prestige);
    }

    public Set<String> getUnlockedOneHandedPerks() {
        if (unlockedOneHandedPerks == null) {
            unlockedOneHandedPerks = new HashSet<>();
        }
        return unlockedOneHandedPerks;
    }

    public boolean hasOneHandedPerk(String perkId) {
        return getUnlockedOneHandedPerks().contains(perkId);
    }

    public void unlockOneHandedPerk(String perkId) {
        getUnlockedOneHandedPerks().add(perkId);
    }

    public void clearOneHandedPerks() {
        getUnlockedOneHandedPerks().clear();
    }

    public boolean isOneHandedDualWieldEnabled() {
        return oneHandedDualWieldEnabled == null || oneHandedDualWieldEnabled;
    }

    public void setOneHandedDualWieldEnabled(boolean enabled) {
        oneHandedDualWieldEnabled = enabled;
    }

    public boolean isOneHandedParryEnabled() {
        return oneHandedParryEnabled == null || oneHandedParryEnabled;
    }

    public void setOneHandedParryEnabled(boolean enabled) {
        oneHandedParryEnabled = enabled;
    }

    public boolean isOneHandedShieldEffectsEnabled() {
        return oneHandedShieldEffectsEnabled == null || oneHandedShieldEffectsEnabled;
    }

    public void setOneHandedShieldEffectsEnabled(boolean enabled) {
        oneHandedShieldEffectsEnabled = enabled;
    }

    public boolean isOneHandedBonusLootEnabled() {
        return oneHandedBonusLootEnabled == null || oneHandedBonusLootEnabled;
    }

    public void setOneHandedBonusLootEnabled(boolean enabled) {
        oneHandedBonusLootEnabled = enabled;
    }
}
