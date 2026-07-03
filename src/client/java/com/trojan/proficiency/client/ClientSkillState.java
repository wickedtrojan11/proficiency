package com.trojan.proficiency.client;

import com.trojan.proficiency.network.PerkUnlockRequestPayload;
import com.trojan.proficiency.network.SkillStatePayload;
import com.trojan.proficiency.network.ToggleChangeRequestPayload;
import com.trojan.proficiency.perk.FarmingPerks;
import com.trojan.proficiency.perk.MiningPerks;
import com.trojan.proficiency.perk.SkillPerk;
import com.trojan.proficiency.perk.WoodcuttingPerks;
import com.trojan.proficiency.perk.OneHandedPerks;
import com.trojan.proficiency.skill.SkillType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import com.trojan.proficiency.network.PrestigeRosterPayload;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.HashMap;

public final class ClientSkillState {

    private static final SkillStatePayload.SkillState DEFAULT_STATE =
            new SkillStatePayload.SkillState(
                    1,
                    0,
                    10,
                    0,
                    0,
                    Set.of(),
                    Map.of()
            );

    private static SkillStatePayload.SkillState mining = DEFAULT_STATE;
    private static SkillStatePayload.SkillState woodcutting = DEFAULT_STATE;
    private static SkillStatePayload.SkillState farming = DEFAULT_STATE;
    private static SkillStatePayload.SkillState oneHanded = DEFAULT_STATE;
    private static int miningStreak;
    private static boolean initialized;
    private static UUID syncedPlayerId;
    private static final Map<UUID, Integer> prestigeRoster = new HashMap<>();

    private ClientSkillState() {
    }

    public static void register() {

        ClientPlayNetworking.registerGlobalReceiver(
                SkillStatePayload.TYPE,
                (payload, context) ->
                        context.client().execute(
                                () -> apply(payload)
                        )
        );

        ClientPlayNetworking.registerGlobalReceiver(
                PrestigeRosterPayload.TYPE,
                (payload, context) -> context.client().execute(() -> {
                    prestigeRoster.clear();
                    prestigeRoster.putAll(payload.prestigeByPlayer());
                })
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.getConnection() == null) {
                return;
            }

            client.getConnection().getOnlinePlayers().forEach(playerInfo -> {
                int prestige = prestigeRoster.getOrDefault(
                        playerInfo.getProfile().getId(),
                        0
                );

                if (prestige <= 0) {
                    playerInfo.setTabListDisplayName(null);
                    return;
                }

                ChatFormatting color = prestige <= 3
                        ? ChatFormatting.GOLD
                        : prestige <= 6
                        ? ChatFormatting.GRAY
                        : ChatFormatting.YELLOW;
                int stars = prestige <= 9
                        ? ((prestige - 1) % 3) + 1
                        : 3;
                String suffix = prestige > 9
                        ? " +" + (prestige - 9)
                        : "";

                playerInfo.setTabListDisplayName(
                        Component.literal("★".repeat(stars) + suffix + " ")
                                .withStyle(color)
                                .append(Component.literal(
                                        playerInfo.getProfile().getName()
                                ).withStyle(ChatFormatting.WHITE))
                );
            });
        });

        ClientPlayConnectionEvents.DISCONNECT.register(
                (handler, client) -> reset()
        );
        ClientPlayConnectionEvents.JOIN.register(
                (handler, sender, client) -> reset()
        );
    }

    private static void apply(SkillStatePayload payload) {

        Minecraft minecraft = Minecraft.getInstance();

        if (
                minecraft.player == null
                        || !minecraft.player.getUUID()
                        .equals(payload.playerId())
        ) {
            reset();
            return;
        }

        if (
                syncedPlayerId == null
                        || !syncedPlayerId.equals(payload.playerId())
        ) {
            reset();
        }

        if (initialized) {
            showNewPerkToasts(mining, payload.mining(), SkillType.MINING);
            showNewPerkToasts(
                    woodcutting,
                    payload.woodcutting(),
                    SkillType.WOODCUTTING
            );
            showNewPerkToasts(farming, payload.farming(), SkillType.FARMING);
            showNewPerkToasts(
                    oneHanded,
                    payload.oneHanded(),
                    SkillType.ONE_HANDED
            );
        }

        mining = payload.mining();
        woodcutting = payload.woodcutting();
        farming = payload.farming();
        oneHanded = payload.oneHanded();
        miningStreak = payload.miningStreak();
        syncedPlayerId = payload.playerId();
        initialized = true;
    }

    private static void showNewPerkToasts(
            SkillStatePayload.SkillState previous,
            SkillStatePayload.SkillState current,
            SkillType skillType
    ) {

        for (String perkId : current.unlockedPerks()) {

            if (previous.unlockedPerks().contains(perkId)) {
                continue;
            }

            SkillPerk perk = switch (skillType) {
                case MINING -> MiningPerks.getById(perkId);
                case WOODCUTTING -> WoodcuttingPerks.getById(perkId);
                case FARMING -> FarmingPerks.getById(perkId);
                case ONE_HANDED -> OneHandedPerks.getById(perkId);
            };

            if (perk != null) {

                SystemToast.add(
                        Minecraft.getInstance().getToasts(),
                        SystemToast.SystemToastId.PERIODIC_NOTIFICATION,
                        Component.literal("NEW PERK UNLOCKED!"),
                        Component.literal(
                                perk.getName()
                                        + ": "
                                        + perk.getDescription()
                        )
                );
            }
        }
    }

    public static void requestPerkUnlock(
            SkillType skillType,
            String perkId
    ) {

        if (ClientPlayNetworking.canSend(
                PerkUnlockRequestPayload.TYPE
        )) {

            ClientPlayNetworking.send(
                    new PerkUnlockRequestPayload(
                            skillType.getId(),
                            perkId
                    )
            );
        }
    }

    public static void requestPrestige(SkillType skillType) {

        if (ClientPlayNetworking.canSend(
                com.trojan.proficiency.network.PrestigeRequestPayload.TYPE
        )) {
            ClientPlayNetworking.send(
                    new com.trojan.proficiency.network.PrestigeRequestPayload(
                            skillType.getId()
                    )
            );
        }
    }

    private static void requestToggle(
            SkillType skillType,
            String toggleId,
            boolean desiredState
    ) {

        if (ClientPlayNetworking.canSend(
                ToggleChangeRequestPayload.TYPE
        )) {

            ClientPlayNetworking.send(
                    new ToggleChangeRequestPayload(
                            skillType.getId(),
                            toggleId,
                            desiredState
                    )
            );
        }
    }

    private static void reset() {
        mining = DEFAULT_STATE;
        woodcutting = DEFAULT_STATE;
        farming = DEFAULT_STATE;
        oneHanded = DEFAULT_STATE;
        miningStreak = 0;
        syncedPlayerId = null;
        initialized = false;
        prestigeRoster.clear();
    }

    public static int getMiningLevel(UUID ignored) {
        return mining.level();
    }

    public static int getMiningXp(UUID ignored) {
        return mining.xp();
    }

    public static int getMiningXpRequired(UUID ignored) {
        return mining.requiredXp();
    }

    public static int getMiningPerkPoints(UUID ignored) {
        return mining.perkPoints();
    }

    public static int getMiningPrestige(UUID ignored) {
        return mining.prestige();
    }

    public static int getMiningStreak(UUID ignored) {
        return miningStreak;
    }

    public static boolean hasMiningPerk(UUID ignored, String perkId) {
        return mining.unlockedPerks().contains(perkId);
    }

    public static boolean isMiningHeavySwingsEnabled(UUID ignored) {
        return mining.toggles().getOrDefault("heavy_swings", true);
    }

    public static void toggleMiningHeavySwings(UUID ignored) {
        requestToggle(
                SkillType.MINING,
                "heavy_swings",
                !isMiningHeavySwingsEnabled(ignored)
        );
    }

    public static int getWoodcuttingLevel(UUID ignored) {
        return woodcutting.level();
    }

    public static int getWoodcuttingXp(UUID ignored) {
        return woodcutting.xp();
    }

    public static int getWoodcuttingXpRequired(UUID ignored) {
        return woodcutting.requiredXp();
    }

    public static int getWoodcuttingPerkPoints(UUID ignored) {
        return woodcutting.perkPoints();
    }

    public static int getWoodcuttingPrestige(UUID ignored) {
        return woodcutting.prestige();
    }

    public static boolean hasWoodcuttingPerk(
            UUID ignored,
            String perkId
    ) {
        return woodcutting.unlockedPerks().contains(perkId);
    }

    public static int getFarmingLevel(UUID ignored) {
        return farming.level();
    }

    public static int getFarmingXp(UUID ignored) {
        return farming.xp();
    }

    public static int getFarmingXpRequired(UUID ignored) {
        return farming.requiredXp();
    }

    public static int getFarmingPerkPoints(UUID ignored) {
        return farming.perkPoints();
    }

    public static int getFarmingPrestige(UUID ignored) {
        return farming.prestige();
    }

    public static boolean hasFarmingPerk(UUID ignored, String perkId) {
        return farming.unlockedPerks().contains(perkId);
    }

    public static int getOneHandedLevel(UUID ignored) {
        return oneHanded.level();
    }

    public static int getOneHandedXp(UUID ignored) {
        return oneHanded.xp();
    }

    public static int getOneHandedXpRequired(UUID ignored) {
        return oneHanded.requiredXp();
    }

    public static int getOneHandedPerkPoints(UUID ignored) {
        return oneHanded.perkPoints();
    }

    public static int getOneHandedPrestige(UUID ignored) {
        return oneHanded.prestige();
    }

    public static boolean hasOneHandedPerk(UUID ignored, String perkId) {
        return oneHanded.unlockedPerks().contains(perkId);
    }

    public static boolean isOneHandedToggleEnabled(String toggleId) {
        return oneHanded.toggles().getOrDefault(toggleId, true);
    }

    public static void toggleOneHanded(String toggleId) {
        requestToggle(
                SkillType.ONE_HANDED,
                toggleId,
                !isOneHandedToggleEnabled(toggleId)
        );
    }

    public static int getFarmingGrowthBonusPercent(UUID ignored) {

        if (farming.unlockedPerks().contains("greenhouse_genius")) {
            return 50;
        }
        if (farming.unlockedPerks().contains("rapid_growth")) {
            return 40;
        }
        if (farming.unlockedPerks().contains("improved_growth")) {
            return 25;
        }
        if (farming.unlockedPerks().contains("cultivation_faster_growth")) {
            return 10;
        }

        return 0;
    }

    public static int getFarmingAnimalGrowthBonusPercent(UUID ignored) {

        if (farming.unlockedPerks().contains("shepherds_call")) {
            return 75;
        }
        if (farming.unlockedPerks().contains("shepherds_touch")) {
            return 50;
        }
        if (farming.unlockedPerks().contains("animal_faster_growth")) {
            return 25;
        }

        return 0;
    }

    public static Set<String> getSelectedOreSense(UUID ignored) {

        Set<String> selectedOres = new HashSet<>();

        for (Map.Entry<String, Boolean> toggle
                : mining.toggles().entrySet()) {

            if (
                    toggle.getValue()
                            && !"heavy_swings".equals(toggle.getKey())
            ) {
                selectedOres.add(toggle.getKey());
            }
        }

        return selectedOres;
    }

    public static void toggleOreSense(UUID ignored, String oreId) {

        requestToggle(
                SkillType.MINING,
                oreId,
                !mining.toggles().getOrDefault(oreId, false)
        );
    }

    public static boolean isWoodcuttingLeafDecayEnabled(UUID ignored) {
        return toggle(woodcutting, "leaf_decay");
    }

    public static void toggleWoodcuttingLeafDecay(UUID ignored) {
        toggleWoodcutting("leaf_decay");
    }

    public static boolean isWoodcuttingWholeTreeEnabled(UUID ignored) {
        return toggle(woodcutting, "whole_tree");
    }

    public static void toggleWoodcuttingWholeTree(UUID ignored) {
        toggleWoodcutting("whole_tree");
    }

    public static boolean isWoodcuttingBonusDropsEnabled(UUID ignored) {
        return toggle(woodcutting, "bonus_drops");
    }

    public static void toggleWoodcuttingBonusDrops(UUID ignored) {
        toggleWoodcutting("bonus_drops");
    }

    public static boolean isWoodcuttingCleanFloorEnabled(UUID ignored) {
        return toggle(woodcutting, "clean_floor");
    }

    public static void toggleWoodcuttingCleanFloor(UUID ignored) {
        toggleWoodcutting("clean_floor");
    }

    private static void toggleWoodcutting(String toggleId) {

        requestToggle(
                SkillType.WOODCUTTING,
                toggleId,
                !toggle(woodcutting, toggleId)
        );
    }

    public static boolean isFarmingBonusHarvestsEnabled(UUID ignored) {
        return toggle(farming, "bonus_harvests");
    }

    public static void toggleFarmingBonusHarvests(UUID ignored) {
        toggleFarming("bonus_harvests");
    }

    public static boolean isFarmingAnimalFollowEnabled(UUID ignored) {
        return toggle(farming, "animal_follow");
    }

    public static void toggleFarmingAnimalFollow(UUID ignored) {
        toggleFarming("animal_follow");
    }

    public static boolean isFarmingAnimalDropsEnabled(UUID ignored) {
        return toggle(farming, "animal_drops");
    }

    public static void toggleFarmingAnimalDrops(UUID ignored) {
        toggleFarming("animal_drops");
    }

    public static boolean isFarmingAutoReplantEnabled(UUID ignored) {
        return toggle(farming, "auto_replant");
    }

    public static void toggleFarmingAutoReplant(UUID ignored) {
        toggleFarming("auto_replant");
    }

    public static boolean isFarmingGatheringBonusDropsEnabled(UUID ignored) {
        return toggle(farming, "gathering_bonus_drops");
    }

    public static void toggleFarmingGatheringBonusDrops(UUID ignored) {
        toggleFarming("gathering_bonus_drops");
    }

    public static boolean isFarmingBeekeepingEnabled(UUID ignored) {
        return toggle(farming, "beekeeping");
    }

    public static void toggleFarmingBeekeeping(UUID ignored) {
        toggleFarming("beekeeping");
    }

    public static boolean isFarmingAnimalOverlayEnabled(UUID ignored) {
        return toggle(farming, "animal_overlay");
    }

    public static void toggleFarmingAnimalOverlay(UUID ignored) {
        toggleFarming("animal_overlay");
    }

    private static void toggleFarming(String toggleId) {

        requestToggle(
                SkillType.FARMING,
                toggleId,
                !toggle(farming, toggleId)
        );
    }

    private static boolean toggle(
            SkillStatePayload.SkillState state,
            String toggleId
    ) {
        return state.toggles().getOrDefault(toggleId, true);
    }
}
