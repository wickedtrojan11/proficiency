package com.trojan.proficiency.event;

import com.trojan.proficiency.SkillManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.phys.AABB;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class FarmingAnimalEffects {

    private static final double CARE_RADIUS = 12.0;
    private static final double HERD_RADIUS = 16.0;
    private static final int FOLLOW_INTERVAL_TICKS = 5;

    private FarmingAnimalEffects() {
    }

    public static void register() {

        ServerTickEvents.END_SERVER_TICK.register(server -> {

            Set<UUID> acceleratedBabies = new HashSet<>();

            for (ServerPlayer player : server.getPlayerList().getPlayers()) {

                applyAnimalCare(
                        player,
                        acceleratedBabies,
                        server.getTickCount()
                );

                if (
                        server.getTickCount()
                                % FOLLOW_INTERVAL_TICKS
                                == 0
                ) {

                    applyAnimalFollowing(player);
                }
            }
        });
    }

    public static int getNearbyWoolRegrowthBonusPercent(
            Animal animal
    ) {

        if (!(animal.level() instanceof ServerLevel level)) {
            return 0;
        }

        int bestBonus = 0;

        for (ServerPlayer player : level.getPlayers(
                serverPlayer ->
                        serverPlayer.distanceToSqr(animal)
                                <= CARE_RADIUS * CARE_RADIUS
        )) {

            if (SkillManager.hasFarmingPerk(
                    player.getUUID(),
                    "shepherds_touch"
            )) {
                return 50;
            }

            if (SkillManager.hasFarmingPerk(
                    player.getUUID(),
                    "healthy_flocks"
            )) {
                bestBonus = 25;
            }
        }

        return bestBonus;
    }

    private static void applyAnimalCare(
            ServerPlayer player,
            Set<UUID> acceleratedBabies,
            int serverTick
    ) {

        int growthBonusPercent =
                SkillManager
                        .getFarmingAnimalGrowthBonusPercent(
                                player.getUUID()
                        );

        if (
                growthBonusPercent <= 0
                        || !shouldApplyGrowthBonus(
                                growthBonusPercent,
                                serverTick
                        )
        ) {
            return;
        }

        AABB area = player.getBoundingBox().inflate(CARE_RADIUS);

        for (AgeableMob animal : player.serverLevel().getEntitiesOfClass(
                AgeableMob.class,
                area,
                AgeableMob::isBaby
        )) {

            if (acceleratedBabies.add(animal.getUUID())) {

                animal.setAge(
                        Math.min(
                                0,
                                animal.getAge() + 1
                        )
                );
            }
        }
    }

    private static boolean shouldApplyGrowthBonus(
            int growthBonusPercent,
            int serverTick
    ) {

        int phase = Math.floorMod(
                serverTick,
                4
        );

        if (growthBonusPercent >= 75) {
            return phase < 3;
        }

        if (growthBonusPercent >= 50) {
            return phase < 2;
        }

        return phase == 0;
    }

    private static void applyAnimalFollowing(
            ServerPlayer player
    ) {

        UUID playerId = player.getUUID();

        if (!SkillManager.isFarmingAnimalFollowEnabled(playerId)) {
            return;
        }

        boolean herdInstinct =
                SkillManager.hasFarmingPerk(
                        playerId,
                        "herd_instinct"
                );

        if (!herdInstinct) {
            return;
        }

        AABB area =
                player.getBoundingBox()
                        .inflate(HERD_RADIUS);

        for (Animal animal : player.serverLevel().getEntitiesOfClass(
                Animal.class,
                area,
                candidate ->
                        candidate.isAlive()
                                && candidate.distanceToSqr(player) > 6.25
        )) {

            animal.getLookControl().setLookAt(
                    player,
                    10.0f,
                    animal.getMaxHeadXRot()
            );
            animal.getNavigation().moveTo(
                    player,
                    1.2
            );
        }
    }
}
