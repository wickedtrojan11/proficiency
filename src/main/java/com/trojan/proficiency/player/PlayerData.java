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

    private final Set<String> unlockedMiningPerks =
            new HashSet<>();

    // =========================
    // WOODCUTTING
    // =========================

    private int woodcuttingXp = 0;

    private int woodcuttingLevel = 1;

    private int woodcuttingPerkPoints = 0;

    private final Set<String> unlockedWoodcuttingPerks =
            new HashSet<>();

    private Boolean woodcuttingLeafDecayEnabled = true;

    private Boolean woodcuttingWholeTreeEnabled = true;

    private Boolean woodcuttingBonusDropsEnabled = true;

    private Boolean woodcuttingCleanFloorEnabled = true;

    // =========================
    // FARMING
    // =========================

    private int farmingXp = 0;

    private int farmingLevel = 1;

    private int farmingPerkPoints = 0;

    private Set<String> unlockedFarmingPerks =
            new HashSet<>();

    private Boolean farmingBonusHarvestsEnabled = true;

    private Boolean farmingAnimalFollowEnabled = true;

    private Boolean farmingAutoReplantEnabled = true;

    private Boolean farmingBeeGrowthEnabled = true;

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
        return unlockedMiningPerks;
    }

    public boolean hasMiningPerk(
            String perkId
    ) {

        return unlockedMiningPerks.contains(
                perkId
        );
    }

    public void unlockMiningPerk(
            String perkId
    ) {

        unlockedMiningPerks.add(
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
        return unlockedWoodcuttingPerks;
    }

    public boolean hasWoodcuttingPerk(
            String perkId
    ) {

        return unlockedWoodcuttingPerks.contains(
                perkId
        );
    }

    public void unlockWoodcuttingPerk(
            String perkId
    ) {

        unlockedWoodcuttingPerks.add(
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
}
